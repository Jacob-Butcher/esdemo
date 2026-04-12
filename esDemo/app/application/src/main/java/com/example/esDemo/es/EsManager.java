package com.example.esDemo.es;

import com.example.esDemo.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EsManager {

    @Autowired
    private EsService esService;

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
    public List<Product> searchByKeyword()  {
        return esService.searchByKeyword();
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
