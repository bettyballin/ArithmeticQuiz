package com.example.ArithmeticQuiz;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class RuleService {
    private final BusinessRepository businessRepository;

    public RuleService(BusinessRepository businessRepository) {
        this.businessRepository = businessRepository;
    }

    public List<BusinessRule> getRules(OperationType operation) {
        List<BusinessRule> rules = new ArrayList<BusinessRule>();
        for (BusinessRule rule : businessRepository.findByActiveTrue()) {
            if (rule.getTrigger().operation() == operation || rule.getTrigger().operation() == OperationType.ANY) {
                rules.add(rule);
            }
        }
        return rules;
    }
}
