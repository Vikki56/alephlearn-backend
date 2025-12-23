package com.example.demo.service;

import com.example.demo.domain.AcademicProfile;
import com.example.demo.domain.dto.paper.PaperContributorDto;
import com.example.demo.domain.dto.paper.PreviousPaperDto;
import com.example.demo.domain.entity.PaperLike;
import com.example.demo.domain.entity.PreviousPaper;
import com.example.demo.domain.repo.PaperLikeRepository;
import com.example.demo.domain.repo.PreviousPaperRepository;
import com.example.demo.repository.AcademicProfileRepository;
import com.example.demo.security.AuthUser;
import com.example.demo.user.User;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)  // default: all methods read-only
public class PreviousPaperService {

    private static final Path PAPERS_ROOT =
            Paths.get("uploads", "papers").toAbsolutePath().normalize();

    private final PreviousPaperRepository paperRepository;
    private final PaperLikeRepository paperLikeRepository;
    private final AcademicProfileRepository academicProfileRepository;

    public PreviousPaperService(PreviousPaperRepository paperRepository,
                                PaperLikeRepository paperLikeRepository,
                                AcademicProfileRepository academicProfileRepository) {
        this.paperRepository = paperRepository;
        this.paperLikeRepository = paperLikeRepository;
        this.academicProfileRepository = academicProfileRepository;
        initFolder();
    }

    private void initFolder() {
        try {
            Files.createDirectories(PAPERS_ROOT);
        } catch (IOException e) {
            throw new RuntimeException("Failed to create papers upload directory", e);
        }
    }

    private User getCurrentUserOrNull() {
        return AuthUser.current();
    }

    private User getCurrentUserOrThrow() {
        User u = AuthUser.current();
        if (u == null) {
            throw new IllegalStateException("No authenticated user");
        }
        return u;
    }

    // --- STREAM KEY HELPERS ---

    private String buildStreamKeyFromProfile(AcademicProfile profile) {
        if (profile == null) return "global";

        String level = profile.getEducationLevel() != null
                ? profile.getEducationLevel().toLowerCase().replaceAll("[^a-z0-9]", "")
                : "unknown";

        String spec = profile.getSpecialization() != null
                ? profile.getSpecialization().toLowerCase().replaceAll("[^a-z0-9]", "")
                : "general";

        // e.g. btech_cse, class12_pcm etc.
        return level + "_" + spec;
    }

    private String resolveStreamKeyForUser(User user) {
        if (user == null) return "global";

        return academicProfileRepository.findByUser(user)
                .map(this::buildStreamKeyFromProfile)
                .orElse("global");
    }

    private PreviousPaperDto toDto(PreviousPaper p) {
        String uploaderName = (p.getUploadedBy() != null)
                ? p.getUploadedBy().getName()
                : "Unknown";

        User current = getCurrentUserOrNull();

        boolean ownedByMe = current != null
                && p.getUploadedBy() != null
                && p.getUploadedBy().getId().equals(current.getId());

        boolean likedByMe = false;
        if (current != null && p.getLikesDetails() != null) {
            likedByMe = p.getLikesDetails().stream()
                    .anyMatch(like -> like.getUser() != null
                            && like.getUser().getId().equals(current.getId()));
        }

        return new PreviousPaperDto(
                p.getId(),
                p.getCollegeName(),
                p.getSubjectName(),
                p.getExamYear(),
                p.getExamType(),
                uploaderName,
                p.getDownloads(),
                p.getLikes(),
                p.getCreatedAt(),
                ownedByMe,
                likedByMe,
                p.getStudentYear()
                
        );
    }

    // ---------- WRITE METHODS: override readOnly flag ----------

