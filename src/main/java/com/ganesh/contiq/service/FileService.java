package com.ganesh.contiq.service;

import com.ganesh.contiq.DTO.FileContentDTO;
import com.ganesh.contiq.DTO.FileMetaDataDTO;
import com.ganesh.contiq.DTO.FileMetaDataListDTO;
import com.ganesh.contiq.exception.FileAccessDeniedException;
import com.ganesh.contiq.exception.FileNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileService {
    void saveFiles(List<MultipartFile> files, String userId) throws IOException;

    FileContentDTO getFileContentById(String fileId, String userId) throws FileNotFoundException, FileAccessDeniedException;

    FileMetaDataListDTO getFilesByUserId(String userId);

    FileMetaDataListDTO getFilesByUserIdAndKeyword(String userId, String keyword);
}
