package com.keunsori.keunsoriserver.global.exception;

public class AuthException extends RuntimeException {

    public AuthException(String message) {
        super(message);
    }

    public static class InvalidRefreshTokenException extends AuthException {
        public InvalidRefreshTokenException(String message) {
            super(message);
        }
    }


    //Auth-login관련 예외처리
    /*public static class MemberNotFoundException extends AuthException {
        public MemberNotFoundException(String message) {
            super(message);
        }
    }
    */

    public static class InvalidPasswordException extends AuthException {
        public InvalidPasswordException(String message) {
            super(message);
        }
    }


}
