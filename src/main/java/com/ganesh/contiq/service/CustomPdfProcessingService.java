package com.ganesh.contiq.service;

import com.ganesh.contiq.model.Paragraph;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.COSArrayList;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Primary
public class CustomPdfProcessingService implements PdfProcessingService {

    public List<Paragraph> processPdf(MultipartFile file) throws IOException {
        List<Paragraph> paragraphList=new ArrayList<>();
        //System.out.println("started processPdf method from PdfProcessingService");
        log.info("started processPdf method for file {} from PdfProcessingService",file.getOriginalFilename());
        InputStream inputStream=file.getInputStream();
        try (PDDocument document = PDDocument.load(inputStream)) {
            PDFTextStripper pdfStripper = createPdfTextStripper();
            int totalPages = document.getNumberOfPages();

            for (int i = 1; i <= totalPages; i++) {
                pdfStripper.setStartPage(i);
                pdfStripper.setEndPage(i);
                String pageText = pdfStripper.getText(document);

                String[] paragraphs = pageText.split("\\n\\s*\\n");
                int paraNumber=0;
                for (String para : paragraphs) {
                    if (!para.trim().isEmpty()) {
                        paraNumber++;
                        Paragraph paragraph = new Paragraph();
                        paragraph.setId(UUID.randomUUID().toString());
                        paragraph.setContent(para.trim());
                        paragraph.setPageNumber(i);
                        paragraph.setFileId(file.getOriginalFilename());
                        paragraph.setParagraphNumber(paraNumber);
                        paragraphList.add(paragraph);
                    }
                }
            }
        }
        catch (IOException e) {
            throw new IOException("Error processing the PDF file. Make sure there are no corrupt files", e);
        }
        //System.out.println("completed processPdf method from PdfProcessingService");
        log.info("completed processPdf method for file {} from PdfProcessingService",file.getOriginalFilename());
        return paragraphList;
    }

    @Override
    public String extractTextFromPdf(MultipartFile file) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper stripper = createPdfTextStripper();
            return stripper.getText(document);
        } catch (IOException e) {
            throw new IOException("Failed to extract text from PDF", e);
        }
    }

    PDFTextStripper createPdfTextStripper() throws IOException {
        return new PDFTextStripper();
    }
}

