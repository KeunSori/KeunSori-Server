package com.keunsori.keunsoriserver.global.constant;

public class UrlConstant {

    private static final String DEFAULT_DOMAIN = System.getenv("SERVER_DOMAIN");

    public static final String PASSWORD_CHANGE_LINK = DEFAULT_DOMAIN + "password/change";
}
