package com.example.ArithmeticQuiz;

import java.math.BigDecimal;

/**
 * RuleAction
 * Modification to applyr when the trigger matches (e.g., "add 1 to the result")
 */
public record RuleAction(
    BigDecimal value,
    TargetType target, // RESULT, OPERAND1, OPERAND2
    OperationType operator // ADD, SUBTRACT, MULTIPLY, DIVIDE
){}
