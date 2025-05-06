package com.ganesh.contiq.service;

import com.ganesh.contiq.DTO.FileMetaDataDTO;
import com.ganesh.contiq.exception.FileAccessDeniedException;
import com.ganesh.contiq.exception.FileNotFoundException;
import com.ganesh.contiq.model.File;
import com.ganesh.contiq.model.Paragraph;
import com.ganesh.contiq.repository.FileRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Primary
public class CustomFileService implements FileService{

    PdfProcessingService pdfProcessingService;
    FileRepository fileRepository;
    ParagraphService paragraphService;
    ModelMapper modelMapper;
    CustomFileService(@Autowired PdfProcessingService pdfProcessingService, @Autowired FileRepository fileRepository, @Autowired ParagraphService paragraphService, @Autowired ModelMapper modelMapper){
        this.pdfProcessingService=pdfProcessingService;
        this.fileRepository=fileRepository;
        this.paragraphService=paragraphService;
        this.modelMapper=modelMapper;
    }

    @Override
    public void saveFiles(List<MultipartFile> files,String userId) throws IOException {
        // create file es documents
        log.info("started saving files and paragraphs");
        // then paragraph splitting
            for(MultipartFile file: files){
                String content=pdfProcessingService.extractTextFromPdf(file);
                File newFile=new File();
                newFile.setId(UUID.randomUUID().toString());
                newFile.setName(file.getOriginalFilename());
                newFile.setContent(content);
                newFile.setUserId(userId);
                File savedFile=fileRepository.save(newFile);
                List<Paragraph> paragraphList=pdfProcessingService.processPdf(file);
                for(Paragraph paragraph: paragraphList){
                    paragraph.setFileId(savedFile.getId());
                    paragraphService.saveParagraph(paragraph);
                }
            }
        log.info("completed saving files and paragraphs");
    }

    @Override
    public String getFileContentById(String fileId,String userId) throws FileNotFoundException, FileAccessDeniedException {

        return fileRepository.findContentById(fileId,userId);
    }

    @Override
    public List<FileMetaDataDTO> getFilesByUserId(String userId) {
        return fileRepository.findFilesByUserId(userId).stream().map(file -> modelMapper.map(file, FileMetaDataDTO.class)).toList();
    }

    @Override
    public List<FileMetaDataDTO> getFilesByUserIdAndKeyword(String userId, String keyword) {
        log.info("started getFilesByUserIdAndKeyword method with userId {} and keyword {}",userId,keyword);
        List<String> fileIDs=fileRepository.findFileIdsByUserIdAndKeyword(userId,keyword);
        for(String id:fileIDs)
            log.info("{}",id);
        return fileIDs.stream()
                .map(fileId->fileRepository.getFileMetaDataById(fileId))
                .map(file->modelMapper.map(file,FileMetaDataDTO.class))
                .toList();
    }
}
