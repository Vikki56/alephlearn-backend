package com.example.demo.repo;

import com.example.demo.entity.Doubt;
import com.example.demo.entity.DoubtLike;
// import com.example.demo.entity.User;

import com.example.demo.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DoubtLikeRepository extends JpaRepository<DoubtLike, Long> {

    Optional<DoubtLike> findByDoubtAndUser(Doubt doubt, User user);

    long countByDoubt(Doubt doubt);
}