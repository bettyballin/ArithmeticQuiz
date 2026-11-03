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
        // first: validate the operation type
        OperationType operationType;
        try {
            operationType = OperationType.valueOf(op.toUpperCase());
            if(operationType == OperationType.ANY) {
                throw new IllegalArgumentException(
                        "ANY cannot be used as a calculation operation");            
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid operation type: " + op);
        }

        // Perform the arithmetic operation
        BigDecimal operand1 = a;
        BigDecimal operand2 = b;
        BigDecimal result = calculate(operationType, operand1, operand2);

        // Retrieve applicable business rules, check if they are triggered, and apply them to the result
        for (BusinessRule rule : ruleService.getRules(operationType)) {
            logger.info("Evaluating rule: {}", rule.getName());

            boolean triggered = isTriggered(operand1, operand2, result, rule);
            logger.info("Rule triggered: {}", triggered);
            if (!triggered) {
                continue;
            }

            RuleAction action = rule.getAction();
            logger.info("Applying action: {} {} to {}", action.operator(),action.value(), action.target());

            // Apply action to the correct target
            switch (action.target()) {
                case OPERAND1:
                    operand1 = calculate(action.operator(), operand1, action.value());
                    break;
                case OPERAND2:
                    operand2 = calculate(action.operator(), operand2, action.value());
                    break;
                case RESULT:
                    result = calculate(action.operator(), result, action.value());
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported action target: "+ action.target());
            }

            // If an operand changed, recalculate the result
            if (action.target() == TargetType.OPERAND1 || action.target() == TargetType.OPERAND2) {
                result = calculate(operationType,operand1,operand2);
                logger.info("Recalculated result: {}", result);
            }       
        }
        return result.setScale(2, RoundingMode.HALF_UP); // Round the result to 2 decimal places
    }

   
    // Perform the arithmetic operation based on the operation type
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