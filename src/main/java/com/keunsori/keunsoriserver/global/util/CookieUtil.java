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
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    public static void deleteCookie(HttpServletResponse response, String name){
        Cookie cookie = new Cookie(name, "");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
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
