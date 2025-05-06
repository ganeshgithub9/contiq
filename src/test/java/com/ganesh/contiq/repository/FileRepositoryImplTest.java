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
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.SearchHitsImpl;
import org.springframework.data.elasticsearch.core.TotalHitsRelation;

import javax.management.Query;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
public class FileRepositoryImplTest {

    @Mock
    CriteriaUtil criteriaUtil;

    @Spy
    @InjectMocks
    FileRepositoryImpl fileRepository;

    SearchHits<File> mockFileSearchHits;

    SearchHits<Paragraph> mockParagraphSearchHits;

    SearchHits<File> mockNoFileSearchHits;

    List<File> fileList;

    File file;

    @BeforeEach
    public void setup(){

        File file1 = new File("1","file1.pdf","File1 content","user1");
        File file2 = new File("2","file2.pdf","File2 content","user1");
        file=file1;

        fileList=List.of(file1,file2);

        Paragraph paragraph1=new Paragraph("1","paragraph 3",1,"1",3);
        Paragraph paragraph2=new Paragraph("2","paragraph 1",1,"1",1);

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

        mockNoFileSearchHits = new SearchHitsImpl<>(
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
    public void shouldReturnFileContent_whenFindContentById(){
        when(criteriaUtil.isFileExists(anyString())).thenReturn(true);
        when(criteriaUtil.queryIfFileUploadedByUser(anyString(),anyString())).thenReturn(mockFileSearchHits);

        String actualContent=fileRepository.findContentById("1","user1"),expectedContent="File1 content";

        assertEquals(expectedContent,actualContent);
    }

    @Test
    public void shouldThrowFileNotFoundException_whenFindContentByInvalidId(){
        when(criteriaUtil.isFileExists(anyString())).thenReturn(false);
        when(criteriaUtil.queryIfFileUploadedByUser(anyString(),anyString())).thenReturn(mockFileSearchHits);

        assertThrows(FileNotFoundException.class,()->fileRepository.findContentById("invalidId","user1"));
    }

    @Test
    public void shouldThrowFileAccessDeniedException_whenFindContentByIdCalledByUnauthorizedUser(){

        when(criteriaUtil.isFileExists(anyString())).thenReturn(true);

        when(criteriaUtil.queryIfFileUploadedByUser(anyString(),anyString())).thenReturn(mockNoFileSearchHits);

        assertThrows(FileAccessDeniedException.class,()->fileRepository.findContentById("1","user1"));
    }

    @Test
    public void shouldReturnAListOfFiles_whenFindFilesByUserId(){
        when(criteriaUtil.isFileExists(anyString())).thenReturn(true);
        when(criteriaUtil.queryFilesByUserId(anyString())).thenReturn(mockFileSearchHits);

        List<File> actualFiles=fileRepository.findFilesByUserId("user1");

        assertEquals(mockFileSearchHits.getSearchHits().size(),actualFiles.size());
        assertEquals(mockFileSearchHits.getSearchHits().get(0).getContent().getContent(),actualFiles.get(0).getContent());
        assertEquals(mockFileSearchHits.getSearchHits().get(1).getContent().getContent(),actualFiles.get(1).getContent());
    }

    @Test
    public void shouldReturnAListOfFileIDs_whenFindFileIDsByUserIdAndKeyword(){
        doReturn(fileList).when(fileRepository).findFilesByUserId(anyString());
        when(criteriaUtil.queryParagraphsByFileIdAndContent(anyString(),anyString())).thenReturn(mockParagraphSearchHits);

        List<String> actualFileIDs=fileRepository.findFileIdsByUserIdAndKeyword("user1","content");

        assertEquals(2,actualFileIDs.size());
    }

    @Test
    public void shouldReturnFile_whenGetFileMetaDataById(){

        when(criteriaUtil.queryFileMetaDataByFileId(anyString())).thenReturn(mockFileSearchHits);

        File actualFile=fileRepository.getFileMetaDataById("1");

        String expectedFileContent="File1 content";
        assertEquals(expectedFileContent,actualFile.getContent());
    }

    @Test
    public void shouldReturnNull_whenGetFileMetaDataByInvalidId(){

        when(criteriaUtil.queryFileMetaDataByFileId(anyString())).thenReturn(mockNoFileSearchHits);

        assertNull(fileRepository.getFileMetaDataById("1"));

    }
}