    @Transactional
    public PreviousPaperDto uploadPaper(
            String collegeName,
            String subjectName,
            Integer examYear,
            String examType,
            Integer studentYear,
            MultipartFile file
    ) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is required");
        }

        User currentUser = getCurrentUserOrThrow();

        String trimmedCollege = collegeName.trim();
        String trimmedSubject = subjectName.trim();
        String trimmedType = examType.trim();

        // ⭐ streamKey from academic profile
        String streamKey = resolveStreamKeyForUser(currentUser);

        // ⭐ duplicate check (same college+subject+year+type+stream)
        boolean exists = paperRepository
                .existsByCollegeNameIgnoreCaseAndSubjectNameIgnoreCaseAndExamYearAndExamTypeIgnoreCaseAndStreamKeyIgnoreCase(
                        trimmedCollege,
                        trimmedSubject,
                        examYear,
                        trimmedType,
                        streamKey
                );

        if (exists) {
            throw new IllegalArgumentException("This paper already exists for your stream (same college, subject, year & type).");
        }

        String originalName = StringUtils.cleanPath(file.getOriginalFilename());
        String ext = "";
        int dotIndex = originalName.lastIndexOf('.');
        if (dotIndex != -1) {
            ext = originalName.substring(dotIndex);
        }

        String randomName = UUID.randomUUID().toString().replace("-", "") + ext;
        Path targetPath = PAPERS_ROOT.resolve(randomName);

        Files.copy(file.getInputStream(), targetPath);

        PreviousPaper paper = new PreviousPaper();
        paper.setCollegeName(trimmedCollege);
        paper.setSubjectName(trimmedSubject);
        paper.setExamYear(examYear);
        paper.setExamType(trimmedType);
        paper.setStreamKey(streamKey);   // ⭐ important
        paper.setStudentYear(studentYear);
        paper.setOriginalFileName(originalName);
        paper.setStoredFileName(randomName);
        paper.setContentType(file.getContentType() != null ? file.getContentType() : "application/octet-stream");
        paper.setFileSizeBytes(file.getSize());
        paper.setUploadedBy(currentUser);
        paper.setCreatedAt(Instant.now());

        PreviousPaper saved = paperRepository.save(paper);
        return toDto(saved);
    }

    // ---------- READ METHODS ----------

    public PreviousPaperDto getPaper(Long id) {
        PreviousPaper paper = paperRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Paper not found"));
        return toDto(paper);
    }

    public Resource getFileForView(Long id) {
        PreviousPaper paper = paperRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Paper not found"));
        Path path = PAPERS_ROOT.resolve(paper.getStoredFileName());
        return new FileSystemResource(path);
    }

    @Transactional
    public Resource getFileForDownload(Long id) {
        PreviousPaper paper = paperRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Paper not found"));
        paper.setDownloads(paper.getDownloads() + 1);
        paperRepository.save(paper);

        Path path = PAPERS_ROOT.resolve(paper.getStoredFileName());
        return new FileSystemResource(path);
    }

    @Transactional
    public PreviousPaperDto toggleLike(Long id) {
        PreviousPaper paper = paperRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Paper not found"));

        User currentUser = getCurrentUserOrThrow();

        Optional<PaperLike> existing = paperLikeRepository.findByPaperAndUser(paper, currentUser);

        if (existing.isPresent()) {
            // already liked -> unlike
            paperLikeRepository.delete(existing.get());
        } else {
            PaperLike like = new PaperLike();
            like.setPaper(paper);
            like.setUser(currentUser);
            paperLikeRepository.save(like);
        }

        long count = paperLikeRepository.countByPaper(paper);
        paper.setLikes(count);
        PreviousPaper saved = paperRepository.save(paper);

        return toDto(saved);
    }

    @Transactional
    public void deletePaper(Long id) throws IOException {
        PreviousPaper paper = paperRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Paper not found"));

        User currentUser = getCurrentUserOrThrow();
        if (paper.getUploadedBy() == null ||
                !paper.getUploadedBy().getId().equals(currentUser.getId())) {
            throw new SecurityException("Only uploader can delete this paper");
        }

        Path path = PAPERS_ROOT.resolve(paper.getStoredFileName());
        Files.deleteIfExists(path);

        paperRepository.delete(paper);
    }

    public List<PaperContributorDto> getTopContributors() {
        return paperRepository.findTopContributors();
    }

    // ---------- LIST + SCOPE + SEARCH + SORT ----------

    /**
     * scope:
     *  - "my"     => user stream + global  (default)
     *  - "stream" => only user stream
     *  - "all"    => only global
     *
     * sort:
     *  - "popular" => downloads desc
     *  - anything else => recent (createdAt desc)
     */
    public List<PreviousPaperDto> listFilteredPapers(String search,
                                                     String sort,
                                                     String scope,
                                                     String streamKey) {

        Sort sortObj = "popular".equalsIgnoreCase(sort)
                ? Sort.by(Sort.Direction.DESC, "downloads")
                : Sort.by(Sort.Direction.DESC, "createdAt");

        // sab papers pehle sort ke saath
        List<PreviousPaper> all = paperRepository.findAll(sortObj);

        // effective streamKey
        String effectiveStreamKey = streamKey;
        if (effectiveStreamKey == null || effectiveStreamKey.isBlank()) {
            User currentUser = getCurrentUserOrNull();
            if (currentUser != null) {
                effectiveStreamKey = resolveStreamKeyForUser(currentUser);
            }
        }
        if (effectiveStreamKey == null || effectiveStreamKey.isBlank()) {
            effectiveStreamKey = "global";
        }
        String keyLower = effectiveStreamKey.toLowerCase();

        if (scope == null || scope.isBlank()) {
            scope = "my";
        }

        List<PreviousPaper> base;

        if ("stream".equalsIgnoreCase(scope)) {
            // STREAM chip => sirf apna stream
            base = all.stream()
                    .filter(p -> {
                        String pk = p.getStreamKey() != null ? p.getStreamKey().toLowerCase() : "";
                        return pk.equals(keyLower);
                    })
                    .toList();
        
        } else if ("all".equalsIgnoreCase(scope)) {
            // 🔥 ALL Papers => sab papers (global + sabhi streams)
            // yaha koi streamKey filter nahi, directly 'all' list use karo
            base = all;
        
        } else {
            // "my" (default) => global + current user ka stream
            base = all.stream()
                    .filter(p -> {
                        String pk = p.getStreamKey() != null ? p.getStreamKey().toLowerCase() : "";
                        return pk.equals("global") || pk.equals(keyLower);
                    })
                    .toList();
        }

        // search hamesha current scope ke andar
        if (search != null && !search.isBlank()) {
            String s = search.toLowerCase();
            base = base.stream()
                    .filter(p ->
                            (p.getSubjectName() != null && p.getSubjectName().toLowerCase().contains(s)) ||
                            (p.getCollegeName() != null && p.getCollegeName().toLowerCase().contains(s)) ||
                            (p.getExamYear() != null && String.valueOf(p.getExamYear()).contains(s))
                    )
                    .toList();
        }

        return base.stream()
                .map(this::toDto)
                .toList();
    }
}