package com.ganesh.contiq.service;

import com.ganesh.contiq.model.Paragraph;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PdfProcessingService {
    List<Paragraph> processPdf(MultipartFile file) throws IOException;
    String extractTextFromPdf(MultipartFile file) throws IOException;
}
