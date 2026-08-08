package com.sandbox.assessment.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AssignCandidatesRequest {

    private List<Long> candidateIds;
}