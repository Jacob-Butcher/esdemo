# RAG 知识库检索系统 — 实现计划

## 概述

按 6 个阶段、约 20 个任务逐步实现。每个任务 2-5 分钟，含精确文件路径和验证步骤。

---

## Phase 1: 升级 Spring Boot & 依赖配置

### Task 1.1: 升级根 POM — Spring Boot 3.3 + Spring AI
**文件**: `pom.xml`
**操作**:
- 升级 `spring-boot-starter-parent` 2.7.15 → 3.3.5
- 添加 Spring AI BOM (1.0.0-M6) 到 dependencyManagement
- 添加 `spring-ai-openai-spring-boot-starter`
- 添加 `spring-ai-elasticsearch-store-spring-boot-starter`
- 升级 `fastjson2` 到最新版
- 移除 `jakarta.json` / `glassfish`（Spring Boot 3 自带）
- 验证: `mvn dependency:tree` 无冲突

### Task 1.2: 升级 bootstrap POM
**文件**: `app/bootstrap/pom.xml`
**操作**: 确保依赖关系正确，无额外变更
**验证**: `mvn compile` 通过

### Task 1.3: 更新 application.yml
**文件**: `app/bootstrap/src/main/resources/application.yml`
**操作**: 添加 ES、Spring AI OpenAI、文件上传配置
**验证**: 启动无配置错误

### Task 1.4: 修复 javax → jakarta 迁移问题
**文件**: 所有 `.java` 文件
**操作**: 全局搜索 `javax.` 替换为 `jakarta.`（如有）
**验证**: `mvn compile` 全部通过

---

## Phase 2: 领域模型 (domain)

### Task 2.1: 创建 Document 模型
**文件**: `app/domain/src/main/java/com/example/esDemo/model/Document.java`
**内容**:
```java
@Data @Builder
public class Document {
    private String id;          // ES _id
    private String name;        // 原始文件名
    private String contentType; // application/pdf 等
    private Long size;          // 字节
    private LocalDateTime createdAt;
}
```

### Task 2.2: 创建 DocumentChunk 模型
**文件**: `app/domain/src/main/java/com/example/esDemo/model/DocumentChunk.java`
**内容**:
```java
@Data @Builder
public class DocumentChunk {
    private String id;
    private String documentId;
    private String documentName;
    private int chunkIndex;
    private String content;
    private List<Double> embedding;
    private LocalDateTime createdAt;
}
```

### Task 2.3: 创建 RAGQuery / RAGResponse 模型
**文件**: `app/domain/src/main/java/com/example/esDemo/model/RAGQuery.java`
**文件**: `app/domain/src/main/java/com/example/esDemo/model/RAGResponse.java`
**内容**: 请求 {question, topK} / 响应 {question, answer, sources}

---

## Phase 3: 应用服务 (application)

### Task 3.1: 添加 application POM 依赖
**文件**: `app/application/pom.xml`
**操作**: 添加 infrastructure 模块依赖

### Task 3.2: 创建 DocumentService 接口 + 实现
**文件**: `app/application/src/main/java/com/example/esDemo/service/DocumentService.java`
**文件**: `app/application/src/main/java/com/example/esDemo/service/impl/DocumentServiceImpl.java`
**逻辑**:
- upload(MultipartFile): 解析→分块→向量化→存储
- listAll(): 查询所有文档
- deleteById(String id): 删除文档及分块

### Task 3.3: 创建 RAGService 接口 + 实现
**文件**: `app/application/src/main/java/com/example/esDemo/service/RAGService.java`
**文件**: `app/application/src/main/java/com/example/esDemo/service/impl/RAGServiceImpl.java`
**逻辑**:
- query(RAGQuery): 向量化问题→ES检索→构建prompt→LLM生成→返回

---

## Phase 4: 基础设施 (infrastructure)

### Task 4.1: 更新 infrastructure POM
**文件**: `app/infrastructure/pom.xml`
**操作**: 添加 Tika parser 依赖（tika-core + tika-parsers-standard-package）

### Task 4.2: 创建 ES 配置
**文件**: `app/infrastructure/src/main/java/com/example/esDemo/config/ElasticsearchConfig.java`
**操作**: 
- 创建索引模板（如索引不存在自动创建）
- 配置 ElasticsearchClient bean
- 提供 VectorStore bean（Spring AI 的 ElasticsearchVectorStore）

### Task 4.3: 创建文档解析器
**文件**: `app/infrastructure/src/main/java/com/example/esDemo/parser/DocumentParser.java`
**操作**:
- 使用 Apache Tika 解析 PDF/Word/TXT
- 文本分块策略：按段落 + 最大 1000 字符，重叠 200 字符
- 返回 `List<String>` 分块列表

---

## Phase 5: Web 层 (web)

### Task 5.1: 更新 web POM
**文件**: `app/web/pom.xml`
**操作**: 确保依赖正确

### Task 5.2: 创建 DocumentController
**文件**: `app/web/src/main/java/com/example/esDemo/controller/DocumentController.java`
**API**:
- `POST /api/v1/documents/upload` — 上传( max 10MB)
- `GET /api/v1/documents` — 列表
- `DELETE /api/v1/documents/{id}` — 删除

### Task 5.3: 创建 RAGController
**文件**: `app/web/src/main/java/com/example/esDemo/controller/RAGController.java`
**API**:
- `POST /api/v1/rag/query` — RAG 问答

### Task 5.4: 创建前端页面
**文件**: `app/web/src/main/resources/static/rag.html`
**设计**: 
- 左侧文档管理面板（拖拽上传 + 列表）
- 右侧问答聊天面板
- 使用 fetch API 调用后端

---

## Phase 6: 集成验证

### Task 6.1: 编译 & 启动验证
**命令**: `mvn clean compile` → `mvn spring-boot:run`
**验证**: 应用无错误启动，检查启动日志

### Task 6.2: API 手动测试
**验证**:
```bash
# 上传文档
curl -F "file=@test.pdf" http://localhost:8080/api/v1/documents/upload
# 文档列表
curl http://localhost:8080/api/v1/documents
# RAG 问答
curl -X POST http://localhost:8080/api/v1/rag/query \
  -H "Content-Type: application/json" \
  -d '{"question":"这篇文档讲了什么？"}'
```

---

> 确认后我将按这个计划逐步执行实现。
