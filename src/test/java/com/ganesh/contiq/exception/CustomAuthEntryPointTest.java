package com.ganesh.contiq.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ganesh.contiq.exception.CustomAuthEntryPoint;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.AuthenticationException;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class CustomAuthEntryPointTest {

    @Test
    void shouldSetUnauthenticatedResponse_whenAuthenticationFails() throws Exception {

        CustomAuthEntryPoint entryPoint = new CustomAuthEntryPoint(new ObjectMapper());

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        AuthenticationException authException = mock(AuthenticationException.class);

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        when(response.getWriter()).thenReturn(printWriter);


        entryPoint.commence(request, response, authException);


        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response).setContentType("application/json");
        verify(response).setHeader("WWW-Authenticate", "Bearer");

        printWriter.flush();
        assertTrue(stringWriter.toString().contains("Invalid or missing token"));
    }
}
