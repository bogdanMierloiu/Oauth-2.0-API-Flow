package ro.bogdanmierloiu.Oauth2APIFlow.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ro.bogdanmierloiu.Oauth2APIFlow.dto.ResponseDto;
import ro.bogdanmierloiu.Oauth2APIFlow.exception.NotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ResponseDto> handleNotFoundException(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ResponseDto(ex.getMessage()));
    }

}
