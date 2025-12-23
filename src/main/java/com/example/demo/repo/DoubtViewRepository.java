package com.example.demo.repo;

import com.example.demo.entity.Doubt;
import com.example.demo.entity.DoubtView;
import com.example.demo.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional; 

public interface DoubtViewRepository extends JpaRepository<DoubtView, Long> {

    Optional<DoubtView> findByDoubtAndViewer(Doubt doubt, User viewer);

    long countByDoubt(Doubt doubt);
}