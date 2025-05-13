package com.ganesh.contiq.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ganesh.contiq.DTO.FileContentDTO;
import com.ganesh.contiq.DTO.FileMetaDataDTO;
import com.ganesh.contiq.DTO.FileMetaDataListDTO;
import com.ganesh.contiq.model.File;
import com.ganesh.contiq.model.Paragraph;
import com.ganesh.contiq.repository.FileRepository;
import com.ganesh.contiq.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class FileServiceTest {

    @Mock
    CustomPdfProcessingService pdfProcessingService;
    @Mock
    FileRepository fileRepository;
    @Mock
    CustomParagraphService paragraphService;
    @Spy
    ModelMapper modelMapper;

    @InjectMocks
    CustomFileService fileService;

    ObjectMapper objectMapper=new ObjectMapper();


    @BeforeEach
    void setup(){
        openMocks(this);
    }

    @Test
    void contextLoads() {
    }

    @Test
    public void shouldSaveFiles_whenSaveFilesByUser() throws Exception {

        MultipartFile file1=new MockMultipartFile(
                "files",
                "file1.pdf",
                "application/pdf",
                "Dummy file1 PDF content".getBytes()
        ), file2=new MockMultipartFile(
                "files",
                "file2.pdf",
                "application/pdf",
                "Dummy file2 PDF content".getBytes()
        );
        List<MultipartFile> multipartFileList=List.of(file1,file2);

        String content1="Dummy file1 PDF content",content2="Dummy file2 PDF content",userId="userId";

        File newFile=new File();
        newFile.setId("fileId");
        newFile.setName("file.pdf");
        newFile.setContent("Dummy file PDF content");
        newFile.setUserId(userId);

        Paragraph mockedParagraph=new Paragraph("1","Hello mate!",1,"fileId",1);
        List<Paragraph> mockedParagraphList=new ArrayList<>();
        mockedParagraphList.add(mockedParagraph);

        

        when(pdfProcessingService.extractTextFromPdf(file1)).thenReturn(content1);
        when(pdfProcessingService.extractTextFromPdf(file2)).thenReturn(content2);
        when(fileRepository.save(any(File.class))).thenReturn(newFile);
        when(pdfProcessingService.processPdf(any(MultipartFile.class))).thenReturn(mockedParagraphList);
        when(paragraphService.saveParagraph(any(Paragraph.class))).thenReturn(mockedParagraph);


        fileService.saveFiles(multipartFileList,userId);

        verify(pdfProcessingService,times(2)).extractTextFromPdf(any(MultipartFile.class));
        verify(fileRepository,times(2)).save(any(File.class));
        verify(pdfProcessingService,times(2)).processPdf(any(MultipartFile.class));
        verify(paragraphService,times(2)).saveParagraph(any(Paragraph.class));

    }

    @Test
    public void shouldReturnFileContent_whenGetFileContentById() throws Exception {

        String content="Dummy PDF file content";
        FileContentDTO fileContentDTO=new FileContentDTO(content);

        String expectedResult=objectMapper.writeValueAsString(fileContentDTO);

        when(fileRepository.findContentById(anyString(),anyString())).thenReturn(content);


        FileContentDTO actualContent=fileService.getFileContentById("fileId","userId");
        String actualResult=objectMapper.writeValueAsString(actualContent);
        assertEquals(expectedResult,actualResult);

    }

    @Test
    public void shouldReturnFiles_whenGetFilesByUserId() throws Exception {

        File newFile=new File();
        newFile.setId("fileId");
        newFile.setName("file.pdf");
        newFile.setContent("Dummy file PDF content");
        newFile.setUserId("userId");

        List<File> mockedFileList=List.of(newFile);
        List<FileMetaDataDTO> expectedFileMetaDataList=List.of(modelMapper.map(newFile,FileMetaDataDTO.class));
        FileMetaDataListDTO expectedResult=new FileMetaDataListDTO();
        expectedResult.setFileList(expectedFileMetaDataList);

        when(fileRepository.findFilesByUserId(anyString())).thenReturn(mockedFileList);

        FileMetaDataListDTO actualResult=fileService.getFilesByUserId("userId");

        assertEquals(objectMapper.writeValueAsString(expectedResult),objectMapper.writeValueAsString(actualResult));

    }

    @Test
    public void shouldReturnFiles_whenGetFilesByUserIdAndKeyword() throws Exception {

        File newFile=new File();
        newFile.setId("fileId");
        newFile.setName("file.pdf");
        newFile.setContent("Dummy file PDF content");
        newFile.setUserId("userId");

        List<File> mockedFileList=List.of(newFile);
        List<FileMetaDataDTO> mockedFileMetaDataList=List.of(modelMapper.map(newFile,FileMetaDataDTO.class));
        FileMetaDataListDTO expectedResult=new FileMetaDataListDTO();
        expectedResult.setFileList(mockedFileMetaDataList);

        List<String> mockedFileIDList=List.of("fileId");
        when(fileRepository.findFileIdsByUserIdAndKeyword(anyString(),anyString())).thenReturn(mockedFileIDList);
        when(fileRepository.getFileMetaDataById(anyString())).thenReturn(newFile);

        FileMetaDataListDTO actualResult=fileService.getFilesByUserIdAndKeyword("userId","file");

        assertEquals(objectMapper.writeValueAsString(expectedResult),objectMapper.writeValueAsString(actualResult));

    }

}
