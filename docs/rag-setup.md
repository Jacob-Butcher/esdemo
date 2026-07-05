# RAG 知识库检索系统 — 搭建文档

## 架构

```
浏览器 (rag.html)
    │
    ▼
Spring Boot 3.3.5 (port 8888)
    ├── DocumentController   POST /api/v1/documents/upload|list|delete
    ├── RAGController        POST /api/v1/rag/query
    │
    ├── Chat:     SiliconFlow → deepseek-ai/DeepSeek-V3
    ├── Embedding: SiliconFlow → BAAI/bge-m3 (1024维)
    │
    └── Vector Store: ES 8.15 (via TCP proxy)
            127.0.0.1:19200 ─► 192.168.5.9:9200
```

## 模块结构

```
app/
├── bootstrap/    启动入口 + application.yml
├── domain/       模型: Document, DocumentChunk, RAGQuery, RAGResponse
├── application/  服务: DocumentService, RAGService 及其实现
├── infrastructure/  ES 配置 + Tika 文档解析器
├── web/          Controller + rag.html 前端
└── facade/       (未使用)
```

## 启动步骤

### 1. ES 代理 (macOS Java 兼容性)

macOS 上 Java 进程无法直连 `192.168.5.9:9200` (`NoRouteToHost`)，需 Python TCP 代理：

```bash
python3 -c "
import socket, threading
server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
server.bind(('127.0.0.1', 19200))
server.listen(5)
def forward(src, dst):
    try:
        while True:
            data = src.recv(8192)
            if not data: break
            dst.sendall(data)
    except: pass
    finally:
        try: src.close()
        except: pass
        try: dst.close()
        except: pass
while True:
    client, _ = server.accept()
    remote = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    remote.connect(('192.168.5.9', 9200))
    threading.Thread(target=forward, args=(client, remote), daemon=True).start()
    threading.Thread(target=forward, args=(remote, client), daemon=True).start()
" &
```

### 2. 构建启动

```bash
# 安装依赖模块
mvn clean install -DskipTests -pl app/domain,app/infrastructure,app/application,app/web

# 打包并启动
mvn clean package -DskipTests -pl app/bootstrap
java -Djava.net.preferIPv4Stack=true -jar app/bootstrap/target/bootstrap-0.0.1-SNAPSHOT.jar
```

### 3. 访问

打开 `http://localhost:8888/rag.html`

## 配置 (application.yml)

```yaml
server.port: 8888
spring.ai.openai:
  api-key: sk-yrl...ccuilr           # SiliconFlow API Key
  base-url: https://api.siliconflow.cn  # 不带 /v1，避免 URL 重复拼接
  chat.options.model: deepseek-ai/DeepSeek-V3
  embedding.options.model: BAAI/bge-m3

spring.elasticsearch.uris: http://127.0.0.1:19200  # 经代理连 ES

spring.ai.vectorstore.elasticsearch:
  index-name: rag_knowledge_base
  dimensions: 1024
  initialize-schema: true

spring.servlet.multipart.max-file-size: 10MB
```

## API

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/v1/documents/upload` | 上传文件 (multipart `file`) |
| GET | `/api/v1/documents` | 文档列表 |
| DELETE | `/api/v1/documents/{id}` | 删除文档 |
| POST | `/api/v1/rag/query` | RAG 问答 `{"question":"...","topK":4,"similarityThreshold":0.3}` |

## ES 索引

| 索引 | 用途 |
|------|------|
| `rag_documents` | 文档元数据 (name, size, chunks...) |
| `rag_knowledge_base` | 向量分块 (content + 1024维 embedding) |

## 踩坑记录

1. **Spring AI M6 + ES 客户端版本不匹配** — `KnnSearch.Builder.k()` 参数类型不一致，需排除 `elasticsearch-java:8.13.4` 升级到 `8.15.3`
2. **Jackson + LocalDateTime** — ES 客户端序列化需要 `jackson-datatype-jsr310`，或改用 `String` 存时间
3. **SiliconFlow base-url** — 必须用 `https://api.siliconflow.cn` (不带 `/v1`)，Spring AI 会自动拼接 `/v1/embeddings`
4. **相似度阈值** — BGE-M3 对短查询的余弦相似度偏低，默认阈值从 0.7 降到 0.3
5. **macOS Java NIO `NoRouteToHost`** — Java NIO socket 无法连接同网段 IP，需 TCP 代理绕过
6. **首版本编译失败后 `-rf` 缺 jar** — 失败模块之前 clean 过的 jar 丢失，需完整 `mvn install` 重装
