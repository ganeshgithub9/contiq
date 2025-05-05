package com.ganesh.contiq.repository;

import com.ganesh.contiq.exception.FileAccessDeniedException;
import com.ganesh.contiq.exception.FileNotFoundException;
import com.ganesh.contiq.model.Paragraph;

import java.util.List;

public interface ParagraphRepositoryCustom {
    List<Paragraph> findParagraphsByFileIdAndKeyword(String fileId, String keyword, String userId) throws FileAccessDeniedException, FileNotFoundException;
}
