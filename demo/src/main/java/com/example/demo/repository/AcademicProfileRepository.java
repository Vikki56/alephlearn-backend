package com.example.demo.repository;

import com.example.demo.domain.AcademicProfile;
import com.example.demo.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AcademicProfileRepository extends JpaRepository<AcademicProfile, Long> {

    Optional<AcademicProfile> findByUser(User user);

    boolean existsByUser(User user);
}