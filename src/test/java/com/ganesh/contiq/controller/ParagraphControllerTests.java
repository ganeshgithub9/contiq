package com.ganesh.contiq.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.ganesh.contiq.DTO.FileMetaDataDTO;
import com.ganesh.contiq.model.Paragraph;
import com.ganesh.contiq.service.CustomFileService;
import com.ganesh.contiq.service.CustomParagraphService;
import com.ganesh.contiq.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {ParagraphController.class})
public class ParagraphControllerTests {
    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    CustomParagraphService paragraphService;

    ObjectMapper objectMapper=new ObjectMapper();

    @Test
    public void shouldReturnParagraphs_whenGetParagraphsByFileIdWithGivenKeyword() throws Exception {

        List<Paragraph> mockedResponse=new ArrayList<>();
        mockedResponse.add(new Paragraph("1","Hello mate!",3,"fileId1",4));
        mockedResponse.add(new Paragraph("2","Hi! Hello mate!",1,"fileId1",6));

        String expectedResult= objectMapper.writeValueAsString(mockedResponse);

        when(paragraphService.getParagraphsByFileIdAndKeyword(anyString(),anyString(),anyString())).thenReturn(mockedResponse);

        try (MockedStatic<JwtUtil> mockedStatic = mockStatic(JwtUtil.class)) {
            mockedStatic.when(() -> JwtUtil.getUserId(any(Jwt.class))).thenReturn("user1");


            MvcResult result=mockMvc.perform(get("/api/v1/files/fileId1/search?keyword=Hello")
                            .with(jwt().jwt(jwt -> jwt
                                    .claim("sub", "user1")
                                    .claim("scope", "read:files"))))
                    .andExpect(status().isOk())
                    .andReturn();

            String actualResult=result.getResponse().getContentAsString();
            assertEquals(expectedResult,actualResult);
        }
    }
}
