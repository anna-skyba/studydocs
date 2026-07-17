package com.upjs.studydocs.repository;

import com.upjs.studydocs.entity.StudyDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyDocumentRepository extends JpaRepository<StudyDocument, Long> {
}