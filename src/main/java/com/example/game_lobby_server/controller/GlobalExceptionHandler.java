package com.example.game_lobby_server.controller;

import com.example.game_lobby_server.dto.ErrorResponseDto;
import com.example.game_lobby_server.exception.RoomCreationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ルーム作成時のエラー
    @ExceptionHandler(RoomCreationException.class)
    public ResponseEntity<ErrorResponseDto> handleRoomCreationException(RoomCreationException ex) {
        ErrorResponseDto errorResponse = new ErrorResponseDto(ex.getMessage(), "error");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // その他のエラー
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(Exception ex) {
        ErrorResponseDto errorResponse = new ErrorResponseDto("サーバーエラーが発生しました", "error");
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
