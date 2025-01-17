package com.keunsori.keunsoriserver.global.exception;

public class MemberException extends RuntimeException {
    public MemberException(String message) {
    super(message);
    }

    public static class InvalidStudentIdException extends MemberException {
        public InvalidStudentIdException(String message) {
            super(message);
        }
    }

    public static class InvalidHongikGmailException extends MemberException {
        public InvalidHongikGmailException(String message) { super(message); }
    }

    public static class IncorrectPasswordException extends MemberException {
        public IncorrectPasswordException(String message) {
            super(message);
        }
    }



}
