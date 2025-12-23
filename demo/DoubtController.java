// package com.example.demo;

// import com.example.demo.entity.Doubt;
// // import com.example.demo.entity.User;
// import com.example.demo.service.DoubtService;
// import com.example.demo.user.User; 
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.PageRequest;
// import org.springframework.security.core.annotation.AuthenticationPrincipal;
// import org.springframework.web.bind.annotation.*;
// import com.example.demo.entity.DoubtAnswer;
// import java.util.List;
// import java.util.Map;
// @RestController
// @RequestMapping("/api/doubts")
// public class DoubtController {

//     private final DoubtService doubtService;

//     public DoubtController(DoubtService doubtService) {
//         this.doubtService = doubtService;
//     }

//     @PostMapping
//     public Doubt createDoubt(@RequestBody Doubt doubt,
//                              @AuthenticationPrincipal User user) {
//         doubt.setAsker(user);
//         return doubtService.createDoubt(doubt);
//     }

//     @GetMapping
//     public Page<Doubt> listDoubts(@RequestParam(defaultValue = "0") int page,
//                                   @RequestParam(defaultValue = "10") int size) {
//         return doubtService.listDoubts(PageRequest.of(page, size));
//     }

//     @GetMapping("/{id}")
//     public Doubt getDoubt(@PathVariable Long id,
//                           @AuthenticationPrincipal User user) {
//         return doubtService.getDoubt(id);
//     }
//     // ---------- Like a doubt ----------
// // ---------- Like a doubt ----------
// @PostMapping("/{id}/like")
// public Map<String, Long> likeDoubt(@PathVariable Long id,
//                                    @RequestBody LikeRequest req) {
//     return doubtService.likeDoubt(id, req.userId());
// }

// // ---------- Unlike a doubt ----------
// @DeleteMapping("/{id}/like")
// public Map<String, Long> unlikeDoubt(@PathVariable Long id,
//                                      @RequestBody LikeRequest req) {
//     return doubtService.unlikeDoubt(id, req.userId());
// }

// // DTO for like/unlike
// public record LikeRequest(Long userId) {}

// // ----------- Get all answers for a doubt -----------
// @GetMapping("/{id}/answers")
// public List<DoubtAnswer> getAnswers(@PathVariable Long id) {
//     return doubtService.getAnswersForDoubt(id);
// }

// // ----------- Add new answer -----------
// @PostMapping("/{id}/answers")
// public DoubtAnswer addAnswer(@PathVariable Long id,
//                              @RequestBody AnswerRequest req) {
//     return doubtService.addAnswer(id, req.userId(), req.body(), req.attachmentUrl());
// }

// // ----------- Edit answer -----------
// @PutMapping("/answers/{answerId}")
// public DoubtAnswer editAnswer(@PathVariable Long answerId,
//                               @RequestBody AnswerRequest req) {
//     return doubtService.editAnswer(answerId, req.userId(), req.body(), req.attachmentUrl());
// }
// // simple test for controller
// @GetMapping("/ping")
// public String ping() {
//     return "doubts-controller-alive";
// }

// // ----------- Accept answer -----------
// @PostMapping("/answers/{answerId}/accept")
// public DoubtAnswer acceptAnswer(@PathVariable Long answerId,
//                                 @RequestBody AcceptRequest req) {
//     return doubtService.acceptAnswer(answerId, req.userId());
// }

// // ----------- DTOs -----------
// public record AnswerRequest(Long userId, String body, String attachmentUrl) {}
// public record AcceptRequest(Long userId) {}
// }