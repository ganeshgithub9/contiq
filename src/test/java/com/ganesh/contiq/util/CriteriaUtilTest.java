package com.ganesh.contiq.util;

import com.ganesh.contiq.model.File;
import com.ganesh.contiq.model.Paragraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.*;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.SqlQuery;

@SpringBootTest
public class CriteriaUtilTest {

    @Mock
    ElasticsearchOperations elasticsearchOperations;

    @InjectMocks
    CriteriaUtil criteriaUtil;

    SearchHits<File> mockSearchHits;

    SearchHits<Paragraph> paragraphMockSearchHits;

    @BeforeEach
    public void setup(){

        File file1 = new File("1","file1.pdf","File1 content","user1");
        File file2 = new File("2","file2.pdf","File2 content","user1");

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
        mockSearchHits = new SearchHitsImpl<>(
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
        paragraphMockSearchHits = new SearchHitsImpl<>(
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
    public void shouldReturnTrue_whenFileExists(){

        when(elasticsearchOperations.search(any(Query.class),eq(File.class))).thenReturn(mockSearchHits);

        assert(criteriaUtil.isFileExists("1"));
    }

    @Test
    public void shouldReturnMatchedSearchHit_whenQueryIfFileUploadedByUser(){

        when(elasticsearchOperations.search(any(Query.class),eq(File.class))).thenReturn(mockSearchHits);

        SearchHits<File> actualSearchHits=criteriaUtil.queryIfFileUploadedByUser("1","user1");

        assertEquals(2,actualSearchHits.getSearchHits().size());
    }

    @Test
    public void shouldReturnMatchedSearchHits_whenQueryFilesByUserId(){

        when(elasticsearchOperations.search(any(Query.class),eq(File.class))).thenReturn(mockSearchHits);

        SearchHits<File> actualSearchHits=criteriaUtil.queryFilesByUserId("user1");

        assertEquals(2,actualSearchHits.getSearchHits().size());
    }

    @Test
    public void shouldReturnMatchedSearchHits_whenQueryFileMetaDataByFileId(){

        when(elasticsearchOperations.search(any(Query.class),eq(File.class))).thenReturn(mockSearchHits);

        SearchHits<File> actualSearchHits=criteriaUtil.queryFileMetaDataByFileId("file1");

        assertEquals(2,actualSearchHits.getSearchHits().size());
    }

    @Test
    public void shouldReturnMatchedSearchHits_whenQueryParagraphsByFileIdAndContent(){

        when(elasticsearchOperations.search(any(Query.class),eq(Paragraph.class))).thenReturn(paragraphMockSearchHits);

        SearchHits<Paragraph> actualSearchHits=criteriaUtil.queryParagraphsByFileIdAndContent("1","paragraph");

        assertEquals(2,actualSearchHits.getSearchHits().size());
    }
}
