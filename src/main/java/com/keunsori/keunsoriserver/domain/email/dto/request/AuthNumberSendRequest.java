package com.keunsori.keunsoriserver.domain.email.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AuthNumberSendRequest(
        @NotBlank String email
) {}
