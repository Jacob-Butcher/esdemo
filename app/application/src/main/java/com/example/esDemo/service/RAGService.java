package com.example.esDemo.service;

import com.example.esDemo.model.RAGQuery;
import com.example.esDemo.model.RAGResponse;

/**
 * RAG 问答服务：检索相关片段 → 增强 Prompt → LLM 生成回答
 */
public interface RAGService {

    /**
     * 基于知识库的检索增强生成问答
     */
    RAGResponse query(RAGQuery query);
}
