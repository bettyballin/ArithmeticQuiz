package com.example.ArithmeticQuiz;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MathOperation {

    private static final Logger logger =
        LoggerFactory.getLogger(MathOperation.class);
        
    private final RuleService ruleService;

    public MathOperation(RuleService ruleService) {
        this.ruleService = ruleService;
    }

    // Perform the specified arithmetic operation on the two operands, 
    // apply any applicable business rules, and return the final result
    public BigDecimal doOperation(String op, BigDecimal a, BigDecimal b) {
        // First: validate the operation type
        OperationType operationType;
        try {
            operationType = OperationType.valueOf(op.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid operation type: " + op);
        }
        if (operationType == OperationType.ANY) {
            throw new IllegalArgumentException("ANY cannot be used as a calculation operation");
        }

        BigDecimal operand1 = a;
        BigDecimal operand2 = b;
        List<BusinessRule> rules = ruleService.getRules(operationType);

        // Modify operands first
        // Evaluate each rule and apply the action if the trigger condition is met
        for (BusinessRule rule : rules) {
            logger.info("Evaluating rule: {}", rule.getName());
            RuleAction action = rule.getAction();
            if (action.target() == TargetType.RESULT) {
                continue;
            }
            BigDecimal triggerValue = rule.getTrigger().target() == TargetType.RESULT
                    ? calculate(operationType, operand1, operand2)
                    : null;
            if (!isTriggered(operand1, operand2, triggerValue, rule)) {
                continue;
            }
            logger.info("Pre-calculating rule '{}': {} {} on {}", rule.getName(), action.operator(), action.value(), action.target());
            if (action.target() == TargetType.OPERAND1) {
                // let rule action modify operand1
                operand1 = calculate(action.operator(), operand1, action.value());
            } else { // or operand2
                operand2 = calculate(action.operator(), operand2, action.value());
            }
        }
        // Perform the arithmetic operation and get the result
        BigDecimal result = calculate(operationType, operand1, operand2);
        logger.info("Performed operation {} on operands {} and {}, result: {}", operationType, operand1, operand2, result);

        // Loop through the rules again to apply any actions that target the result
        for (BusinessRule rule : rules) {
            RuleAction action = rule.getAction();
            if (action.target() != TargetType.RESULT || !isTriggered(operand1, operand2, result, rule)) {
                continue;
            }
            logger.info("Applying rule '{}': {} {} on RESULT", rule.getName(), action.operator(), action.value());
            result = calculate(action.operator(), result, action.value());
        }
        return result.setScale(2, RoundingMode.HALF_UP); // Round the result to 2 decimal places
    }
   
    // Perform the arithmetic operation based on the operation type
    public BigDecimal calculate(OperationType op, BigDecimal a, BigDecimal b) {
        switch (op) {
            case ADD:
                return a.add(b);
            case SUBTRACT:
                return a.subtract(b);
            case MULTIPLY:
                return a.multiply(b);
            case DIVIDE:
                if (b.compareTo(BigDecimal.ZERO) == 0) {
                    throw new IllegalArgumentException("Division by zero is not allowed.");
                }
                return a.divide(b, 10, RoundingMode.HALF_UP);
            case SET:
                return b;
            default:
                throw new IllegalArgumentException("Unsupported operation: " + op);
        }
    }

    // Check if the rule is triggered based on the rule trigger and the operation values
    private boolean isTriggered(BigDecimal a, BigDecimal b, BigDecimal result, BusinessRule rule) {
        BigDecimal value;
        switch (rule.getTrigger().target()) {
            case OPERAND1:
                value = a;
                break;
            case OPERAND2:
                value = b;
                break;
            case RESULT:
                value = result;
                break;
            default:
                throw new IllegalArgumentException("Unsupported target type: " + rule.getTrigger().target());
        }        
        switch (rule.getTrigger().operator()) {
            case EQUALS:
                return value.compareTo(rule.getTrigger().value()) == 0;
            case NOT_EQUALS:
                return value.compareTo(rule.getTrigger().value()) != 0;
            case GREATER:
                return value.compareTo(rule.getTrigger().value()) > 0;
            case GREATER_OR_EQUALS:
                return value.compareTo(rule.getTrigger().value()) >= 0;
            case LESS:
                return value.compareTo(rule.getTrigger().value()) < 0;
            case LESS_OR_EQUALS:
                return value.compareTo(rule.getTrigger().value()) <= 0;
            default:
                throw new IllegalArgumentException("Unsupported operator type: " + rule.getTrigger().operator());
        }
    }
    
}