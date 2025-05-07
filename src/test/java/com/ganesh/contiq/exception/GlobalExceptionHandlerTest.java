package com.ganesh.contiq.exception;

import com.ganesh.contiq.DTO.ErrorResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

//@SpringBootTest
public class GlobalExceptionHandlerTest {


    GlobalExceptionHandler globalExceptionHandler=new GlobalExceptionHandler();

    @Test
    public void shouldReturnFileNotFoundMessage_whenRunHandleFileNotFoundExceptionMethod(){

        ResponseEntity<ErrorResponseDTO> response=globalExceptionHandler.handleFileNotFoundException(new FileNotFoundException("File does not exist"));

        assertEquals("File does not exist",response.getBody().getError());
        assertEquals(HttpStatus.NOT_FOUND,response.getStatusCode());
    }

    @Test
    public void shouldReturnProperMessage_whenRunHandleInternalServerExceptionMethod(){

        ResponseEntity<ErrorResponseDTO> response=globalExceptionHandler.handleInternalServerException(new Exception());

        assertEquals("An unexpected error occurred. Please try again later or contact support if the issue persists.",response.getBody().getError());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,response.getStatusCode());
    }


}
