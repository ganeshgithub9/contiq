package com.ganesh.contiq.service;

import com.ganesh.contiq.DTO.ParagraphListDTO;
import com.ganesh.contiq.exception.FileAccessDeniedException;
import com.ganesh.contiq.exception.FileNotFoundException;
import com.ganesh.contiq.model.Paragraph;

import java.util.List;

public interface ParagraphService {
    Paragraph saveParagraph(Paragraph paragraph);
    ParagraphListDTO getParagraphsByFileIdAndKeyword(String fileId, String keyword, String userId) throws FileAccessDeniedException, FileNotFoundException;
}
