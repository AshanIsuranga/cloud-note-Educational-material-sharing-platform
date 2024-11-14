package com.isuranga.cloud_note.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.isuranga.cloud_note.model.PdfDocument;
import com.isuranga.cloud_note.model.Category;
import com.isuranga.cloud_note.model.DocumentType;
import com.isuranga.cloud_note.repository.PdfDocumentRepository;
import com.isuranga.cloud_note.service.PdfDocumentService;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pdf")
public class PdfDocumentController {

    @Autowired
    private PdfDocumentRepository pdfDocumentRepository;

    @Autowired
    private PdfDocumentService pdfDocumentService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadPdf(
            @RequestParam("file") MultipartFile file,
            @RequestParam("category") Category category,
            @RequestParam("type") DocumentType type) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("Please select a file to upload");
            }
            
            pdfDocumentService.savePdfDocument(file, category, type);
            return ResponseEntity.ok("PDF uploaded successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload PDF: " + e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to process PDF: " + e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<PdfDocument>> getAllPdfs() {
        List<PdfDocument> pdfs = pdfDocumentRepository.findAll();
        List<PdfDocument> simplifiedPdfs = pdfs.stream()
            .map(pdf -> new PdfDocument(pdf.getId(), pdf.getName(), pdf.getCategory(), pdf.getType()))
            .collect(Collectors.toList());
        return ResponseEntity.ok(simplifiedPdfs);
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<PdfDocument>> getPaginatedPdfs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PdfDocument> pdfPage = pdfDocumentRepository.findAll(pageable);
        return ResponseEntity.ok(pdfPage);
    }

    @GetMapping("/download/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<Resource> downloadPdf(@PathVariable Long id) {
        Optional<PdfDocument> pdfDocumentOptional = pdfDocumentRepository.findById(id);
        if (pdfDocumentOptional.isPresent()) {
            PdfDocument pdfDocument = pdfDocumentOptional.get();
            ByteArrayResource resource = new ByteArrayResource(pdfDocument.getContent());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + pdfDocument.getName() + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(pdfDocument.getContent().length)
                    .body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    @Transactional(readOnly = true)  //Using readOnly = true optimizes performance by indicating that this method will not modify any data in the database. This helps the transaction manager optimize resource usage
    public ResponseEntity<Page<PdfDocument>> searchPdfs(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) DocumentType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<PdfDocument> results;
        
        if (category != null && type != null) {
            results = pdfDocumentRepository.findByCategoryAndTypeAndNameContainingIgnoreCase(category, type, query, pageable);
        } else if (category != null) {
            results = pdfDocumentRepository.findByCategoryAndNameContainingIgnoreCase(category, query, pageable);
        } else if (type != null) {
            results = pdfDocumentRepository.findByTypeAndNameContainingIgnoreCase(type, query, pageable);
        } else {
            results = pdfDocumentRepository.findByNameContainingIgnoreCase(query, pageable);
        }

        return ResponseEntity.ok(results);  //http response to successfully parse data to client
    }
}