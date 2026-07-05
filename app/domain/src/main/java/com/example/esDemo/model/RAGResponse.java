package com.example.esDemo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RAGResponse {
    /** 原始问题 */
    private String question;

    /** LLM 生成的回答 */
    private String answer;

    /** 引用的来源片段 */
    private List<Source> sources;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Source {
        /** 来源文档名称 */
        private String documentName;

        /** 分块序号 */
        private Integer chunkIndex;

        /** 片段内容摘要（截取前 200 字符） */
        private String content;
    }
}
