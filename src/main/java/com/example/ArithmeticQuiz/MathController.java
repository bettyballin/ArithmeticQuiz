package com.example.ArithmeticQuiz;

import java.math.BigDecimal;

import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/math")
public class MathController {

    private final MathOperation mathOperation;

    public MathController(MathOperation mathOperation) {
        this.mathOperation = mathOperation;
    }

    public record CalculationRequest(
            @NotNull BigDecimal a,
            @NotNull BigDecimal b
    ) {}

    @PostMapping("/{operation}")
    public BigDecimal doOperation(
            @PathVariable String operation,
            @Valid @RequestBody CalculationRequest request) {

        return mathOperation.doOperation(
                operation,
                request.a(),
                request.b()
        );
    }
}
