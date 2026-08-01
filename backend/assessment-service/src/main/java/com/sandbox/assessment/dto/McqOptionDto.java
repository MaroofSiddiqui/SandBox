package com.sandbox.assessment.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class McqOptionDto {
    private Long id;
    private String optionText;
    private Boolean isCorrect;
}