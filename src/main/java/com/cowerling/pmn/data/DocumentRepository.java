package com.cowerling.pmn.data;

import com.cowerling.pmn.domain.document.Document;

import java.util.List;

public interface DocumentRepository {
    List<Document> findDocuments();
    Document findDocumentById(Long id);
}
