package com.example.ArithmeticQuiz;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BusinessRepository extends JpaRepository<BusinessRule, Long> {
    List<BusinessRule> findByActiveTrue();
}