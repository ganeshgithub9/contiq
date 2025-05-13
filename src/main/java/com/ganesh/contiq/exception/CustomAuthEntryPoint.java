package com.ganesh.contiq.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ganesh.contiq.DTO.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@Component
@AllArgsConstructor
public class CustomAuthEntryPoint implements AuthenticationEntryPoint {

    @Autowired
    ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        ErrorResponseDTO errorResponseDTO=new ErrorResponseDTO("Invalid or missing token", UUID.randomUUID().toString(), Instant.now().toString());
        String responseString=objectMapper.writeValueAsString(errorResponseDTO);
        response.getWriter().write(responseString);

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"Invalid or missing token\"}");
        response.setHeader("WWW-Authenticate","Bearer");
    }
}

