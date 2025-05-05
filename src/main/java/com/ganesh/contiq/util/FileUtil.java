package com.ganesh.contiq.util;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;


public class FileUtil {
    public static String convertFileToBase64String(MultipartFile file) throws IOException {
        byte[] fileBytes = file.getBytes();
        return Base64.getEncoder().encodeToString(fileBytes);
    }

    public static String convertBase64StringToUTFString(String base64String) throws IOException {
        byte[] decoded = Base64.getDecoder().decode(base64String);
        return new String(decoded, StandardCharsets.UTF_8);
    }
}
