package com.example.ArithmeticQuiz;

import java.math.BigDecimal;

/**
 * RuleTrigger
 * condition tied to a specific operation type (e.g., "addition and first operand == 5")
 */
public record RuleTrigger(
    BigDecimal value,
    TargetType target, // RESULT, OPERAND1, OPERAND2
    OperatorType operator, // EQUALS, NOT_EQUALS, GREATER, GREATER_OR_EQUALS, LESS, LESS_OR_EQUALS
    OperationType operation // ADD, SUBTRACT, MULTIPLY, DIVIDE
){}