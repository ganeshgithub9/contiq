package com.ganesh.contiq.repository;

import com.ganesh.contiq.exception.FileAccessDeniedException;
import com.ganesh.contiq.exception.FileNotFoundException;
import com.ganesh.contiq.model.File;
import com.ganesh.contiq.model.Paragraph;
import com.ganesh.contiq.util.CriteriaUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;



import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class FileRepositoryImpl implements FileRepositoryCustom {

    @Autowired
    CriteriaUtil criteriaUtil;

    @Override
    public String findContentById(String fileId,String userId) throws FileNotFoundException, FileAccessDeniedException {

        if(!criteriaUtil.isFileExists(fileId))
            throw new FileNotFoundException("File with ID "+fileId+" not found");

        SearchHits<File> searchHits = criteriaUtil.queryIfFileUploadedByUser(fileId,userId);
        if(!searchHits.hasSearchHits())
            throw new FileAccessDeniedException("You are not authorized to access this file");

        return searchHits.getSearchHit(0).getContent().getContent();
    }

    @Override
    public List<File> findFilesByUserId(String userId) {

        SearchHits<File> searchHits = criteriaUtil.queryFilesByUserId(userId);

        return searchHits.stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> findFileIdsByUserIdAndKeyword(String userId,String keyword) {
        List<File> userFiles=findFilesByUserId(userId);
        for(File file:userFiles)
            log.info("{}",file.getId());
        return userFiles.stream()
                .map(File::getId)
                .filter(fileId -> {
                    SearchHits<Paragraph> hits = criteriaUtil.queryParagraphsByFileIdAndContent(fileId,keyword);
                    return hits.hasSearchHits();
                })
                .distinct()
                .toList();

    }

    @Override
    public File getFileMetaDataById(String fileId) {
        SearchHits<File> hits = criteriaUtil.queryFileMetaDataByFileId(fileId);

        if (hits.hasSearchHits()) {
            return hits.getSearchHit(0).getContent();
        } else {
            return null;
        }
    }
}

