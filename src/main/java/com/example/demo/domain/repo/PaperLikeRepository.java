package com.example.demo.domain.repo;

import com.example.demo.domain.entity.PaperLike;
import com.example.demo.domain.entity.PreviousPaper;
import com.example.demo.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaperLikeRepository extends JpaRepository<PaperLike, Long> {

    Optional<PaperLike> findByPaperAndUser(PreviousPaper paper, User user);

    long countByPaper(PreviousPaper paper);
}