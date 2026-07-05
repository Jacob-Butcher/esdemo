package com.example.esDemo.service;

import com.example.esDemo.model.Document;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 文档管理服务：上传、列表、删除
 */
public interface DocumentService {

    /**
     * 上传文档：解析 → 分块 → 向量化 → 存储到 ES
     */
    Document upload(MultipartFile file) throws IOException;

    /**
     * 列出所有已上传文档
     */
    List<Document> listAll();

    /**
     * 删除文档及其所有分块
     */
    void deleteById(String documentId);
}
