package com.example.ArithmeticQuiz;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RuleService {
    private final BusinessRepository businessRepository;

    public RuleService(BusinessRepository businessRepository) {
        this.businessRepository = businessRepository;
    }

    // GET: Retrieve all rules
    public List<BusinessRule> getAllRules() {
        return businessRepository.findAll();
    }
    // GET: Retrieve rule by ID
    public BusinessRule getRuleById(Long id) {
        return businessRepository.findById(id)
                .orElseThrow(() ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Rule with ID " + id + " not found"
                    )
                );
    }
    // GET: Retrieve all active rules
    public List<BusinessRule> getRules(OperationType operation) {
        List<BusinessRule> rules = new ArrayList<BusinessRule>();
        for (BusinessRule rule : businessRepository.findByActiveTrueOrderByIdAsc()) {
            if (rule.getTrigger().operation() == operation || rule.getTrigger().operation() == OperationType.ANY) {
                rules.add(rule);
            }
        }
        return rules;
    }

    // POST: Create a new rule
    public BusinessRule createRule(BusinessRule rule) {
        return businessRepository.save(rule);
    }

    // PUT: Update an existing rule
    public BusinessRule updateRule(Long id,BusinessRule updatedRule) {
        BusinessRule existingRule = getRuleById(id);
        existingRule.setName(updatedRule.getName());
        existingRule.setActive(updatedRule.isActive());
        existingRule.setTrigger(updatedRule.getTrigger());
        existingRule.setAction(updatedRule.getAction());
        return businessRepository.save(existingRule);
    }

    // DELETE: Delete an existing rule
    public void deleteRule(Long id) {
        BusinessRule rule = getRuleById(id);
        businessRepository.delete(rule);
    }
}
