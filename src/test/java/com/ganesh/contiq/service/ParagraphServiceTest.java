package com.ganesh.contiq.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ganesh.contiq.model.Paragraph;
import com.ganesh.contiq.repository.ParagraphRepository;
import com.ganesh.contiq.repository.ParagraphRepositoryCustom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

@SpringBootTest
public class ParagraphServiceTest {

    @Mock
    ParagraphRepository paragraphRepository;

    @InjectMocks
    CustomParagraphService paragraphService;

    ObjectMapper objectMapper=new ObjectMapper();

    @BeforeEach
    void setup(){
        openMocks(this);
    }

    @Test
    public void shouldReturnParagraph_whenSaveParagraph() throws JsonProcessingException {
        Paragraph mockedParagraph=new Paragraph("1","Hello mate!",1,"fileId",1);

        when(paragraphRepository.save(any(Paragraph.class))).thenReturn(mockedParagraph);

        Paragraph savedParagraph=paragraphService.saveParagraph(mockedParagraph);

        String expectedResult=objectMapper.writeValueAsString(mockedParagraph), actualResult=objectMapper.writeValueAsString(savedParagraph);
        assertEquals(expectedResult,actualResult);
    }

    @Test
    public void shouldReturnAListOfParagraphs_whenGetParagraphsByFileIdAndKeyword() throws JsonProcessingException {
        Paragraph mockedParagraph=new Paragraph("1","Hello mate!",1,"fileId",1);
        List<Paragraph> mockedParagraphList=new ArrayList<>();
        mockedParagraphList.add(mockedParagraph);

        when(paragraphRepository.findParagraphsByFileIdAndKeyword(anyString(),anyString(),anyString())).thenReturn(mockedParagraphList);

        List<Paragraph> paragraphList=paragraphService.getParagraphsByFileIdAndKeyword("fileId","Hello","userId");

        String expectedResult=objectMapper.writeValueAsString(mockedParagraphList), actualResult=objectMapper.writeValueAsString(paragraphList);
        assertEquals(expectedResult,actualResult);
    }
}
