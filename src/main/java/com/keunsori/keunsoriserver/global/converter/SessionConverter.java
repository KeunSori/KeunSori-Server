package com.keunsori.keunsoriserver.global.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.keunsori.keunsoriserver.domain.reservation.domain.vo.Session;

@Component
public class SessionConverter implements Converter<String, Session> {

    @Override
    public Session convert(String source) {
        source = source.toUpperCase();
        return Session.valueOf(source);
    }

    @Override
    public <U> Converter<String, U> andThen(Converter<? super Session, ? extends U> after) {
        return Converter.super.andThen(after);
    }
}
