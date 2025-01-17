package com.keunsori.keunsoriserver.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //@Valid 유효성 검사 실패 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String errorMessage=ex.getBindingResult()
                .getFieldErrors()
                .stream().
                findFirst().
                map(error-> error.getField()+":"+error.getDefaultMessage()).
                orElse("유효성 검사 실패");

        ErrorMessage response= new ErrorMessage(HttpStatus.BAD_REQUEST.value(),errorMessage);
        return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
    }

    //Member 도메인 예외처리(400)
    @ExceptionHandler(MemberException.class)
    public ResponseEntity<ErrorMessage> handleMemberException(MemberException ex) {
        ErrorMessage response=new ErrorMessage(HttpStatus.BAD_REQUEST.value(),ex.getMessage());
        return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
    }

    //Auth 도메인 예외처리(401)
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ErrorMessage> handleAuthException(AuthException ex) {
        ErrorMessage response=new ErrorMessage(HttpStatus.UNAUTHORIZED.value(),ex.getMessage());
        return new ResponseEntity<>(response,HttpStatus.UNAUTHORIZED);
    }

    //MemberNotFoundException 예외처리(400)
    @ExceptionHandler
    public ResponseEntity<ErrorMessage> handleMemberNotFoundException(MemberNotFoundException ex) {
        ErrorMessage response=new ErrorMessage(HttpStatus.BAD_REQUEST.value(),ex.getMessage());
        return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
    }
}
