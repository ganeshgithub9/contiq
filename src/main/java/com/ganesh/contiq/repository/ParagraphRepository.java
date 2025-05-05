package com.ganesh.contiq.repository;

import com.ganesh.contiq.model.Paragraph;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParagraphRepository extends ElasticsearchRepository<Paragraph, String>, ParagraphRepositoryCustom {
    List<Paragraph> findAllByFileId(String fileId);
}

