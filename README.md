# esDemo — RAG 知识库检索系统

基于 **Elasticsearch + Spring AI + SiliconFlow** 的 RAG (Retrieval-Augmented Generation) 知识库问答系统。上传文档 → 自动向量化存储 → 提问时检索相关片段 → LLM 生成答案。

## 技术栈

| 组件 | 技术 |
|------|------|
| 框架 | Spring Boot 3.3.5 + Spring AI 1.0.0-M6 |
| LLM | SiliconFlow `deepseek-ai/DeepSeek-V3` |
| Embedding | SiliconFlow `BAAI/bge-m3` (1024维) |
| 向量存储 | Elasticsearch 8.x (dense_vector + kNN) |
| 文档解析 | Apache Tika (PDF/Word/TXT) |
| 前端 | 纯 HTML/CSS/JS |

## 快速启动

```bash
# 1. ES 代理 (macOS Java 兼容性，详见 docs/rag-setup.md)
python3 docs/es-proxy.py &

# 2. 构建 & 启动
mvn clean package -DskipTests -pl app/domain,app/infrastructure,app/application,app/web,app/bootstrap
java -Djava.net.preferIPv4Stack=true -jar app/bootstrap/target/bootstrap-0.0.1-SNAPSHOT.jar

# 3. 打开 http://localhost:8888/rag.html
```

## 项目结构

```
app/
├── bootstrap/      启动入口 + application.yml
├── domain/         领域模型 (Document, RAGQuery, RAGResponse)
├── application/    业务服务 (DocumentService, RAGService)
├── infrastructure/ ES 配置 + Tika 文档解析
├── web/            REST Controller + 前端页面
└── facade/         (预留)
```

## API

| 方法 | 路径 | 说明 |
|------|------|------|
| `POST` | `/api/v1/documents/upload` | 上传文档 (multipart, ≤10MB) |
| `GET` | `/api/v1/documents` | 文档列表 |
| `DELETE` | `/api/v1/documents/{id}` | 删除文档 |
| `POST` | `/api/v1/rag/query` | RAG 问答 `{"question":"...","topK":4}` |

## 配置

核心配置在 `app/bootstrap/src/main/resources/application.yml`，关键项：

```yaml
spring.ai.openai.api-key:       # SiliconFlow API Key
spring.elasticsearch.uris:      # ES 地址
spring.ai.vectorstore.elasticsearch.index-name: rag_knowledge_base
```

详细文档见 [docs/rag-setup.md](docs/rag-setup.md)。
