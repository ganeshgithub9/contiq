package com.ganesh.contiq.util;

import com.ganesh.contiq.model.File;
import com.ganesh.contiq.model.Paragraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Component;

@Component
public class CriteriaUtil {
    @Autowired
    private ElasticsearchOperations elasticsearchOperations;
    public boolean isFileExists(String fileId){
        Criteria isFileExistsCriteria = new Criteria("id").is(fileId);
        CriteriaQuery isFileExistsQuery = new CriteriaQuery(isFileExistsCriteria);
        SearchHits<File> isFileExistsSearchHits = elasticsearchOperations.search(isFileExistsQuery, File.class);
        return isFileExistsSearchHits.hasSearchHits();
    }

    public SearchHits<File> queryIfFileUploadedByUser(String fileId, String userId){
        Criteria userAndFileCriteria=new Criteria("id").is(fileId)
                .and(new Criteria("userId").is(userId));

        CriteriaQuery userAndFileQuery=new CriteriaQuery(userAndFileCriteria);
        return elasticsearchOperations.search(userAndFileQuery,File.class);
    }

    public SearchHits<File> queryFilesByUserId(String userId){
        Criteria criteria = new Criteria("userId").is(userId);
        CriteriaQuery query = new CriteriaQuery(criteria);
        query.addFields("id","name");
        return elasticsearchOperations.search(query,File.class);
    }

    public SearchHits<File> queryFileMetaDataByFileId(String fileId){
        Criteria criteria=new Criteria("id").is(fileId);
        CriteriaQuery query = new CriteriaQuery(criteria);
        query.addFields("id","name");
        return elasticsearchOperations.search(query,File.class);
    }

    public SearchHits<Paragraph> queryParagraphsByFileIdAndContent(String fileId,String keyword){
        Criteria criteria = new Criteria("fileId").is(fileId)
                .and(new Criteria("content").contains(keyword));

        CriteriaQuery query = new CriteriaQuery(criteria);
        query.addSort(Sort.by(Sort.Order.asc("pageNumber"), Sort.Order.asc("paragraphNumber")));
        return elasticsearchOperations.search(query, Paragraph.class);
    }
}
