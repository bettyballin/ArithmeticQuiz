package com.example.ArithmeticQuiz;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

@ExtendWith(MockitoExtension.class)
class ArithmeticQuizApplicationTests {

    @Mock
    private RuleService ruleService;

    @InjectMocks
    private MathOperation mathOperation;

    @Test
    void shouldAddNumbers() {
        assertEquals(15, mathOperation.calculate(OperationType.ADD, new BigDecimal(5), new BigDecimal(10)));
    }

    @Test
    void shouldSubtractNumbers() {
        assertEquals(15, mathOperation.calculate(OperationType.SUBTRACT, new BigDecimal(20), new BigDecimal(5)));
    }

    @Test
    void shouldMultiplyNumbers() {
        assertEquals(50, mathOperation.calculate(OperationType.MULTIPLY, new BigDecimal(5), new BigDecimal(10)));
    }

    @Test
    void shouldDivideNumbers() {
        assertEquals(5, mathOperation.calculate(OperationType.DIVIDE, new BigDecimal(20), new BigDecimal(4)));
    }
}