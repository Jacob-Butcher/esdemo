package com.example.esDemo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentChunk {
    /** ES 文档 _id */
    private String id;

    /** 所属文档 ID */
    private String documentId;

    /** 所属文档名称 */
    private String documentName;

    /** 分块序号（从 0 开始） */
    private Integer chunkIndex;

    /** 分块文本内容 */
    private String content;

    /** 向量嵌入（存储时用，检索时不返回） */
    private double[] embedding;

    /** 元数据 */
    private Map<String, Object> metadata;

    /** 创建时间（ISO-8601 字符串） */
    @Builder.Default
    private String createdAt = Instant.now().toString();
}
