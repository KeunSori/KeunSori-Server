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

    public void sendPasswordInitializeEmail(String email, String initializedPassword) {
        SimpleMailMessage message = new SimpleMailMessage(templateMessage);
        message.setTo(email);
        message.setSubject("[큰소리] 비밀번호 초기화");
        message.setText("초기화된 비밀번호는 [" + initializedPassword + "] 입니다. 초기화 후 마이페이지에서 비밀번호를 변경해주세요.");

        mailSender.send(message);
    }
}
