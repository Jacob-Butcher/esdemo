package com.example.esDemo.service.impl;

import com.example.esDemo.model.RAGQuery;
import com.example.esDemo.model.RAGResponse;
import com.example.esDemo.service.RAGService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RAGServiceImpl implements RAGService {

    private static final Logger log = LoggerFactory.getLogger(RAGServiceImpl.class);

    private final VectorStore vectorStore;
    private final ChatClient.Builder chatClientBuilder;

    public RAGServiceImpl(VectorStore vectorStore, ChatClient.Builder chatClientBuilder) {
        this.vectorStore = vectorStore;
        this.chatClientBuilder = chatClientBuilder;
    }

    @Override
    public RAGResponse query(RAGQuery query) {
        log.info("RAG query: question='{}' topK={}", query.getQuestion(), query.getTopK());

        // 1. 向量检索：从 ES 中搜索最相关的分块
        SearchRequest searchRequest = SearchRequest.builder()
                .query(query.getQuestion())
                .topK(query.getTopK())
                .similarityThreshold(query.getSimilarityThreshold())
                .build();

        List<org.springframework.ai.document.Document> results = vectorStore.similaritySearch(searchRequest);
        log.info("Retrieved {} relevant chunks", results.size());

        if (results.isEmpty()) {
            return RAGResponse.builder()
                    .question(query.getQuestion())
                    .answer("未在知识库中找到相关信息，请尝试上传相关文档后再提问。")
                    .sources(List.of())
                    .build();
        }

        // 2. 构建上下文 Prompt
        String context = results.stream()
                .map(doc -> doc.getText())
                .collect(Collectors.joining("\n\n---\n\n"));

        String prompt = """
                你是一个知识库问答助手。请根据以下提供的上下文信息回答用户的问题。
                如果上下文中没有足够的信息，请如实告知用户，不要编造内容。
                请用中文回答。

                === 上下文信息 ===
                %s

                === 用户问题 ===
                %s

                === 回答 ==="""
                .formatted(context, query.getQuestion());

        // 3. 调用 LLM 生成回答
        String answer = chatClientBuilder.build()
                .prompt()
                .user(prompt)
                .call()
                .content();

        log.info("RAG answer generated, length={}", answer != null ? answer.length() : 0);

        // 4. 提取引用来源
        List<RAGResponse.Source> sources = results.stream()
                .map(doc -> {
                    String content = doc.getText();
                    return RAGResponse.Source.builder()
                            .documentName(doc.getMetadata().getOrDefault("document_name", "unknown").toString())
                            .chunkIndex(doc.getMetadata().get("chunk_index") instanceof Integer i ? i : -1)
                            .content(content.length() > 200 ? content.substring(0, 200) + "..." : content)
                            .build();
                })
                .collect(Collectors.toList());

        return RAGResponse.builder()
                .question(query.getQuestion())
                .answer(answer)
                .sources(sources)
                .build();
    }
}
