package com.ganesh.contiq.exception;

import com.ganesh.contiq.DTO.ErrorResponseDTO;
import com.ganesh.contiq.util.ErrorResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpServerErrorException;

import java.io.IOException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponseDTO> handleIOException(IOException ioException){
        String errorId=ErrorResponseUtil.getRandomId();
        log.error("Error id: {}",errorId);
        return new ResponseEntity<>(new ErrorResponseDTO(ioException.getMessage(), errorId,ErrorResponseUtil.getTimeStamp()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FileAccessDeniedException.class)
    public ResponseEntity<ErrorResponseDTO> handleFileAccessDeniedException(FileAccessDeniedException fileAccessDeniedException){
        String errorId=ErrorResponseUtil.getRandomId();
        log.error("Error id: {}",errorId);
        return new ResponseEntity<>(new ErrorResponseDTO(fileAccessDeniedException.getMessage(),errorId,ErrorResponseUtil.getTimeStamp()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleFileNotFoundException(FileNotFoundException fileNotFoundException){
        String errorId=ErrorResponseUtil.getRandomId();
        log.error("Error id: {}",errorId);
        return new ResponseEntity<>(new ErrorResponseDTO(fileNotFoundException.getMessage(),errorId,ErrorResponseUtil.getTimeStamp()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleInternalServerException(Exception exception){
        String errorId=ErrorResponseUtil.getRandomId();
        log.error("Error id: {}",errorId);
        return new ResponseEntity<>(new ErrorResponseDTO("An unexpected error occurred. Please try again later or contact support if the issue persists.",errorId,ErrorResponseUtil.getTimeStamp()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
