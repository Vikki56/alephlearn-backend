package com.example.demo.security;

import com.example.demo.user.UserRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwt;
    private final UserRepository users;

    public JwtAuthFilter(JwtService jwt, UserRepository users) {
        this.jwt = jwt;
        this.users = users;
    }

    private boolean isPublicPath(HttpServletRequest req) {
        String p = req.getRequestURI();
        return p.startsWith("/api/auth/")
                || p.startsWith("/swagger-ui")
                || p.equals("/swagger-ui.html")
                || p.startsWith("/v3/api-docs")
                || p.startsWith("/swagger-resources")
                || p.startsWith("/webjars")
                || p.startsWith("/h2-console")
                || p.equals("/error")
                || "OPTIONS".equalsIgnoreCase(req.getMethod());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
    
        if (isPublicPath(req)) {
            chain.doFilter(req, res);
            return;
        }
    
        String auth = req.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            chain.doFilter(req, res);
            return;
        }
    
        String token = auth.substring(7);
    
        try {
            var claims = jwt.parse(token).getBody();
            String email = claims.getSubject();
    
            var opt = users.findByEmail(email);
            if (opt.isEmpty()) {
                chain.doFilter(req, res);
                return;
            }
    
            var user = opt.get();
    
            // ✅ auto-unblock if expired
            if (user.isBlocked() && user.getBlockedUntil() != null && Instant.now().isAfter(user.getBlockedUntil())) {
                user.setBlocked(false);
                user.setBlockedUntil(null);
                user.setBlockReason(null);
                users.save(user);
            }
    
            // ✅ still blocked => STOP HERE
// ✅ still blocked => STOP HERE
if (user.isBlocked()) {
    res.setStatus(HttpServletResponse.SC_FORBIDDEN);
    res.setContentType("application/json");
    res.setCharacterEncoding("UTF-8");

    String until = (user.getBlockedUntil() == null) ? null : user.getBlockedUntil().toString();
    String reason = (user.getBlockReason() == null) ? "" : user.getBlockReason();

    String json =
        "{"
      + "\"code\":\"USER_BLOCKED\","
      + "\"blockedUntil\":" + (until == null ? "null" : ("\"" + until + "\"")) + ","
      + "\"adminEmail\":\"admin@alephlearn.com\","
      + "\"reason\":\"" + reason.replace("\"","\\\"") + "\""
      + "}";

    res.getWriter().write(json);
    return;
}
    
            String role = (user.getRole() != null) ? user.getRole().name() : "USER";
            var authToken = new UsernamePasswordAuthenticationToken(
                    user, null, List.of(new SimpleGrantedAuthority("ROLE_" + role))
            );
            SecurityContextHolder.getContext().setAuthentication(authToken);
    
        } catch (JwtException ignored) {
            // invalid token -> anonymous
        }
    
        chain.doFilter(req, res);
    }
}