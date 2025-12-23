package com.example.demo.domain.repo;

import com.example.demo.domain.dto.paper.PaperContributorDto;
import com.example.demo.domain.entity.PreviousPaper;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PreviousPaperRepository extends JpaRepository<PreviousPaper, Long> {

    default List<PreviousPaper> findAllSorted(String sortMode) {
        Sort sort;
        if ("popular".equalsIgnoreCase(sortMode)) {
            sort = Sort.by(Sort.Direction.DESC, "downloads");
        } else {
            sort = Sort.by(Sort.Direction.DESC, "createdAt");
        }
        return findAll(sort);
    }

    @Query("""
        select new com.example.demo.domain.dto.paper.PaperContributorDto(
            p.uploadedBy.name,
            count(p)
        )
        from PreviousPaper p
        where p.uploadedBy is not null
        group by p.uploadedBy.name
        order by count(p) desc
        """)
    List<PaperContributorDto> findTopContributors();

    // Filter by exact stream if needed
    List<PreviousPaper> findByStreamKeyIgnoreCase(String streamKey, Sort sort);

    // ⭐ NEW: duplicate check including streamKey
    boolean existsByCollegeNameIgnoreCaseAndSubjectNameIgnoreCaseAndExamYearAndExamTypeIgnoreCaseAndStreamKeyIgnoreCase(
            String collegeName,
            String subjectName,
            Integer examYear,
            String examType,
            String streamKey
    );
}