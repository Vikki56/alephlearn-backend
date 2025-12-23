package com.example.demo.repo;

import com.example.demo.domain.ProfileLike;
import com.example.demo.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileLikeRepository extends JpaRepository<ProfileLike, Long> {

    long countByTargetUser(User targetUser);

    boolean existsByTargetUserAndLikedBy(User targetUser, User likedBy);

    Optional<ProfileLike> findByTargetUserAndLikedBy(User targetUser, User likedBy);
}