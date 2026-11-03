
package com.example.ArithmeticQuiz;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArithmeticQuizApplicationTests {

    @Mock
    private RuleService ruleService;

    @InjectMocks
    private MathOperation mathOperation;

    @BeforeEach
    void setUp() {
        when(ruleService.getRules(any(OperationType.class))).thenReturn(List.of());
    }

    /*
        Basic arithmetic operations 
    */

    @Test
    void doAddNumbers() {
        assertEquals(bd("15.00"), mathOperation.doOperation("ADD", bd("5"), bd("10")));
    }

    @Test
    void doSubtractNumbers() {
        assertEquals(bd("15.00"), mathOperation.doOperation("SUBTRACT", bd("20"), bd("5")));
    }

    @Test
    void doMultiplyNumbers() {
        assertEquals(bd("50.00"), mathOperation.doOperation("MULTIPLY", bd("5"), bd("10")));
    }

    @Test
    void doDivideNumbers() {
        assertEquals(bd("5.00"), mathOperation.doOperation("DIVIDE", bd("20"), bd("4")));
    }

    @Test
    void doRejectDivisionByZero() {
        assertThrows(IllegalArgumentException.class, () 
            -> mathOperation.doOperation("DIVIDE", bd("10"), bd("0")));
    }

    @Test
    void doHandleDecimalNumbers() {
        assertEquals(bd("3.75"), mathOperation.doOperation("MULTIPLY", bd("1.5"), bd("2.5")));
    }

    @Test
    void doHandleNegNumbers() {
        assertEquals(bd("-5.00"), mathOperation.doOperation("ADD", bd("-10"), bd("5")));
    }

    @Test
    void doAcceptLowercaseOpNames() {
        assertEquals(bd("15.00"), mathOperation.doOperation("add", bd("5"), bd("10")));
    }

    /*
        Triggering business rules
    */

    @Test
    void doSetNumber() {
        BusinessRule rule = createRule("Set second operand to 10",
                new RuleTrigger(bd("1"), TargetType.OPERAND2, OperatorType.EQUALS, OperationType.ADD),
                new RuleAction(bd("10"), TargetType.OPERAND2, OperationType.SET));
        when(ruleService.getRules(OperationType.ADD)).thenReturn(List.of(rule));

        // 5 + 1 = 6, but the rule sets the second operand to 10, so 5 + 10 = 15
        assertEquals(bd("15.00"), mathOperation.doOperation("ADD", bd("5"), bd("1")));
    }

    @Test
    void doModifyFirstOperand() {
        BusinessRule rule = createRule("Increase first operand",
                new RuleTrigger(bd("2"), TargetType.OPERAND1, OperatorType.EQUALS, OperationType.ADD),
                new RuleAction(bd("5"), TargetType.OPERAND1, OperationType.ADD));
        when(ruleService.getRules(OperationType.ADD)).thenReturn(List.of(rule));

        // 2 + 3 = 5, but the rule increases the first operand by 5, so 7 + 3 = 10
        assertEquals(bd("10.00"), mathOperation.doOperation("ADD", bd("2"), bd("3")));
    }

    @Test
    void doModifySecondOperand() {
        BusinessRule rule = createRule("Double second operand",
                new RuleTrigger(bd("3"), TargetType.OPERAND2, OperatorType.EQUALS, OperationType.MULTIPLY),
                new RuleAction(bd("2"), TargetType.OPERAND2, OperationType.MULTIPLY));
        when(ruleService.getRules(OperationType.MULTIPLY)).thenReturn(List.of(rule));

        // 3 * 3 = 9, but the rule doubles the second operand, so 3 * 6 = 18
        assertEquals(bd("18.00"), mathOperation.doOperation("MULTIPLY", bd("3"), bd("3")));
    }

    @Test
    void doModifyResult() {
        BusinessRule rule = createRule("Add bonus to result",
                new RuleTrigger(bd("15"), TargetType.RESULT, OperatorType.EQUALS, OperationType.ADD),
                new RuleAction(bd("5"), TargetType.RESULT, OperationType.ADD));
        when(ruleService.getRules(OperationType.ADD)).thenReturn(List.of(rule));

        // 5 + 10 = 15, but the rule adds a bonus of 5 to the result, so 15 + 5 = 20
        assertEquals(bd("20.00"), mathOperation.doOperation("ADD", bd("5"), bd("10")));
    }

    @Test
    void doApplyActionOnAnyRule() {
        BusinessRule rule = createRule("For any result = 100, operand1 added 11",
                new RuleTrigger(bd("100"), TargetType.RESULT, OperatorType.EQUALS, OperationType.ANY),
                new RuleAction(bd("11"), TargetType.OPERAND1, OperationType.ADD));
        when(ruleService.getRules(OperationType.MULTIPLY)).thenReturn(List.of(rule));

        // 10 * 10 = 100, but the rule adds 11 to operand1, so 21 * 10 = 210
        assertEquals(bd("210.00"), mathOperation.doOperation("MULTIPLY", bd("10"), bd("10")));
    }

    @Test
    void doApplyRulesInReturnedOrder() {
        BusinessRule firstRule = createRule("Increase operand",
                new RuleTrigger(bd("2"), TargetType.OPERAND1, OperatorType.EQUALS, OperationType.ADD),
                new RuleAction(bd("5"), TargetType.OPERAND1, OperationType.ADD));

        BusinessRule secondRule = createRule("Double operand",
                new RuleTrigger(bd("7"), TargetType.OPERAND1, OperatorType.EQUALS, OperationType.ADD),
                new RuleAction(bd("2"), TargetType.OPERAND1, OperationType.MULTIPLY));

        when(ruleService.getRules(OperationType.ADD)).thenReturn(List.of(firstRule, secondRule));

        // First: 2 + 5 = 7
        // Second: 7 * 2 = 14
        // Final: 14 + 3 = 17
        assertEquals(bd("17.00"), mathOperation.doOperation("ADD", bd("2"), bd("3")));
    }

    /* 
        Helper methods for creating business rules
    */
    private BusinessRule createRule(String name, RuleTrigger trigger, RuleAction action) {
        BusinessRule rule = new BusinessRule();
        rule.setName(name);
        rule.setActive(true);
        rule.setTrigger(trigger);
        rule.setAction(action);
        return rule;
    }

    private BigDecimal bd(String value) {
        return new BigDecimal(value);
    }
}
