package com.example.esDemo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Document {
    /** Elasticsearch document _id */
    private String id;

    /** 原始文件名 */
    private String name;

    /** MIME 类型，如 application/pdf */
    private String contentType;

    /** 文件大小（字节） */
    private Long size;

    /** 分块数量 */
    private Integer chunkCount;

    /** 上传时间（ISO-8601 字符串） */
    @Builder.Default
    private String createdAt = Instant.now().toString();
}
