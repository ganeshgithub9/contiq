package com.ganesh.contiq.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

public class CustomAccessDeniedHandlerTest {
    @Test
    void shouldSetUnauthorizedResponse_whenAuthorizationFails() throws Exception {

        CustomAccessDeniedHandler accessDeniedHandler=new CustomAccessDeniedHandler(new ObjectMapper());

        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        AccessDeniedException authException = mock(AccessDeniedException.class);

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        when(response.getWriter()).thenReturn(printWriter);


        accessDeniedHandler.handle(request, response, authException);


        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(response).setContentType("application/json");

        printWriter.flush();
        assertTrue(stringWriter.toString().contains("You don't have permission to access this resource"));
    }
}
