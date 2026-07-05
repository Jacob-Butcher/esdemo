package com.example.esDemo.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import com.example.esDemo.config.ElasticsearchConfig;
import com.example.esDemo.model.Document;
import com.example.esDemo.parser.DocumentParser;
import com.example.esDemo.service.DocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DocumentServiceImpl implements DocumentService {

    private static final Logger log = LoggerFactory.getLogger(DocumentServiceImpl.class);
    private static final int MAX_RESULTS = 100;

    private final VectorStore vectorStore;
    private final ElasticsearchClient esClient;
    private final DocumentParser documentParser;

    public DocumentServiceImpl(VectorStore vectorStore,
                               ElasticsearchClient esClient,
                               DocumentParser documentParser) {
        this.vectorStore = vectorStore;
        this.esClient = esClient;
        this.documentParser = documentParser;
    }

    @Override
    public Document upload(MultipartFile file) throws IOException {
        log.info("Uploading document: {} ({} bytes)", file.getOriginalFilename(), file.getSize());

        // 1. 解析 + 分块
        List<String> chunks = documentParser.parseAndChunk(file);

        if (chunks.isEmpty()) {
            throw new IllegalArgumentException("Document contains no extractable text: " + file.getOriginalFilename());
        }

        // 2. 创建文档元数据记录
        String docId = UUID.randomUUID().toString();
        Document doc = Document.builder()
                .id(docId)
                .name(file.getOriginalFilename())
                .contentType(file.getContentType())
                .size(file.getSize())
                .chunkCount(chunks.size())
                .build();

        // 3. 存储文档元数据到 ES
        esClient.index(i -> i
                .index(ElasticsearchConfig.DOCUMENTS_INDEX)
                .id(docId)
                .document(doc)
        );
        log.info("Document metadata saved: {} ({} chunks)", docId, chunks.size());

        // 4. 构建 Spring AI Documents 并存入 VectorStore（自动向量化）
        List<org.springframework.ai.document.Document> aiDocs = new ArrayList<>();
        for (int i = 0; i < chunks.size(); i++) {
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("document_id", docId);
            metadata.put("document_name", doc.getName());
            metadata.put("chunk_index", i);
            aiDocs.add(new org.springframework.ai.document.Document(chunks.get(i), metadata));
        }

        try {
            vectorStore.add(aiDocs);
            log.info("Chunks vectorized and stored: {} chunks for document {}", chunks.size(), docId);
        } catch (Exception e) {
            log.error("Vector store add failed: {}", e.getMessage(), e);
            // Clean up document metadata on failure
            esClient.delete(d -> d.index(ElasticsearchConfig.DOCUMENTS_INDEX).id(docId));
            throw new RuntimeException("Failed to store document chunks: " + e.getMessage(), e);
        }

        return doc;
    }

    @Override
    public List<Document> listAll() {
        try {
            var response = esClient.search(s -> s
                            .index(ElasticsearchConfig.DOCUMENTS_INDEX)
                            .size(MAX_RESULTS)
                            .query(q -> q.matchAll(m -> m)),
                    Document.class);

            return response.hits().hits().stream()
                    .map(hit -> {
                        Document doc = hit.source();
                        if (doc != null) {
                            doc.setId(hit.id());
                        }
                        return doc;
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Failed to list documents", e);
            return List.of();
        }
    }

    @Override
    public void deleteById(String documentId) {
        log.info("Deleting document: {}", documentId);

        try {
            // 删除文档元数据
            esClient.delete(d -> d
                    .index(ElasticsearchConfig.DOCUMENTS_INDEX)
                    .id(documentId)
            );

            // 删除所有关联的向量分块
            esClient.deleteByQuery(d -> d
                    .index("rag_knowledge_base")
                    .query(q -> q.term(t -> t
                            .field("metadata.document_id")
                            .value(documentId)
                    ))
            );

            log.info("Document and chunks deleted: {}", documentId);
        } catch (IOException e) {
            log.error("Failed to delete document: {}", documentId, e);
            throw new RuntimeException("Failed to delete document: " + documentId, e);
        }
    }
}
