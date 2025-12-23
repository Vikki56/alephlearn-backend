package com.example.demo.api;

import com.example.demo.domain.dto.paper.PaperContributorDto;
import com.example.demo.domain.dto.paper.PreviousPaperDto;
import com.example.demo.service.PreviousPaperService;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/papers")
public class PreviousPaperController {

    private final PreviousPaperService paperService;

    public PreviousPaperController(PreviousPaperService paperService) {
        this.paperService = paperService;
    }

    // ---------- Upload a paper ----------
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PreviousPaperDto> uploadPaper(
            @RequestParam String collegeName,
            @RequestParam String subjectName,
            @RequestParam Integer examYear,
            @RequestParam String examType,
            @RequestParam Integer studentYear,
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        PreviousPaperDto dto = paperService.uploadPaper(
                collegeName,
                subjectName,
                examYear,
                examType,
                studentYear,
                file
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    // ---------- List + search + sort ----------
    @GetMapping
    public ResponseEntity<List<PreviousPaperDto>> listPapers(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "sort", required = false, defaultValue = "recent") String sort,
            @RequestParam(value = "scope", required = false, defaultValue = "my") String scope,
            @RequestParam(value = "streamKey", required = false) String streamKey
    ) {
        List<PreviousPaperDto> list = paperService.listFilteredPapers(search, sort, scope, streamKey);
        return ResponseEntity.ok(list);
    }

    // ---------- Get single paper meta (not file) ----------
    @GetMapping("/{id}")
    public ResponseEntity<PreviousPaperDto> getPaper(@PathVariable Long id) {
        return ResponseEntity.ok(paperService.getPaper(id));
    }

    // ---------- View file (inline) ----------
    @GetMapping("/{id}/view")
    public ResponseEntity<Resource> viewPaper(@PathVariable Long id) {
        Resource file = paperService.getFileForView(id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .body(file);
    }

    // ---------- Download file (attachment, +count) ----------
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadPaper(@PathVariable Long id) {
        Resource file = paperService.getFileForDownload(id);

        String filename = "paper-" + id + ".pdf";
        String encoded = URLEncoder.encode(filename, StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(
                ContentDisposition.attachment().filename(encoded).build()
        );

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(file);
    }

    // ---------- Like ----------
// ---------- Like (toggle) ----------
@PostMapping("/{id}/like")
public ResponseEntity<PreviousPaperDto> likePaper(@PathVariable Long id) {
    return ResponseEntity.ok(paperService.toggleLike(id));
}

    // ---------- Delete (only uploader allowed by service) ----------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePaper(@PathVariable Long id) throws IOException {
        paperService.deletePaper(id);
        return ResponseEntity.noContent().build();
    }

    // ---------- NEW: Top Contributors ----------
    @GetMapping("/contributors")
    public ResponseEntity<List<PaperContributorDto>> topContributors() {
        return ResponseEntity.ok(paperService.getTopContributors());
    }
}