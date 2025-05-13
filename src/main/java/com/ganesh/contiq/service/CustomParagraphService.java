package com.ganesh.contiq.service;

import com.ganesh.contiq.DTO.ParagraphListDTO;
import com.ganesh.contiq.exception.FileAccessDeniedException;
import com.ganesh.contiq.exception.FileNotFoundException;
import com.ganesh.contiq.model.Paragraph;
import com.ganesh.contiq.repository.ParagraphRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Primary
public class CustomParagraphService implements ParagraphService{

    ParagraphRepository paragraphRepository;

    CustomParagraphService(@Autowired ParagraphRepository paragraphRepository){
        this.paragraphRepository=paragraphRepository;
    }

    @Override
    public Paragraph saveParagraph(Paragraph paragraph) {
        return paragraphRepository.save(paragraph);
    }

    @Override
    public ParagraphListDTO getParagraphsByFileIdAndKeyword(String fileId, String keyword, String userId) throws FileAccessDeniedException, FileNotFoundException {
        List<Paragraph> paragraphList=paragraphRepository.findParagraphsByFileIdAndKeyword(fileId,keyword,userId);

        return new ParagraphListDTO(paragraphList);
    }
}
