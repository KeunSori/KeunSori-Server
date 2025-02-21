package com.keunsori.keunsoriserver.domain.email.service;

import org.springframework.stereotype.Service;

import com.keunsori.keunsoriserver.domain.email.domain.EmailAuthentication;
import com.keunsori.keunsoriserver.domain.email.dto.request.EmailValidateRequest;
import com.keunsori.keunsoriserver.domain.email.repository.EmailRepository;
import com.keunsori.keunsoriserver.global.util.EmailUtil;

import java.util.Random;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final EmailUtil emailUtil;
    private final EmailRepository emailRepository;

    public void sendAuthNumber(EmailValidateRequest request) {
        String authNumber = generateAuthNumber();
        emailRepository.save(EmailAuthentication.of(request.email(), authNumber));
        emailUtil.sendEmailAddressVerifyMail(request.email(), authNumber);
    }

    private String generateAuthNumber() {
        int baseNumber = 100000;

        Random random = new Random();
        random.setSeed(System.currentTimeMillis());
        baseNumber += random.nextInt(899999);

        return Integer.toString(baseNumber);
    }
}
