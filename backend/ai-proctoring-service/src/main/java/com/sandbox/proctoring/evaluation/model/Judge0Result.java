package com.sandbox.proctoring.evaluation.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Judge0Result {
    private boolean passed;
    private String actualOutput;
    private String errorMessage; // compile error ya runtime error, agar hua ho
}
