package com.example.demo;
import com.example.demo.domain.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {

    Optional<Room> findBySubjectAndSlug(String subject, String slug);

    boolean existsBySubjectAndSlug(String subject, String slug);

    Page<Room> findBySubjectAndVisibilityAndTitleIgnoreCaseContainingOrderByLastActivityDesc(
            String subject,
            Room.Visibility visibility,
            String title,
            Pageable pageable
    );

    @Query("""
           select r from Room r
           where r.subject = :subject
             and r.visibility = :visibility
             and (:q is null or :q = '' or upper(r.title) like upper(concat('%', :q, '%')))
           order by r.lastActivity desc
           """)
    List<Room> search(
            @Param("subject") String subject,
            @Param("visibility") Room.Visibility visibility,
            @Param("q") String q
    );
    @Query("""
        select r from Room r
        where r.subject = :subject
          and lower(r.specialization) = :spec
          and (:q = '' or lower(r.title) like concat('%', :q, '%'))
        order by r.lastActivity desc
        """)
List<Room> searchByStream(
        @Param("subject") String subject,
        @Param("spec") String specialization,
        @Param("q") String query
);
}
