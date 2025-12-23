package com.example.demo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import com.example.demo.repo.RoomKickRepository;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatHandler extends TextWebSocketHandler {

    private final MessageRepository repo;
    private final ObjectMapper mapper = new ObjectMapper();
    private final RoomKickRepository kicks;

    // NEW: room -> set of active user emails
// private final ConcurrentHashMap<String, Set<String>> activeUsers = new ConcurrentHashMap<>();
    // room -> sessions
    private final ConcurrentHashMap<String, Set<WebSocketSession>> rooms = new ConcurrentHashMap<>();

    // room -> (messageId -> (emoji -> set of users))
private final Map<String, Map<Long, Map<String, Set<String>>>> roomReactions = new ConcurrentHashMap<>();

// --- presence helpers (room -> active count) --------------------
// --- presence helpers: count unique users from sessions only -----------
private void broadcastActiveCount(String room) {
    if (room == null || room.isBlank()) return;

    // rooms = room -> Set<WebSocketSession>
    Set<WebSocketSession> sessions = rooms.getOrDefault(room, Collections.emptySet());
    Set<String> emails = new HashSet<>();

    for (WebSocketSession s : sessions) {
        Object em = s.getAttributes().get("userEmail");
        if (em == null) continue;

        String email = String.valueOf(em).trim().toLowerCase();
        if (!email.isBlank()) {
            emails.add(email); // same user in multiple tabs -> still 1
        }
    }

    int count = emails.size();

    Map<String, Object> evt = new LinkedHashMap<>();
    evt.put("type", "active-count");
    evt.put("room", room);
    evt.put("count", count);

    broadcast(room, evt);
}

// ab alag se map rakhne ki zaroorat nahi, ye NO-OP rehne do (ya hata do)
// private void markActive(String room, String email) { }
// private void markInactive(String room, String email) { }

private Map<Long, Map<String, Set<String>>> rxRoom(String room){
    return roomReactions.computeIfAbsent(room, r -> new ConcurrentHashMap<>());
}
private Map<String, Set<String>> rxMsg(Map<Long, Map<String, Set<String>>> byMsg, Long id){
    return byMsg.computeIfAbsent(id, k -> new ConcurrentHashMap<>());
}
private Set<String> rxUsers(Map<String, Set<String>> byEmoji, String emoji){
    return byEmoji.computeIfAbsent(emoji, k -> ConcurrentHashMap.newKeySet());
}

public ChatHandler(MessageRepository repo, RoomKickRepository kicks) {
    this.repo = repo;
    this.kicks = kicks;
}

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        final String payload = message.getPayload();

        Map<String, Object> msg;
        try {
            msg = mapper.readValue(payload, new TypeReference<Map<String, Object>>() {});
        } catch (Exception ex) {
            // Ignore non-JSON frames
            return;
        }

        final String type = str(msg.get("type"));
        if (type == null) return;

        // ---------- LEAVE ----------
        if ("leave".equals(type)) {
            final String leaveRoom = str(msg.get("room"));
            final String email = (String) session.getAttributes().get("userEmail"); // FIXED
        
            if (leaveRoom == null || leaveRoom.isBlank()) return;
        
            Set<WebSocketSession> oldSet = rooms.get(leaveRoom);
            if (oldSet != null) {
                oldSet.remove(session);
                if (oldSet.isEmpty()) rooms.remove(leaveRoom);
            }
        
            // markInactive(leaveRoom, email);
            session.getAttributes().remove("room");
        
            broadcastActiveCount(leaveRoom);
            return;
        }

        // ---------- JOIN ----------
// ---------- JOIN ----------
if ("join".equals(type)) {
    final String newRoom = str(msg.get("room"));
    if (newRoom == null || newRoom.isBlank()) return;

    // 1) Pehle session se lo (agar auth filter ne already daala ho)
    String email = str(session.getAttributes().get("userEmail"));

    // 2) Agar null/blank ho to frame se lo
    if (email == null || email.isBlank()) {
        String fromPayload = opt(str(msg.get("userEmail")), opt(str(msg.get("user")), "unknown"));
        email = (fromPayload != null ? fromPayload.trim().toLowerCase() : "unknown");
    } else {
        email = email.trim().toLowerCase();
    }

    session.getAttributes().put("userEmail", email);
// 🚫 if kicked -> reject join
String fullRoomKey = newRoom; // NOTE: client sends "subject/slug" already
if (isKickedInRoom(fullRoomKey, email)) {
    session.sendMessage(asText(Map.of(
        "type", "kicked",
        "room", fullRoomKey,
        "message", "You were removed from this room"
    )));
    try { session.close(CloseStatus.POLICY_VIOLATION); } catch (Exception ignored) {}
    return;
}
    // 1) Purane room se session + presence hatao
    String oldRoom = (String) session.getAttributes().get("room");
    if (oldRoom != null && !oldRoom.equals(newRoom)) {
        Set<WebSocketSession> oldSet = rooms.get(oldRoom);
        if (oldSet != null) {
            oldSet.remove(session);
            if (oldSet.isEmpty()) {
                rooms.remove(oldRoom);
            }
        }
        // markInactive(oldRoom, email);
        broadcastActiveCount(oldRoom);
    }

    // 2) Naye room me session + presence add karo
    rooms
        .computeIfAbsent(newRoom, k -> ConcurrentHashMap.newKeySet())
        .add(session);
    session.getAttributes().put("room", newRoom);
    // markActive(newRoom, email);

    // 3) Ack
    session.sendMessage(asText(Map.of("type", "join-ack", "room", newRoom)));

    // 4) Reaction snapshot
    Map<Long, Map<String, Set<String>>> byMsg = roomReactions.getOrDefault(newRoom, Map.of());
    List<Map<String, Object>> items = new ArrayList<>();
    for (Map.Entry<Long, Map<String, Set<String>>> e1 : byMsg.entrySet()) {
        Long msgId = e1.getKey();
        for (Map.Entry<String, Set<String>> e2 : e1.getValue().entrySet()) {
            String emoji = e2.getKey();
            Set<String> users = e2.getValue();
            if (users != null && !users.isEmpty()) {
                Map<String, Object> it = new LinkedHashMap<>();
                it.put("id", msgId);
                it.put("emoji", emoji);
                it.put("users", users);
                items.add(it);
            }
        }
    }
    Map<String, Object> snap = new LinkedHashMap<>();
    snap.put("type", "reaction-sync");
    snap.put("room", newRoom);
    snap.put("items", items);
    session.sendMessage(asText(snap));

    // 5) Naye room ka active-count
    broadcastActiveCount(newRoom);
    return;
}

        // From here on, we need a joined room
        final String room = (String) session.getAttributes().get("room");
        if (room == null || room.isBlank()) return;

        // ---------- EDIT ----------
        if ("edit".equals(type)) {
            Long id = asLong(msg.get("id"));
            String newText = str(msg.get("text"));
            String clientId = str(msg.get("clientId"));

            if (id != null && id > 0 && clientId != null) {
                repo.findById(id).ifPresent(m -> {
                    if (clientId.equals(m.getClientId()) && !m.isDeleted()) {
                        m.setText(newText != null ? newText : "");
                        m.setEditedAt(Instant.now());
                        repo.save(m);

                        Map<String, Object> evt = new LinkedHashMap<>();
                        evt.put("type", "edit");
                        evt.put("id", m.getId());
                        evt.put("text", m.getText());
                        evt.put("editedAt", m.getEditedAt() != null ? m.getEditedAt().toEpochMilli() : null);
                        evt.put("room", m.getRoom());
                        broadcast(m.getRoom(), evt);
                    }
                });
            }
            return;
        }

        // ---------- DELETE ----------
        if ("delete".equals(type)) {
            Long id = asLong(msg.get("id"));
            String clientId = str(msg.get("clientId"));

            if (id != null && id > 0 && clientId != null) {
                repo.findById(id).ifPresent(m -> {
                    if (clientId.equals(m.getClientId()) && !m.isDeleted()) {
                        m.setDeleted(true);
                        m.setText(""); // optional wipe
                        repo.save(m);

                        Map<String, Object> evt = new LinkedHashMap<>();
                        evt.put("type", "delete");
                        evt.put("id", m.getId());
                        evt.put("room", m.getRoom());
                        broadcast(m.getRoom(), evt);
                    }
                });
            }
            return;
        }

        // ---------- PIN / UNPIN ----------
        if ("pin".equals(type)) {
            Long id = asLong(msg.get("id"));
            Boolean pinned = asBoolean(msg.get("pinned")); // accepts true/false or "true"/"false"

            if (id != null && id > 0 && pinned != null) {
                repo.findById(id).ifPresent(m -> {
                    // (Optional) add authorization here if only certain users can pin
                    m.setPinned(pinned);
                    repo.save(m);

                    Map<String, Object> evt = new LinkedHashMap<>();
                    evt.put("type", "pin");
                    evt.put("id", m.getId());
                    evt.put("pinned", m.isPinned());
                    evt.put("room", m.getRoom());
                    evt.put("user", m.getUserName());
                    evt.put("text", m.getText());
                    evt.put("ts", m.getTs() != null ? m.getTs().toEpochMilli() : System.currentTimeMillis());
                    if (m.getReplyToId() != null) evt.put("replyToId", m.getReplyToId());
                    broadcast(m.getRoom(), evt);
                });
            }
            return;
        }

        // ---------- REACTION ----------
// if ("reaction".equals(type)) {
//     // message id to react to
//     Long id = asLong(msg.get("id"));
//     String emoji = str(msg.get("emoji"));   // 👍 ❤️ 😂 etc.
//     String user  = opt(str(msg.get("user")), "Anon");

//     // sanity check: need an id and emoji
//     if (id != null && id > 0 && emoji != null && !emoji.isBlank()) {
//         // For now we just broadcast to everyone in the same room.
//         // (Optional: persist in DB if you later add a Reaction entity/repo.)
//         Map<String, Object> evt = new LinkedHashMap<>();
//         evt.put("type",   "reaction");
//         evt.put("id",     id);
//         evt.put("emoji",  emoji);
//         evt.put("user",   user);
//         evt.put("room",   room);

//         broadcast(room, evt);
//     }
//     return;
// }
// ---------- REACTION ----------
// ---------- REACTION ----------
// Client sends: {type:"reaction", id, emoji, user}
// Server enforces: one reaction per user per message.
// If user taps same emoji again -> toggle off (remove).
// Server broadcasts events with {type:"reaction", id, emoji, user, added:true|false}
if ("reaction".equals(type)) {
    Long id    = asLong(msg.get("id"));
    String emoji = str(msg.get("emoji"));
    String user  = opt(str(msg.get("user")), "Anon");
    if (id == null || id <= 0 || emoji == null || emoji.isBlank()) return;

    Map<Long, Map<String, Set<String>>> byMsg = rxRoom(room);
    Map<String, Set<String>> byEmoji = rxMsg(byMsg, id);

    // 1) If user already has this emoji → toggle it off
    Set<String> cur = rxUsers(byEmoji, emoji);
    if (cur.contains(user)) {
        cur.remove(user);
        broadcast(room, Map.of(
            "type","reaction",
            "id", id,
            "emoji", emoji,
            "user", user,
            "added", false
        ));
        return;
    }

    // 2) Remove user from any other emoji on the same message
    for (Map.Entry<String, Set<String>> entry : byEmoji.entrySet()){
        String otherEmoji = entry.getKey();
        Set<String> users = entry.getValue();
        if (!otherEmoji.equals(emoji) && users.remove(user)) {
            broadcast(room, Map.of(
                "type","reaction",
                "id", id,
                "emoji", otherEmoji,
                "user", user,
                "added", false
            ));
        }
    }

    // 3) Add to the selected emoji
    cur.add(user);
    broadcast(room, Map.of(
        "type","reaction",
        "id", id,
        "emoji", emoji,
        "user", user,
        "added", true
    ));
    return;
}


        // ---------- MESSAGE ----------
        if ("message".equals(type)) {
            String email = str(session.getAttributes().get("userEmail"));
if (isKickedInRoom(room, email)) {
    // silently drop OR close session
    session.sendMessage(asText(Map.of(
        "type", "kicked",
        "room", room,
        "message", "You were removed from this room"
    )));
    try { session.close(CloseStatus.POLICY_VIOLATION); } catch (Exception ignored) {}
    return;
}
            String user     = opt(str(msg.get("user")), "Anon");
            String text     = opt(str(msg.get("text")), "");
            String clientId = opt(str(msg.get("clientId")), opt(str(msg.get("senderId")), null));
            Long ts         = asLong(msg.get("ts"));
            long tsMillis   = (ts != null ? ts : System.currentTimeMillis());

            // optional replyToId
            Long replyToId  = asLong(msg.get("replyToId"));

            Message entity = new Message();
            entity.setRoom(room);
            entity.setUserName(user);
            entity.setText(text);
            entity.setTs(Instant.ofEpochMilli(tsMillis));
            entity.setClientId(clientId);
            if (replyToId != null && replyToId > 0) {
                entity.setReplyToId(replyToId);
            }

            repo.save(entity);

            Map<String, Object> out = new LinkedHashMap<>();
            out.put("type", "message");
            out.put("id", entity.getId());
            out.put("room", room);
            out.put("user", entity.getUserName());
            out.put("text", entity.getText());
            out.put("ts", tsMillis);
            out.put("clientId", clientId == null ? "" : clientId);
            if (entity.getReplyToId() != null) {
                out.put("replyToId", entity.getReplyToId());
            }

            broadcast(room, out);
        }
    }

    public void forceDisconnectUserFromRoom(String room, String email) {
        if (room == null || room.isBlank() || email == null || email.isBlank()) return;
    
        String target = email.trim().toLowerCase();
        Set<WebSocketSession> sessions = rooms.getOrDefault(room, Collections.emptySet());
    
        // Copy to avoid ConcurrentModification while closing sessions
        List<WebSocketSession> snapshot = new ArrayList<>(sessions);
    
        for (WebSocketSession s : snapshot) {
            Object em = s.getAttributes().get("userEmail");
            if (em == null) continue;
    
            String se = String.valueOf(em).trim().toLowerCase();
            if (!target.equals(se)) continue;
    
            try {
                s.sendMessage(asText(Map.of(
                        "type", "kicked",
                        "room", room,
                        "message", "You were removed from this room"
                )));
            } catch (Exception ignored) {}
    
            try { s.close(CloseStatus.POLICY_VIOLATION); } catch (Exception ignored) {}
        }
    }
    public List<String> activeMembers(String room) {
        Set<WebSocketSession> sessions = rooms.getOrDefault(room, Collections.emptySet());
        Set<String> emails = new TreeSet<>();
        for (WebSocketSession s : sessions) {
            Object em = s.getAttributes().get("userEmail");
            if (em != null) {
                String e = String.valueOf(em).trim().toLowerCase();
                if (!e.isBlank()) emails.add(e);
            }
        }
        return new ArrayList<>(emails);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String room  = (String) session.getAttributes().get("room");
        String email = (String) session.getAttributes().get("userEmail");
    
        if (room != null) {
            Set<WebSocketSession> set = rooms.get(room);
            if (set != null) {
                set.remove(session);
                if (set.isEmpty()) {
                    rooms.remove(room);
                }
            }
            // markInactive(room, email);
            broadcastActiveCount(room);
        }
    }

    // ---------- helpers ----------

    private void broadcast(String room, Map<String, Object> event) {
        TextMessage frame = asText(event);
        for (WebSocketSession s : rooms.getOrDefault(room, ConcurrentHashMap.newKeySet())) {
            if (s.isOpen()) {
                try { s.sendMessage(frame); } catch (Exception ignored) {}
            }
        }
    }

    private TextMessage asText(Map<String, Object> m) {
        try {
            return new TextMessage(mapper.writeValueAsString(m));
        } catch (Exception e) {
            // Fallback: never throw in WS thread
            return new TextMessage("{\"type\":\"error\",\"message\":\"serialization\"}");
        }
    }

    private static String str(Object o) {
        return (o == null) ? null : String.valueOf(o);
    }

    private static String opt(String v, String def) {
        return (v == null || v.isBlank()) ? def : v;
    }

    private static Long asLong(Object o) {
        if (o == null) return null;
        if (o instanceof Number n) return n.longValue();
        try { return Long.parseLong(String.valueOf(o).replaceAll("[^0-9-]", "")); }
        catch (Exception e) { return null; }
    }

    private static Boolean asBoolean(Object o) {
        if (o == null) return null;
        if (o instanceof Boolean b) return b;
        String s = String.valueOf(o).trim().toLowerCase();
        if ("true".equals(s)) return true;
        if ("false".equals(s)) return false;
        return null; // invalid
    }

    private boolean isKickedInRoom(String room, String email) {
        if (room == null || email == null) return false;
    
        // room format: subject/slug  -> slug = doubt-q-48
        // extract questionId if slug matches
        String slug = room.contains("/") ? room.substring(room.indexOf("/") + 1) : room;
        if (!slug.startsWith("doubt-q-")) return false;
    
        try {
            Long qid = Long.parseLong(slug.replace("doubt-q-", "").replaceAll("[^0-9]", ""));
            return kicks.existsByQuestionIdAndKickedUserEmailIgnoreCase(qid, email.trim().toLowerCase());
        } catch (Exception e) {
            return false;
        }
    }
}
