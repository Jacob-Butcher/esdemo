# RAG 知识库检索系统 — 设计文档

## 1. 概述

基于 Elasticsearch + Spring AI Alibaba 实现 RAG（Retrieval-Augmented Generation）知识库检索系统。用户上传文档（PDF/Word/TXT），系统解析、分块、向量化后存入 ES，提问时检索相关片段并交给 LLM 生成回答。附带简单前端页面。

## 2. 系统架构

```
┌────────────────────────────────────────────────────────────┐
│                     Frontend (HTML/CSS/JS)                  │
│              文档上传 / 问答界面 / 文档列表                    │
└──────────────────────┬─────────────────────────────────────┘
                       │ HTTP REST API
┌──────────────────────┴─────────────────────────────────────┐
│                   Web Layer (Controller)                    │
│  DocumentController: 上传/列表/删除                          │
│  RAGController: 问答接口                                    │
└──────────────────────┬─────────────────────────────────────┘
                       │
┌──────────────────────┴─────────────────────────────────────┐
│                Application Layer (Service)                  │
│  DocumentService: 解析→分块→向量化→存储                      │
│  RAGService: 检索→增强→生成                                 │
└──────────────────────┬─────────────────────────────────────┘
                       │
┌──────────────────────┴─────────────────────────────────────┐
│              Infrastructure Layer                           │
│  ES VectorStore: 文档存储 + kNN 向量检索                     │
│  EmbeddingClient: 文本向量化                                 │
│  ChatClient: LLM 对话                                       │
│  DocumentParser: PDF/Word/TXT 解析                          │
└────────────────────────────────────────────────────────────┘
```

## 3. 核心流程

### 3.1 文档入库流程
```
上传文件 → 识别类型 → 解析文本 → 文本分块(Chunking)
→ 每块生成 Embedding → 存入 ES (含向量 + 原文 + 元数据)
```

### 3.2 问答流程
```
用户提问 → 问题向量化 → ES kNN 检索 Top-K 相关片段
→ 构建 Prompt(上下文 + 问题) → LLM 生成 → 返回答案 + 引用来源
```

## 4. 技术选型

| 组件 | 选择 | 说明 |
|------|------|------|
| 框架 | Spring Boot 3.3.x + Spring AI 1.0.x | 需从 2.7.15 升级 |
| AI SDK | Spring AI Alibaba | 提供 ChatClient、Embedding、VectorStore 统一抽象 |
| LLM | OpenAI 兼容协议 | 可配任何 OpenAI 兼容服务 |
| Embedding | OpenAI text-embedding-3-small (1536维) | 可配置切换 |
| 向量存储 | Elasticsearch 8.x | 利用 dense_vector + kNN |
| 文档解析 | Apache Tika | 支持 PDF/Word/TXT/HTML 等 |
| 前端 | 纯 HTML/CSS/JS | 无需前端构建工具，简单直接 |

## 5. ES 索引设计

```json
{
  "index": "rag_knowledge_base",
  "mappings": {
    "properties": {
      "document_id":  { "type": "keyword" },
      "document_name": { "type": "keyword" },
      "chunk_index":   { "type": "integer" },
      "content":       { "type": "text", "analyzer": "standard" },
      "embedding":     { "type": "dense_vector", "dims": 1536, "index": true, "similarity": "cosine" },
      "metadata":      { "type": "object", "enabled": false },
      "created_at":    { "type": "date" }
    }
  }
}
```

## 6. API 设计

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/v1/documents/upload | 上传文档 (multipart/form-data, field: file) |
| GET | /api/v1/documents | 文档列表 |
| DELETE | /api/v1/documents/{id} | 删除文档及所有分块 |
| POST | /api/v1/rag/query | RAG 问答 { "question": "...", "topK": 4 } |

### POST /api/v1/rag/query 响应格式
```json
{
  "code": 0,
  "message": "success",
  "data": {
    "question": "什么是RAG？",
    "answer": "RAG是检索增强生成...",
    "sources": [
      { "document_name": "ai_intro.pdf", "chunk_index": 3, "content": "..." }
    ]
  }
}
```

## 7. 模块分工（基于现有 DDD 结构）

| 模块 | 新增内容 |
|------|---------|
| **bootstrap** | 升级 Spring Boot 3.3，application.yml 配置 ES/LLM/Embedding |
| **domain** | 新增 `Document.java`、`DocumentChunk.java`、`RAGQuery.java`、`RAGResponse.java` |
| **application** | 新增 `DocumentService.java`、`RAGService.java` 接口及实现 |
| **infrastructure** | 新增 ES 配置、DocumentParser（Tika）、EmbeddingClient 配置 |
| **web** | 新增 `DocumentController.java`、`RAGController.java`、前端静态页面 |
| **facade** | 暂不修改 |

## 8. 前端页面设计

单个 HTML 页面 `rag.html`，包含三个区域：

1. **顶部导航** — 标题「知识库 RAG 检索系统」
2. **左侧：文档管理** — 拖拽上传区域 + 已上传文档列表（可删除）
3. **右侧：问答区域** — 对话式界面，输入问题 → 显示回答 + 引用来源

UI 风格：简洁现代，类似 ChatGPT 界面，使用内联 CSS。

## 9. 关键依赖

```xml
<!-- Spring Boot 升级到 3.3.x -->
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.3.5</version>
</parent>

<!-- Spring AI Alibaba BOM -->
<dependency>
    <groupId>com.alibaba.cloud.ai</groupId>
    <artifactId>spring-ai-alibaba-bom</artifactId>
    <version>1.0.0-M6</version>
    <type>pom</type>
    <scope>import</scope>
</dependency>

<!-- 关键依赖 -->
<dependency>
    <groupId>com.alibaba.cloud.ai</groupId>
    <artifactId>spring-ai-alibaba-starter</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-elasticsearch-store-spring-boot-starter</artifactId>
</dependency>
<dependency>
    <groupId>org.apache.tika</groupId>
    <artifactId>tika-core</artifactId>
    <version>2.9.0</version>
</dependency>
<dependency>
    <groupId>org.apache.tika</groupId>
    <artifactId>tika-parsers-standard-package</artifactId>
    <version>2.9.0</version>
</dependency>
```

## 10. 配置项 (application.yml)

```yaml
spring:
  elasticsearch:
    uris: ${ES_URIS:http://localhost:9200}

  ai:
    openai:
      api-key: ${OPENAI_API_KEY:sk-xxx}
      base-url: ${OPENAI_BASE_URL:https://api.openai.com}
      chat:
        model: ${CHAT_MODEL:gpt-4o-mini}
      embedding:
        model: ${EMBEDDING_MODEL:text-embedding-3-small}

    vectorstore:
      elasticsearch:
        index-name: rag_knowledge_base
        dimensions: 1536
```

## 11. 确认结果

1. **ES 连接地址** — 待提供，先用环境变量 `${ES_URIS}` 占位
2. **LLM API** — 待提供，先用环境变量 `${OPENAI_API_KEY}` / `${OPENAI_BASE_URL}` 占位
3. **文件大小限制** — 10MB ✅
4. **前端登录** — 不需要 ✅

---

> 请审阅以上设计，有任何调整意见请提出。确认后我将进入实现计划阶段。
