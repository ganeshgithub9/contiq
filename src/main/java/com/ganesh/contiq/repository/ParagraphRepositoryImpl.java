package com.ganesh.contiq.repository;

import com.ganesh.contiq.exception.FileAccessDeniedException;
import com.ganesh.contiq.exception.FileNotFoundException;
import com.ganesh.contiq.model.File;
import com.ganesh.contiq.model.Paragraph;
import com.ganesh.contiq.util.CriteriaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;

import java.util.List;


public class ParagraphRepositoryImpl implements ParagraphRepositoryCustom{

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @Autowired
    CriteriaUtil criteriaUtil;

    @Override
    public List<Paragraph> findParagraphsByFileIdAndKeyword(String fileId, String keyword, String userId) throws FileAccessDeniedException, FileNotFoundException {

        if(!criteriaUtil.isFileExists(fileId))
            throw new FileNotFoundException("File with ID "+fileId+" not found");

        SearchHits<File> fileHits = criteriaUtil.queryIfFileUploadedByUser(fileId,userId);
        if(!fileHits.hasSearchHits())
            throw new FileAccessDeniedException("You are not authorized to access this file");

        SearchHits<Paragraph> hits = criteriaUtil.queryParagraphsByFileIdAndContent(fileId,keyword);

        return hits.stream()
                .map(SearchHit::getContent)
                .toList();
    }
}
