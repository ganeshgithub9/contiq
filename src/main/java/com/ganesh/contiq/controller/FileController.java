package com.ganesh.contiq.controller;

import com.ganesh.contiq.DTO.FileMetaDataDTO;
import com.ganesh.contiq.exception.FileAccessDeniedException;
import com.ganesh.contiq.exception.FileNotFoundException;
import com.ganesh.contiq.service.CustomPdfProcessingService;
import com.ganesh.contiq.service.FileService;
import com.ganesh.contiq.service.PdfProcessingService;
import com.ganesh.contiq.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequestMapping("/api/v1")
@RestController
public class FileController {

    FileService fileService;


    FileController(@Autowired FileService fileService){
        this.fileService=fileService;
    }

    @PostMapping("/files")
    public ResponseEntity<String> uploadFiles(@RequestParam("files") List<MultipartFile> files, @AuthenticationPrincipal Jwt jwt) throws IOException {
        //System.out.println("started uploadFiles method from File controller");
        log.info("started uploadFiles method from File controller");
        String userId= JwtUtil.getUserId(jwt);
        fileService.saveFiles(files,userId);
        //System.out.println("completed uploadFiles method from File controller");
        log.info("completed uploadFiles method from File controller");
        return new ResponseEntity<>("Files uploaded successfully", HttpStatus.CREATED);
    }

    @GetMapping("/files")
    public ResponseEntity<List<FileMetaDataDTO>> getFilesByUserId( @AuthenticationPrincipal Jwt jwt) {
        //System.out.println(jwt.getSubject()+"   "+jwt.getClaimAsString("https://contiq.ganesh.com/name"));
        String userId= JwtUtil.getUserId(jwt);
        return new ResponseEntity<>(fileService.getFilesByUserId(userId), HttpStatus.OK);
    }

    @GetMapping("/files/{file-id}")
    public ResponseEntity<String> getFileContentById( @PathVariable("file-id") String fileId, @AuthenticationPrincipal Jwt jwt) throws IOException, FileAccessDeniedException, FileNotFoundException {
        String userId= JwtUtil.getUserId(jwt);
        return new ResponseEntity<>(fileService.getFileContentById(fileId,userId), HttpStatus.OK);
    }

    @GetMapping("/files/search")
    public ResponseEntity<List<FileMetaDataDTO>> getFilesByUserIdAndKeyword( @RequestParam("keyword") String keyword, @AuthenticationPrincipal Jwt jwt) {
        log.info("started getFilesByUserIdAndKeyword method");
        String userId= JwtUtil.getUserId(jwt);
        return new ResponseEntity<>(fileService.getFilesByUserIdAndKeyword(userId,keyword), HttpStatus.OK);
    }


}
