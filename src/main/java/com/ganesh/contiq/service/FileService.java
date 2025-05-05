package com.ganesh.contiq.service;

import com.ganesh.contiq.DTO.FileMetaDataDTO;
import com.ganesh.contiq.exception.FileAccessDeniedException;
import com.ganesh.contiq.exception.FileNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileService {
    void saveFiles(List<MultipartFile> files, String userId) throws IOException;

    String getFileContentById(String fileId,String userId) throws IOException, FileNotFoundException, FileAccessDeniedException;

    List<FileMetaDataDTO> getFilesByUserId(String userId);

    List<FileMetaDataDTO> getFilesByUserIdAndKeyword(String userId, String keyword);
}
