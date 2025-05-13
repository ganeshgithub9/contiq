package com.ganesh.contiq.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.ganesh.contiq.DTO.FileContentDTO;
import com.ganesh.contiq.DTO.FileMetaDataDTO;
import com.ganesh.contiq.DTO.FileMetaDataListDTO;
import com.ganesh.contiq.DTO.SuccessResponseDTO;
import com.ganesh.contiq.exception.FileAccessDeniedException;
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

import java.io.IOException;
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

        SuccessResponseDTO expectedResponse= new SuccessResponseDTO("Files uploaded successfully");
        String expectedResult=objectMapper.writeValueAsString(expectedResponse);

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
    public void shouldThrowIOException_whenUserUploadsFilesAndParsingFailed() throws Exception {

        String expectedErrorMessage= "Error processing the PDF file. Make sure there are no corrupt files";

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
            doThrow(new IOException(expectedErrorMessage)).when(fileService).saveFiles(anyList(),anyString());

            //MvcResult result=
                    mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/files")
                            .file(mockedmultipartFileList.get(0))
                            .file(mockedmultipartFileList.get(1))
                            .with(jwt().jwt(jwt -> jwt
                                    .claim("sub", "user1")
                                    .claim("scope", "write:files"))))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").value(expectedErrorMessage));
        }
    }

    @Test
    public void shouldReturnFilesUploadedByUser_whenGetFiles() throws Exception {

        List<FileMetaDataDTO> fileList=new ArrayList<>();
        fileList.add(new FileMetaDataDTO("AB12","file1.pdf"));
        fileList.add(new FileMetaDataDTO("AB13","file2.pdf"));

        FileMetaDataListDTO mockedResponse=new FileMetaDataListDTO();
        mockedResponse.setFileList(fileList);

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
        FileContentDTO mockedFileContentDTO=new FileContentDTO(mockedFileContent);

        String expectedResult= objectMapper.writeValueAsString(mockedFileContentDTO);

        when(fileService.getFileContentById(anyString(),anyString())).thenReturn(mockedFileContentDTO);

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
    public void shouldThrowFileAccessDeniedException_whenGetFileContentByIdOnOthersFile() throws Exception {

        String expectedErrorMessage="You are not authorized to access this file";
        when(fileService.getFileContentById(anyString(),anyString())).thenThrow(new FileAccessDeniedException(expectedErrorMessage));

        try (MockedStatic<JwtUtil> mockedStatic = mockStatic(JwtUtil.class)) {
            mockedStatic.when(() -> JwtUtil.getUserId(any(Jwt.class))).thenReturn("user1");


            //MvcResult result=
                    mockMvc.perform(get("/api/v1/files/randomFileId")
                            .with(jwt().jwt(jwt -> jwt
                                    .claim("sub", "user1")
                                    .claim("scope", "read:files"))))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.error").value(expectedErrorMessage));

//            String actualResult=result.getResponse().getContentAsString();
//            assertEquals(expectedResult,actualResult);
        }
    }

    @Test
    public void shouldThrowFileNotFoundException_whenGetFileContentByInvalidId() throws Exception {


        String invalidFileId="invalidFileId",expectedErrorMessage="File with ID "+invalidFileId+" not found";
        when(fileService.getFileContentById(anyString(),anyString())).thenThrow(new FileAccessDeniedException(expectedErrorMessage));

        try (MockedStatic<JwtUtil> mockedStatic = mockStatic(JwtUtil.class)) {
            mockedStatic.when(() -> JwtUtil.getUserId(any(Jwt.class))).thenReturn("user1");


            //MvcResult result=
            mockMvc.perform(get("/api/v1/files/"+invalidFileId)
                            .with(jwt().jwt(jwt -> jwt
                                    .claim("sub", "user1")
                                    .claim("scope", "read:files"))))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.error").value(expectedErrorMessage));

//            String actualResult=result.getResponse().getContentAsString();
//            assertEquals(expectedResult,actualResult);
        }
    }

//    @Test
//    public void shouldThrowException_whenGetFileContentByIdAndServerIsNotHealthy() throws Exception {
//
//        String expectedErrorMessage="An unexpected error occurred. Please try again later or contact support if the issue persists.";
//        when(fileService.getFileContentById(anyString(),anyString())).thenThrow(new Exception(expectedErrorMessage));
//
//        try (MockedStatic<JwtUtil> mockedStatic = mockStatic(JwtUtil.class)) {
//            mockedStatic.when(() -> JwtUtil.getUserId(any(Jwt.class))).thenReturn("user1");
//
//
//            //MvcResult result=
//            mockMvc.perform(get("/api/v1/files/randomFileId")
//                            .with(jwt().jwt(jwt -> jwt
//                                    .claim("sub", "user1")
//                                    .claim("scope", "read:files"))))
//                    .andExpect(status().isInternalServerError())
//                    .andExpect(jsonPath("$.error").value(expectedErrorMessage));
//        }
//    }

    @Test
    public void shouldReturnFiles_whenSearchFilesContainingGivenKeyword() throws Exception {

        List<FileMetaDataDTO> fileList=new ArrayList<>();
        fileList.add(new FileMetaDataDTO("AB12","file1HavingHelloKeyword.pdf"));
        fileList.add(new FileMetaDataDTO("AB13","file2HavingHelloKeyword.pdf"));
        FileMetaDataListDTO mockedResponse=new FileMetaDataListDTO();
        mockedResponse.setFileList(fileList);

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
