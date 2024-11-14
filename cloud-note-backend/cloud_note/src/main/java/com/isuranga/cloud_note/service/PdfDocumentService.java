package com.isuranga.cloud_note.service;

import com.isuranga.cloud_note.model.PdfDocument;
import com.isuranga.cloud_note.model.Category;
import com.isuranga.cloud_note.model.DocumentType;
import com.isuranga.cloud_note.repository.PdfDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@Service
public class PdfDocumentService {

    @Autowired
    private PdfDocumentRepository pdfDocumentRepository;

    public PdfDocument savePdfDocument(MultipartFile file, Category category, DocumentType type) throws IOException, NoSuchAlgorithmException {
        byte[] content = file.getBytes();
        String fileHash = calculateFileHash(content);

        Optional<PdfDocument> existingDocument = pdfDocumentRepository.findByFileHash(fileHash);
        if (existingDocument.isPresent()) {
            throw new RuntimeException("This file has already been uploaded.");
        }

        PdfDocument pdfDocument = new PdfDocument();
        pdfDocument.setName(file.getOriginalFilename());
        pdfDocument.setContent(content);
        pdfDocument.setCategory(category);
        pdfDocument.setType(type);
        pdfDocument.setFileHash(fileHash);

        return pdfDocumentRepository.save(pdfDocument);
    }

    private String calculateFileHash(byte[] content) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedHash = digest.digest(content);
        return bytesToHex(encodedHash);
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}