package com.example.demo.repo;

import com.example.demo.domain.UserInterest;
import com.example.demo.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface UserInterestRepository extends JpaRepository<UserInterest, Long> {

    java.util.List<UserInterest> findByUserOrderByCreatedAtAsc(User user);

    @Modifying
    @Transactional
    @Query("DELETE FROM UserInterest ui WHERE ui.user = :user")
    void deleteByUser(User user);
}