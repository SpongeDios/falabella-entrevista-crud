package com.falabella.entrevista.app.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ConstraintViolationResponse {
    private String message;
    private List<String> errors;

    public ConstraintViolationResponse(String message, List<String> errors) {
        this.message = message;
        this.errors = errors;
    }
}


