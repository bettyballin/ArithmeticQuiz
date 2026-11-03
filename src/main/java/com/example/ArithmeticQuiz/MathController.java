package com.example.ArithmeticQuiz;

import java.math.BigDecimal;
import java.util.Map;

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

    // Request body for the calculation operation
    public record CalculationRequest(
        // Using @NotNull to ensure that the operands are provided in the request
        @NotNull BigDecimal a,
        @NotNull BigDecimal b
    ) {}

    // Endpoint to perform the arithmetic operation on a and b and the operation type
    @PostMapping("/{operation}")
    public Map<String,BigDecimal> doOperation(@PathVariable String operation, @Valid @RequestBody CalculationRequest request){
        return Map.of("result: ",mathOperation.doOperation(operation, request.a(), request.b()));
    }
}
