package com.example.ArithmeticQuiz;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rules")
public class RuleController {
          
    private final RuleService ruleService;

    public RuleController(RuleService ruleService) {
        this.ruleService = ruleService;
    }

    // POST /api/rules
    @PostMapping
    public ResponseEntity<BusinessRule> createRule(@RequestBody BusinessRule rule) {
        BusinessRule created = ruleService.createRule(rule);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // GET /api/rules
    @GetMapping
    public ResponseEntity<List<BusinessRule>> getAllRules() {
        return ResponseEntity.ok(ruleService.getAllRules());
    }

    // GET /api/rules/{id}
    @GetMapping("/{id}")
    public ResponseEntity<BusinessRule> getRuleById(@PathVariable Long id) {
        return ResponseEntity.ok(ruleService.getRuleById(id));
    }

    // PUT /api/rules/{id}
    @PutMapping("/{id}")
    public ResponseEntity<BusinessRule> updateRule(@PathVariable Long id,@RequestBody BusinessRule rule) {
        return ResponseEntity.ok(ruleService.updateRule(id, rule));
    }

    // DELETE /api/rules/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRule(@PathVariable Long id) {
        ruleService.deleteRule(id);
        return ResponseEntity.noContent().build();
    }
}