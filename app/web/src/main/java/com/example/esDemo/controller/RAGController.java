package com.example.esDemo.controller;

import com.example.esDemo.model.AppResult;
import com.example.esDemo.model.RAGQuery;
import com.example.esDemo.model.RAGResponse;
import com.example.esDemo.service.RAGService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class RAGController {

    private static final Logger log = LoggerFactory.getLogger(RAGController.class);

    private final RAGService ragService;

    public RAGController(RAGService ragService) {
        this.ragService = ragService;
    }

    /**
     * RAG 问答：检索知识库相关内容并生成回答
     */
    @PostMapping("/rag/query")
    public AppResult<RAGResponse> query(@RequestBody RAGQuery query) {
        if (query.getQuestion() == null || query.getQuestion().isBlank()) {
            return AppResult.isFail("问题不能为空");
        }

        log.info("RAG query: '{}' (topK={})", query.getQuestion(), query.getTopK());

        try {
            RAGResponse response = ragService.query(query);
            return AppResult.isSuccess(response);
        } catch (Exception e) {
            log.error("RAG query failed", e);
            return AppResult.isFail("问答失败: " + e.getMessage());
        }
    }
}
