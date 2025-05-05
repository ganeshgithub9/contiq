package com.ganesh.contiq.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.ganesh.contiq.DTO.FileMetaDataDTO;
import com.ganesh.contiq.service.CustomFileService;
import com.ganesh.contiq.util.JwtUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {FileController.class})
public class FileControllerTests {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    CustomFileService fileService;

    ObjectMapper objectMapper=new ObjectMapper();


    @Test
    public void shouldReturnSuccessMessage_whenUserUploadsFiles() throws Exception {

        String expectedResult= "Files uploaded successfully";

        List<MockMultipartFile> mockedmultipartFileList=List.of(new MockMultipartFile(
                "files",
                "file1.pdf",
                "application/pdf",
                "Dummy file1 PDF content".getBytes()
        ), new MockMultipartFile(
                "files",
                "file2.pdf",
                "application/pdf",
                "Dummy file2 PDF content".getBytes()
        ));

        try (MockedStatic<JwtUtil> mockedStatic = mockStatic(JwtUtil.class)) {
            mockedStatic.when(() -> JwtUtil.getUserId(any(Jwt.class))).thenReturn("user1");


            MvcResult result=mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/files")
                    .file(mockedmultipartFileList.get(0))
                    .file(mockedmultipartFileList.get(1))
                            .with(jwt().jwt(jwt -> jwt
                                    .claim("sub", "user1")
                                    .claim("scope", "write:files"))))
                    .andExpect(status().isCreated())
                    .andReturn();

            String actualResult=result.getResponse().getContentAsString();
            assertEquals(expectedResult,actualResult);
        }
    }

    @Test
    public void shouldReturnFilesUploadedByUser_whenGetFiles() throws Exception {

        List<FileMetaDataDTO> mockedResponse=new ArrayList<>();
        mockedResponse.add(new FileMetaDataDTO("AB12","file1.pdf"));
        mockedResponse.add(new FileMetaDataDTO("AB13","file2.pdf"));

        String expectedResult= objectMapper.writeValueAsString(mockedResponse);

        when(fileService.getFilesByUserId(anyString())).thenReturn(mockedResponse);

        try (MockedStatic<JwtUtil> mockedStatic = mockStatic(JwtUtil.class)) {
            mockedStatic.when(() -> JwtUtil.getUserId(any(Jwt.class))).thenReturn("user1");


            MvcResult result=mockMvc.perform(get("/api/v1/files")
                            .with(jwt().jwt(jwt -> jwt
                                    .claim("sub", "user1")
                                    .claim("scope", "read:files"))))
                    .andExpect(status().isOk())
                    .andReturn();

            String actualResult=result.getResponse().getContentAsString();
            assertEquals(expectedResult,actualResult);

        }
    }

    @Test
    public void shouldReturnFileContent_whenGetFileContentById() throws Exception {

        String mockedFileContent="This is a file content";

        String expectedResult= mockedFileContent;

        when(fileService.getFileContentById(anyString(),anyString())).thenReturn(mockedFileContent);

        try (MockedStatic<JwtUtil> mockedStatic = mockStatic(JwtUtil.class)) {
            mockedStatic.when(() -> JwtUtil.getUserId(any(Jwt.class))).thenReturn("user1");


            MvcResult result=mockMvc.perform(get("/api/v1/files/randomFileId")
                            .with(jwt().jwt(jwt -> jwt
                                    .claim("sub", "user1")
                                    .claim("scope", "read:files"))))
                    .andExpect(status().isOk())
                    .andReturn();

            String actualResult=result.getResponse().getContentAsString();
            assertEquals(expectedResult,actualResult);
        }
    }

    @Test
    public void shouldReturnFiles_whenSearchFilesContainingGivenKeyword() throws Exception {

        List<FileMetaDataDTO> mockedResponse=new ArrayList<>();
        mockedResponse.add(new FileMetaDataDTO("AB12","file1HavingHelloKeyword.pdf"));
        mockedResponse.add(new FileMetaDataDTO("AB13","file2HavingHelloKeyword.pdf"));

        String expectedResult= objectMapper.writeValueAsString(mockedResponse);

        when(fileService.getFilesByUserIdAndKeyword(anyString(),anyString())).thenReturn(mockedResponse);

        try (MockedStatic<JwtUtil> mockedStatic = mockStatic(JwtUtil.class)) {
            mockedStatic.when(() -> JwtUtil.getUserId(any(Jwt.class))).thenReturn("user1");


            MvcResult result=mockMvc.perform(get("/api/v1/files/search?keyword=hello")
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
