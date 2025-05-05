package com.ganesh.contiq.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ganesh.contiq.model.Paragraph;
import com.ganesh.contiq.util.JwtUtil;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

@SpringBootTest
public class PdfProcessingServiceTest {

    @Mock
    PDFTextStripper pdfTextStripper;

    @Spy
    @InjectMocks
    CustomPdfProcessingService pdfProcessingService;

    ObjectMapper objectMapper=new ObjectMapper();


    @BeforeEach
    public void setup(){
        openMocks(this);
    }

    @Test
    public void shouldReturnContent_whenExtractTextFromPdf() throws IOException {

        PDDocument document= mock(PDDocument.class);
        MultipartFile multipartFile=new MockMultipartFile(
                "files",
                "file1.pdf",
                "application/pdf",
                "Dummy file1 PDF content".getBytes()
        );
        PDFTextStripper mockStripper = mock(PDFTextStripper.class);

        try (MockedStatic<PDDocument> mockedStatic = mockStatic(PDDocument.class)) {

            mockedStatic.when(() -> PDDocument.load(any(InputStream.class))).thenReturn(document);
            when(mockStripper.getText(any(PDDocument.class))).thenReturn("File content");
            doReturn(mockStripper).when(pdfProcessingService).createPdfTextStripper();

            String actualText= pdfProcessingService.extractTextFromPdf(multipartFile),expectedText="File content";

            assertEquals(expectedText,actualText);

        }
    }


    @Test
    public void shouldReturnAListOfParagraphs_whenProcessPdf() throws IOException {

        PDDocument document= mock(PDDocument.class);
        MultipartFile multipartFile=new MockMultipartFile(
                "files",
                "file1.pdf",
                "application/pdf",
                "Dummy file1 PDF content".getBytes()
        );
        PDFTextStripper mockStripper = mock(PDFTextStripper.class);

        try (MockedStatic<PDDocument> mockedStatic = mockStatic(PDDocument.class)) {

            mockedStatic.when(() -> PDDocument.load(any(InputStream.class))).thenReturn(document);
            when(mockStripper.getText(any(PDDocument.class))).thenReturn("File content paragraph 1\n\nFile content paragraph 2");
            doReturn(mockStripper).when(pdfProcessingService).createPdfTextStripper();
            when(document.getNumberOfPages()).thenReturn(1);

            List<Paragraph> paragraphList=pdfProcessingService.processPdf(multipartFile);

            assertEquals(2,paragraphList.size());
            assert(paragraphList.get(0).getContent().equals("File content paragraph 1"));
            assert(paragraphList.get(1).getContent().equals("File content paragraph 2"));
        }
    }


    @Test
    public void shouldReturnPdfTextStripper_whenCreatePdfTextStripper() throws IOException {
            PDFTextStripper pdfTextStripper=pdfProcessingService.createPdfTextStripper();
            assertNotNull(pdfTextStripper);
    }
}
