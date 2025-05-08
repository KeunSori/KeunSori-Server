package com.keunsori.keunsoriserver.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

import static com.keunsori.keunsoriserver.global.constant.RequestFormatConstant.STUDENT_ID_REGEX;
import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.EMAIL_INVALID_FORMAT;
import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.STUDENT_ID_INVALID_FORMAT;

public record PasswordUpdateLinkSendRequest(
        @Pattern(regexp = STUDENT_ID_REGEX, message = STUDENT_ID_INVALID_FORMAT)
        String studentId,
        @Email(message = EMAIL_INVALID_FORMAT)
        String email
) {}
