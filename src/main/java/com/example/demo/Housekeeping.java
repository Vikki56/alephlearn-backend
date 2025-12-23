package com.example.demo;

import com.example.demo.domain.Claim;
import com.example.demo.repo.ClaimRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@EnableScheduling
@Component
@RequiredArgsConstructor
public class Housekeeping {

    private final ClaimRepository claimRepo;

    @Scheduled(fixedRate = 60_000)
    public void clearExpiredClaims() {
        List<Claim> all = claimRepo.findAll();
        Instant now = Instant.now();
        for (Claim c : all) {
            if (c.getExpiresAt() != null && c.getExpiresAt().isBefore(now)) {
                claimRepo.delete(c);
            }
        }
    }
}
