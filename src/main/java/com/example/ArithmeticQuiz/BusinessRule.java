package com.example.ArithmeticQuiz;

import java.time.LocalDateTime;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.*;

@Entity
@Table(name = "business_rules")
public class BusinessRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private RuleTrigger trigger;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private RuleAction action;

    private boolean active;

    private LocalDateTime createdAt = LocalDateTime.now();

    // Getters and setters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public RuleTrigger getTrigger() {
        return trigger;
    }
    public void setTrigger(RuleTrigger trigger) {
        this.trigger = trigger;
    }

    public RuleAction getAction() {
        return action;
    }
    public void setAction(RuleAction action) {
        this.action = action;
    }

    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}