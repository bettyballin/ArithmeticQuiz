package com.example.ArithmeticQuiz;

import java.math.BigDecimal;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/math")
public class MathController {

    private final MathOperation mathOperation;

    public MathController(MathOperation mathOperation) {
        this.mathOperation = mathOperation;
    }

    public record CalculationRequest(
            BigDecimal a,
            BigDecimal b
    ) {}

    @PostMapping("/{operation}")
    public BigDecimal doOperation(
            @PathVariable String operation,
            @RequestBody CalculationRequest request) {

        return mathOperation.doOperation(
                operation,
                request.a(),
                request.b()
        );
    }
}
