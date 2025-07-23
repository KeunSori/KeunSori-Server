package com.keunsori.keunsoriserver.global.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtil {

    private static final int ACCESS_TOKEN_EXPIRY=30*60;
    private static final int REFRESH_TOKEN_EXPIRY=7*24*60*60;

    public static void addAccessTokenCookie(HttpServletResponse response, String accessToken){
        addCookie(response, "Access-Token", accessToken, ACCESS_TOKEN_EXPIRY);
    }

    public static void addRefreshTokenCookie(HttpServletResponse response, String refreshToken){
        addCookie(response, "Refresh-Token", refreshToken, REFRESH_TOKEN_EXPIRY);
    }

    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge){
        String cookieValue = String.format(
                "%s=%s; Path=/; Max-Age=%d; HttpOnly; Secure; SameSite=None",
                name, value, maxAge
        );
        response.addHeader("Set-Cookie", cookieValue);
    }

    public static void deleteCookie(HttpServletResponse response, String name){
        String cookieValue = String.format(
                "%s=; Path=/; Max-Age=0; HttpOnly; Secure; SameSite=None",
                name
        );
        response.addHeader("Set-Cookie", cookieValue);
    }

    public static String getCookieValue(HttpServletRequest request, String name){
        if(request.getCookies() == null){
            return null;
        }

        for(Cookie cookie : request.getCookies()){
            if(cookie.getName().equals(name)){
                return cookie.getValue();
            }
        }
        return null;
    }
}
