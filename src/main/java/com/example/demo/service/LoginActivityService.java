package com.example.demo.service;

import com.example.demo.domain.UserLoginDay;
import com.example.demo.repo.UserLoginDayRepository;
import com.example.demo.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
public class LoginActivityService {

    private final UserLoginDayRepository loginDays;

    public LoginActivityService(UserLoginDayRepository loginDays) {
        this.loginDays = loginDays;
    }

    @Transactional
    public void recordLogin(User user) {
        if (user == null) return;

        LocalDate today = LocalDate.now(ZoneId.systemDefault());

        if (!loginDays.existsByUserAndLoginDate(user, today)) {
            UserLoginDay d = new UserLoginDay();
            d.setUser(user);
            d.setLoginDate(today);
            loginDays.save(d);
        }
    }

    public long countActiveDaysThisYear(User user) {
        if (user == null) return 0L;

        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        LocalDate startOfYear = today.withDayOfYear(1);

        return loginDays.countByUserAndLoginDateBetween(user, startOfYear, today);
    }

    public List<UserLoginDay> getRecentDays(User user, int daysBack) {
        if (user == null) return List.of();

        LocalDate end = LocalDate.now(ZoneId.systemDefault());
        LocalDate start = end.minusDays(daysBack - 1L);

        return loginDays.findByUserAndLoginDateBetween(user, start, end);
    }
    public List<LocalDate> getLoginDatesThisYear(User user) {
        if (user == null) return List.of();

        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        LocalDate startOfYear = today.withDayOfYear(1);


        return loginDays.findByUserAndLoginDateBetween(user, startOfYear, today)
                .stream()
                .map(UserLoginDay::getLoginDate)   
                .distinct()
                .sorted()
                .toList();
    }
}