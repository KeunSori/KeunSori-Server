package com.keunsori.keunsoriserver.domain.auth.dto.request;

public record PasswordUpdateRequest(
        String token,
        String newPassword
) {}
