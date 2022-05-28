package com.falabella.entrevista.app.domain.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReasonResponse {
    private String errorReason;
    private LocalDateTime timestamp;

    public ReasonResponse(String reason) {
        this.errorReason = reason;
        this.timestamp = LocalDateTime.now();
    }
}



