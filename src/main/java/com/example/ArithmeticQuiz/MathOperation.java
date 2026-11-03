package com.example.ArithmeticQuiz;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    public BigDecimal doOperation(String op, BigDecimal a, BigDecimal b) {
        OperationType operationType;
        try {
            operationType = OperationType.valueOf(op.toUpperCase());
            if(operationType == OperationType.ANY) {
                logger.info("Operation type is ANY, defaulting to ADD operation.");
                operationType = OperationType.ADD; // Default behavior for ANY operation
            }
        } catch (IllegalArgumentException e) {
            logger.error("Invalid operation type: {}", op);
            throw new IllegalArgumentException("Invalid operation type: " + op);
        }
        BigDecimal result = calculate(operationType, a, b);
        for (BusinessRule rule : ruleService.getRules(operationType)) {
            logger.info("Retrieved rule for operation {}: {} with trigger {} and action {}", op, rule.getTrigger().operation(), rule.getTrigger().operation(), rule.getAction().operator());
            logger.info("Rule triggered: {}", isTriggered(a, b, result, rule));
            if (isTriggered(a, b, result, rule)) {
                result = applyRuleAction(a, b, result, rule);
            }
        }
        return result.setScale(2, RoundingMode.HALF_UP); // Round the result to 2 decimal places
    }

    private BigDecimal calculate(OperationType op, BigDecimal a, BigDecimal b) {
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
            case ANY:
                return a.add(b); // Default behavior for ANY operation, can be customized as needed
            default:
                logger.error("Unsupported operation: {}", op);
                throw new IllegalArgumentException("Unsupported operation: " + op);
        }
    }

    private boolean isTriggered(BigDecimal a, BigDecimal b, BigDecimal result, BusinessRule rule) {
        BigDecimal value = getTarget(rule.getTrigger().target(), a, b, result);
        switch (rule.getTrigger().operator()) {
            case EQUALS:
                return value.compareTo(rule.getTrigger().value()) == 0;
            case NOT_EQUALS:
                return value.compareTo(rule.getTrigger().value()) != 0;
            case GREATER:
                return value.compareTo(rule.getTrigger().value()) > 0;
            case GREATER_OR_EQUALS:
                return value.compareTo(rule.getTrigger().value()) > 0;
            case LESS:
                return value.compareTo(rule.getTrigger().value()) < 0;
            case LESS_OR_EQUALS:
                return value.compareTo(rule.getTrigger().value()) < 0;
            default:
                logger.error("Unsupported operator type: {}", rule.getTrigger().operator());
                throw new IllegalArgumentException("Unsupported operator type: " + rule.getTrigger().operator());
        }
    }

    private BigDecimal applyRuleAction(BigDecimal a, BigDecimal b, BigDecimal result, BusinessRule rule) {
        BigDecimal value = getTarget(rule.getAction().target(), a, b, result);
        logger.info("Applying action to value: {} {}", rule.getAction().operator(), value);
        return calculate(rule.getAction().operator(), rule.getAction().value(), value); // Return the modified result (if applicable)
    }

    private BigDecimal getTarget(TargetType target, BigDecimal a, BigDecimal b, BigDecimal result) {
        switch (target) {
            case OPERAND1:
                return a;
            case OPERAND2:
                return b;
            case RESULT:
                return result;
            default:
                logger.error("Unsupported target type: {}", target);
                throw new IllegalArgumentException("Unsupported target type: " + target);
        }
    }
}