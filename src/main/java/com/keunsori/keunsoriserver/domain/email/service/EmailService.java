package com.keunsori.keunsoriserver.domain.email.service;

import static com.keunsori.keunsoriserver.global.exception.ErrorMessage.EMAIL_NOT_EXISTS_FOR_AUTH;

import org.springframework.stereotype.Service;

import com.keunsori.keunsoriserver.domain.email.domain.EmailAuthentication;
import com.keunsori.keunsoriserver.domain.email.dto.request.AuthNumberSendRequest;
import com.keunsori.keunsoriserver.domain.email.dto.request.AuthNumberVerifyRequest;
import com.keunsori.keunsoriserver.domain.email.repository.EmailRepository;
import com.keunsori.keunsoriserver.global.exception.EmailException;
import com.keunsori.keunsoriserver.global.util.EmailUtil;

import java.util.Random;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final EmailUtil emailUtil;
    private final EmailRepository emailRepository;

    public void sendAuthNumber(AuthNumberSendRequest request) {
        String authNumber = generateAuthNumber();
        emailRepository.save(EmailAuthentication.of(request.email(), authNumber));
        emailUtil.sendEmailAddressVerifyMail(request.email(), authNumber);
    }

    public void verifyAuthNumber(AuthNumberVerifyRequest request) {
        EmailAuthentication emailAuthentication = emailRepository.findById(request.email())
                .orElseThrow(() -> new EmailException(EMAIL_NOT_EXISTS_FOR_AUTH));

        emailAuthentication.verifyAuthNumber(request.authNumber());
    }

    private String generateAuthNumber() {
        int baseNumber = 100000;

        Random random = new Random();
        random.setSeed(System.currentTimeMillis());
        baseNumber += random.nextInt(899999);

        return Integer.toString(baseNumber);
    }
}
