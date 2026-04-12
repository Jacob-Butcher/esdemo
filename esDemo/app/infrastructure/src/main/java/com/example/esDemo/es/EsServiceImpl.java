package com.example.esDemo.es;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.example.esDemo.es.EsService;
import com.example.esDemo.model.Product;
import com.example.esDemo.utils.EsClientUtil;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EsServiceImpl implements EsService {

    /**
     * 创建索引
     *
     * @throws Exception 2
     */
//    public static void createIndex() throws Exception {
//        ElasticsearchClient client = EsClientUtil.getClient();
//        ElasticsearchIndicesClient indices = client.indices();
//
//        // 定义索引映射
//        String mappingJson = "{\n" +
//                "                  \"mappings\": {\n" +
//                "                    \"properties\": {\n" +
//                "                      \"title\": { \"type\": \"text\", \"analyzer\": \"ik_max_word\" },\n" +
//                "                      \"price\": { \"type\": \"double\" },\n" +
//                "                      \"createTime\": { \"type\": \"date\", \"format\": \"yyyy-MM-dd HH:mm:ss\" }\n" +
//                "                    }\n" +
//                "                  }\n" +
//                "                }";
//
//        CreateIndexRequest request = CreateIndexRequest.of(builder ->
//                builder.index("products").withJson(new StringReader(mappingJson))
//        );
//
//        indices.create(request);
//        client._transport().close();
//    }

    /**
     * 插入数据
     * @throws Exception
     */
//    public static void indexDocument() throws Exception {
//        ElasticsearchClient client = EsClientUtil.getClient();
//
//        Product product = new Product("手机", 2999.99, new Date());
//
//        IndexRequest<Product> request = IndexRequest.of(builder ->
//                builder.index("products")
//                        .id("1")  // 指定ID则为更新，不指定自动生成
//                        .document(product)
//        );
//
//        IndexResponse response = client.index(request);
//        System.out.println("文档ID: " + response.id());
//
//        client._transport().close();
//    }

    /**
     * 查询
     * @throws Exception
     */
    public List<Product> searchByKeyword() {
        List<Product> list = new ArrayList<>();
        try {
            RestHighLevelClient client = EsClientUtil.getClient();

            // 构建搜索请求
            SearchRequest searchRequest = new SearchRequest("products"); // 指定索引名
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

            // 设置查询条件（此处为匹配所有文档）
            sourceBuilder.query(QueryBuilders.matchAllQuery());
            searchRequest.source(sourceBuilder);

            // 执行查询
            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);

            // 处理结果
            System.out.println("总命中数: " + response.getHits().getTotalHits().value);

            response.getHits().forEach(hit -> {
                Product product = JSONObject.parseObject(hit.getSourceAsString(), new TypeReference<Product>() {
                });

                System.out.println("文档内容: " + hit.getSourceAsString());
                list.add(product);
            });
        }catch (Exception e){

        }
        return list;
        //EsClientUtil.close();
    }

    public void insert() {
        try {
            // 初始化 ES 客户端
            RestHighLevelClient esClient = EsClientUtil.getClient();

            // 创建索引映射（包含文本和向量字段）
            CreateIndexRequest request = new CreateIndexRequest("knowledge_base");
            request.mapping(
                    "{\n" +
                            "  \"properties\": {\n" +
                            "    \"text\": {\"type\": \"text\", \"analyzer\": \"ik_max_word\"},\n" +
                            "    \"vector\": {\"type\": \"dense_vector\", \"dims\": 384}\n" +
                            "  }\n" +
                            "}",
                    XContentType.JSON
            );
            esClient.indices().create(request, RequestOptions.DEFAULT);
            // 加载文档（支持 txt/pdf/docx）
//            Document document = DocumentLoaders.fromFile("data/faq.txt").load();
//
//            // 中文文本分割（按段落）
//            TextSplitter splitter = new ParagraphTextSplitter(500, 50);
//            List<TextSegment> segments = splitter.split(document.text());
//
//            // 使用本地嵌入模型（需下载 all-MiniLM-L6-v2 模型）
//            EmbeddingModel embeddingModel = HuggingFaceEmbeddingModel.builder()
//                    .modelName("/models/all-MiniLM-L6-v2")
//                    .poolingStrategy(PoolingStrategy.MEAN)
//                    .build();
//
//            // 批量处理文档块
//            BulkRequest bulkRequest = new BulkRequest();
//            for (TextSegment segment : segments) {
//                // 生成向量
//                Embedding embedding = embeddingModel.embed(segment.text());
//
//                // 构建 ES 文档
//                XContentBuilder builder = XContentFactory.jsonBuilder()
//                        .startObject()
//                        .field("text", segment.text())
//                        .field("vector", embedding.vectorAsList())
//                        .endObject();
//
//                bulkRequest.add(new IndexRequest("knowledge_base").source(builder));
//            }
//
//            // 批量写入 ES
//            BulkResponse response = esClient.bulk(bulkRequest, RequestOptions.DEFAULT);
//            if (response.hasFailures()) {
//                System.err.println("批量插入失败: " + response.buildFailureMessage());
//            }
        } catch (Exception e) {

        }
    }
//    /**
//     * 批量插入
//     * @throws Exception
//     */
//    public static void bulkInsert() throws Exception {
//        ElasticsearchClient client = EsClientUtil.getClient();
//
//        List<Product> products = Arrays.asList(
//                new Product("平板", 1999.99, new Date()),
//                new Product("耳机", 399.99, new Date())
//        );
//
//        BulkRequest.Builder bulkBuilder = new BulkRequest.Builder();
//        for (Product product : products) {
//            bulkBuilder.operations(op -> op
//                    .index(idx -> idx
//                            .index("products")
//                            .document(product)
//                    )
//            );
//        }
//
//        BulkResponse response = client.bulk(bulkBuilder.build());
//        System.out.println("批量操作耗时: " + response.took());
//
//        client._transport().close();
//    }
//
//    /**
//     * 删除
//     * @throws Exception
//     */
//    public static void deleteDocument() throws Exception {
//        ElasticsearchClient client = EsClientUtil.getClient();
//
//        DeleteRequest request = DeleteRequest.of(builder ->
//                builder.index("products").id("1")
//        );
//
//        DeleteResponse response = client.delete(request);
//        System.out.println("删除结果: " + response.result());
//
//        client._transport().close();
//    }
}
