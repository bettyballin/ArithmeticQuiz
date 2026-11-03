package com.example.ArithmeticQuiz;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;

/**
 * RuleTrigger
 * condition tied to a specific operation type (e.g., "addition and first operand == 5")
 */
public record RuleTrigger(
    @NotNull BigDecimal value,
    @NotNull TargetType target, // RESULT, OPERAND1, OPERAND2
    @NotNull OperatorType operator, // EQUALS, NOT_EQUALS, GREATER, GREATER_OR_EQUALS, LESS, LESS_OR_EQUALS
    @NotNull OperationType operation // ADD, SUBTRACT, MULTIPLY, DIVIDE
){}