package com.ganesh.contiq.controller;

import com.ganesh.contiq.DTO.ParagraphListDTO;
import com.ganesh.contiq.exception.FileAccessDeniedException;
import com.ganesh.contiq.exception.FileNotFoundException;
import com.ganesh.contiq.model.Paragraph;
import com.ganesh.contiq.service.ParagraphService;
import com.ganesh.contiq.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RequestMapping("/api/v1")
@RestController
public class ParagraphController {

    ParagraphService paragraphService;

    ParagraphController(@Autowired ParagraphService paragraphService){
        this.paragraphService=paragraphService;
    }

    @GetMapping("/files/{file-id}/search")
    public ResponseEntity<ParagraphListDTO> getParagraphsByFileIdAndKeyword(@PathVariable("file-id") String fileId, @RequestParam("keyword") String keyword, @AuthenticationPrincipal Jwt jwt) throws IOException, FileAccessDeniedException, FileNotFoundException {
        String userId= JwtUtil.getUserId(jwt);
        return new ResponseEntity<>(paragraphService.getParagraphsByFileIdAndKeyword(fileId,keyword,userId), HttpStatus.OK);
    }
}
