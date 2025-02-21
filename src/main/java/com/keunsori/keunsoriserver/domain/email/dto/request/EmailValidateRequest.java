package com.keunsori.keunsoriserver.domain.email.dto.request;

import jakarta.validation.constraints.NotBlank;

public record EmailValidateRequest(
        @NotBlank String email
) {}
