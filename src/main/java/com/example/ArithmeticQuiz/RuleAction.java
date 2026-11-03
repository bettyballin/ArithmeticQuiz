package com.example.ArithmeticQuiz;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;

/**
 * RuleAction
 * Modification to applyr when the trigger matches (e.g., "add 1 to the result")
 */
public record RuleAction(
    @NotNull BigDecimal value,
    @NotNull TargetType target, // RESULT, OPERAND1, OPERAND2
    @NotNull OperationType operator // ADD, SUBTRACT, MULTIPLY, DIVIDE
){}
