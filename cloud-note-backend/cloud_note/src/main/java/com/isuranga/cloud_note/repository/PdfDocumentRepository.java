package com.isuranga.cloud_note.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.isuranga.cloud_note.model.PdfDocument;
import com.isuranga.cloud_note.model.Category;
import com.isuranga.cloud_note.model.DocumentType;

import java.util.Optional;

public interface PdfDocumentRepository extends JpaRepository<PdfDocument, Long> {
    @Query("SELECT p FROM PdfDocument p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<PdfDocument> findByNameContainingIgnoreCase(@Param("query") String query, Pageable pageable);

    @Query("SELECT p FROM PdfDocument p WHERE p.category = :category AND LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<PdfDocument> findByCategoryAndNameContainingIgnoreCase(@Param("category") Category category, @Param("query") String query, Pageable pageable);

    @Query("SELECT p FROM PdfDocument p WHERE p.type = :type AND LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<PdfDocument> findByTypeAndNameContainingIgnoreCase(@Param("type") DocumentType type, @Param("query") String query, Pageable pageable);

    @Query("SELECT p FROM PdfDocument p WHERE p.category = :category AND p.type = :type AND LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<PdfDocument> findByCategoryAndTypeAndNameContainingIgnoreCase(@Param("category") Category category, @Param("type") DocumentType type, @Param("query") String query, Pageable pageable);

    Optional<PdfDocument> findByFileHash(String fileHash);
}