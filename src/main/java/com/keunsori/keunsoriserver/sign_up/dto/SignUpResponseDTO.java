package com.keunsori.keunsoriserver.sign_up.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignUpResponseDTO {
    private String name;
    private String studentId;
    private String hongikgmail;
}
