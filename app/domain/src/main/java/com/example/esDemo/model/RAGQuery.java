package com.example.esDemo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RAGQuery {
    /** 用户问题 */
    private String question;

    /** 检索返回的最大相关片段数 */
    @Builder.Default
    private Integer topK = 4;

    /** 相似度阈值（0~1），低于此值的片段会被过滤 */
    @Builder.Default
    private Double similarityThreshold = 0.3;
}
