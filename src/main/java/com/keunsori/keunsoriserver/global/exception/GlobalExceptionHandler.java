package com.keunsori.keunsoriserver.global.exception;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    //@Valid 유효성 검사 실패 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse("유효성 검사 실패");
        ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;
        ErrorResponse response = new ErrorResponse(errorCode.getStatus(), errorCode.getCode(), errorMessage);
        log.info(ex.getMessage());
        return new ResponseEntity<>(response,HttpStatus.valueOf(errorCode.getStatus()));
    }

    //Member 도메인 예외처리(400)
    @ExceptionHandler(MemberException.class)
    public ResponseEntity<ErrorResponse> handleMemberException(MemberException ex) {
        ErrorResponse response = new ErrorResponse(ex.getErrorCode().getStatus(), ex.getErrorCode().getCode(), ex.getMessage());
        log.info(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(ex.getErrorCode().getStatus()));
    }

    //Auth 도메인 예외처리(401)
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ErrorResponse> handleAuthException(AuthException ex) {
        ErrorResponse response = new ErrorResponse(ex.getErrorCode().getStatus(), ex.getErrorCode().getCode(), ex.getMessage());
        log.info(ex.getMessage());
        return new ResponseEntity<>(response,HttpStatusCode.valueOf(ex.getErrorCode().getStatus()));
    }

    @ExceptionHandler(ReservationException.class)
    public ResponseEntity<ErrorResponse> handleReservationException(ReservationException ex) {
        ErrorResponse response = new ErrorResponse(ex.getErrorCode().getStatus(), ex.getErrorCode().getCode(), ex.getMessage());
        log.info(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(ex.getErrorCode().getStatus()));
    }

    @ExceptionHandler(EmailException.class)
    public ResponseEntity<ErrorResponse> handleEmailException(ReservationException ex) {
        ErrorResponse response = new ErrorResponse(ex.getErrorCode().getStatus(), ex.getErrorCode().getCode(), ex.getMessage());
        log.info(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(ex.getErrorCode().getStatus()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        ErrorResponse response = new ErrorResponse(errorCode.getStatus(), errorCode.getCode(), ex.getMessage());
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(errorCode.getStatus()));
    }
}
