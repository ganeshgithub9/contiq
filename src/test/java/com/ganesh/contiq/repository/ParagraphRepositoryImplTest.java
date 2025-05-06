package com.ganesh.contiq.repository;

import com.ganesh.contiq.exception.FileAccessDeniedException;
import com.ganesh.contiq.exception.FileNotFoundException;
import com.ganesh.contiq.model.File;
import com.ganesh.contiq.model.Paragraph;
import com.ganesh.contiq.util.CriteriaUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.*;
import org.springframework.security.core.parameters.P;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class ParagraphRepositoryImplTest {


    @Mock
    CriteriaUtil criteriaUtil;

    @Mock
    ElasticsearchOperations elasticsearchOperations;

    @InjectMocks
    ParagraphRepositoryImpl paragraphRepository;

    SearchHits<File> mockFileSearchHits;

    SearchHits<Paragraph> mockParagraphSearchHits;

    List<File> fileList;

    List<Paragraph> paragraphList;

    File file;

    @BeforeEach
    public void setup(){

        File file1 = new File("1","file1.pdf","File1 content","user1");
        File file2 = new File("2","file2.pdf","File2 content","user1");
        file=file1;

        fileList= List.of(file1,file2);

        Paragraph paragraph1=new Paragraph("1","paragraph 3",1,"1",3);
        Paragraph paragraph2=new Paragraph("2","paragraph 1",1,"1",1);

        paragraphList=List.of(paragraph1,paragraph2);

        SearchHit<File> hit1 = mock(SearchHit.class);
        when(hit1.getContent()).thenReturn(file1);

        SearchHit<File> hit2 = mock(SearchHit.class);
        when(hit2.getContent()).thenReturn(file2);

        SearchHit<Paragraph> paragraphHit1 = mock(SearchHit.class);
        when(paragraphHit1.getContent()).thenReturn(paragraph1);

        SearchHit<Paragraph> paragraphHit2 = mock(SearchHit.class);
        when(paragraphHit2.getContent()).thenReturn(paragraph2);


        List<SearchHit<File>> searchHitList = List.of(hit1, hit2);
        mockFileSearchHits = new SearchHitsImpl<>(
                2,
                TotalHitsRelation.EQUAL_TO,
                1.0f,
                Duration.ZERO,
                "scroll id",
                "time id",
                searchHitList,
                null,
                null,
                null
        );

        List<SearchHit<Paragraph>> paragraphSearchHitList = List.of(paragraphHit1,paragraphHit2);
        mockParagraphSearchHits = new SearchHitsImpl<>(
                2,
                TotalHitsRelation.EQUAL_TO,
                1.0f,
                Duration.ZERO,
                "scroll id",
                "time id",
                paragraphSearchHitList,
                null,
                null,
                null
        );
    }

    @Test
    public void shouldReturnAListOfParagraphs_whenQueryParagraphsByFileIdAndKeyword(){
        when(criteriaUtil.isFileExists(anyString())).thenReturn(true);
        when(criteriaUtil.queryIfFileUploadedByUser(anyString(),anyString())).thenReturn(mockFileSearchHits);
        when(criteriaUtil.queryParagraphsByFileIdAndContent(anyString(),anyString())).thenReturn(mockParagraphSearchHits);

        List<Paragraph> actualParagraphList=paragraphRepository.findParagraphsByFileIdAndKeyword("1","content","user1");

        assertEquals(paragraphList.size(),actualParagraphList.size());
        assertEquals(paragraphList.get(0).getContent(),actualParagraphList.get(0).getContent());
        assertEquals(paragraphList.get(1).getContent(),actualParagraphList.get(1).getContent());
    }

    @Test
    public void shouldThrowFileNotFoundException_whenQueryParagraphsByInvalidFileIdAndKeyword(){
        when(criteriaUtil.isFileExists(anyString())).thenReturn(false);
        when(criteriaUtil.queryIfFileUploadedByUser(anyString(),anyString())).thenReturn(mockFileSearchHits);
        when(criteriaUtil.queryParagraphsByFileIdAndContent(anyString(),anyString())).thenReturn(mockParagraphSearchHits);

        assertThrows(FileNotFoundException.class,()->paragraphRepository.findParagraphsByFileIdAndKeyword("1","content","user1"));

    }

    @Test
    public void shouldThrowFileAccessDeniedException_whenQueryParagraphsByInvalidFileIdAndKeyword(){

        SearchHits<File> mockNoFileSearchHits = new SearchHitsImpl<>(
                2,
                TotalHitsRelation.EQUAL_TO,
                1.0f,
                Duration.ZERO,
                "scroll id",
                "time id",
                new ArrayList<>(),
                null,
                null,
                null
        );

        when(criteriaUtil.isFileExists(anyString())).thenReturn(true);
        when(criteriaUtil.queryIfFileUploadedByUser(anyString(),anyString())).thenReturn(mockNoFileSearchHits);
        when(criteriaUtil.queryParagraphsByFileIdAndContent(anyString(),anyString())).thenReturn(mockParagraphSearchHits);

        assertThrows(FileAccessDeniedException.class,()->paragraphRepository.findParagraphsByFileIdAndKeyword("1","content","user1"));

    }
}
