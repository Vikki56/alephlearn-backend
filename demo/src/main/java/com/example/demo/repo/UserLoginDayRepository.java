package com.example.demo.repo;

import com.example.demo.domain.UserLoginDay;
import com.example.demo.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface UserLoginDayRepository extends JpaRepository<UserLoginDay, Long> {

    boolean existsByUserAndLoginDate(User user, LocalDate loginDate);

    long countByUserAndLoginDateBetween(User user, LocalDate start, LocalDate end);

    List<UserLoginDay> findByUserAndLoginDateBetween(User user, LocalDate start, LocalDate end);
}