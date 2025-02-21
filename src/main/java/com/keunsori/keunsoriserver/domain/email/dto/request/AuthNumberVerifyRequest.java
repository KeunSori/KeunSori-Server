package com.keunsori.keunsoriserver.domain.email.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AuthNumberVerifyRequest(
        @NotBlank String email,
        @NotBlank String authNumber
) {}
