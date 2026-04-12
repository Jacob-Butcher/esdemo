package com.example.esDemo.utils;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;

public class EsClientUtil {

    private static final RestHighLevelClient client;

    static {
        // 配置 Elasticsearch 地址（支持多个节点）
        RestClientBuilder builder = RestClient.builder(
                new HttpHost("elasticsearch", 9200, "http")
        );
        client = new RestHighLevelClient(builder);
    }

    public static RestHighLevelClient getClient() {
        return client;
    }

    // 关闭客户端（需在程序结束时调用）
    public static void close() throws IOException {
        client.close();
    }
}
