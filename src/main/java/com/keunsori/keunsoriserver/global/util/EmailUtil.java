package com.keunsori.keunsoriserver.global.util;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

import java.util.Random;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EmailUtil {

    private final MailSender mailSender;

    private final SimpleMailMessage templateMessage;

    public void sendEmailAddressVerifyMail(String email, String authNumber) {
        SimpleMailMessage message = new SimpleMailMessage(templateMessage);
        message.setTo(email);
        message.setSubject("[큰소리] 회원가입 이메일 인증번호");
        message.setText("이메일 인증번호는 [" + authNumber + "] 입니다. 5분 이내로 입력해주세요.");

        mailSender.send(message);
    }
}
