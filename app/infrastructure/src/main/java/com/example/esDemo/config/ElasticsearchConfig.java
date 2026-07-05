package com.example.esDemo.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * Elasticsearch 配置：确保文档元数据索引存在。
 * 向量分块索引由 Spring AI ElasticsearchVectorStore 自动管理。
 */
@Configuration
public class ElasticsearchConfig {

    private static final Logger log = LoggerFactory.getLogger(ElasticsearchConfig.class);

    public static final String DOCUMENTS_INDEX = "rag_documents";

    private final ElasticsearchClient esClient;

    public ElasticsearchConfig(ElasticsearchClient esClient) {
        this.esClient = esClient;
    }

    @PostConstruct
    public void initializeIndex() {
        try {
            ExistsRequest existsReq = ExistsRequest.of(e -> e.index(DOCUMENTS_INDEX));
            boolean exists = esClient.indices().exists(existsReq).value();

            if (!exists) {
                CreateIndexRequest createReq = CreateIndexRequest.of(c -> c
                        .index(DOCUMENTS_INDEX)
                        .mappings(m -> m
                                .properties("name", p -> p.keyword(k -> k.ignoreAbove(256)))
                                .properties("contentType", p -> p.keyword(k -> k.ignoreAbove(128)))
                                .properties("size", p -> p.long_(l -> l))
                                .properties("chunkCount", p -> p.integer(i -> i))
                                .properties("createdAt", p -> p.date(d -> d))
                        )
                );
                esClient.indices().create(createReq);
                log.info("Created ES index: {}", DOCUMENTS_INDEX);
            }
        } catch (IOException e) {
            log.error("Failed to initialize Elasticsearch index: {}", DOCUMENTS_INDEX, e);
            throw new RuntimeException("ES index initialization failed", e);
        }
    }
}
