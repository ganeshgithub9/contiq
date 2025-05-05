package com.ganesh.contiq.repository;

import com.ganesh.contiq.exception.FileAccessDeniedException;
import com.ganesh.contiq.exception.FileNotFoundException;
import com.ganesh.contiq.model.File;

import java.util.List;

public interface FileRepositoryCustom {
    String findContentById(String fileId, String userId) throws FileNotFoundException, FileAccessDeniedException;
    List<File> findFilesByUserId(String userId);

    List<String> findFileIdsByUserIdAndKeyword(String userId, String keyword);
    File getFileMetaDataById(String fileId);
}