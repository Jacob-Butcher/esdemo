package com.example.esDemo.controller;

import com.example.esDemo.model.AppResult;
import com.example.esDemo.model.Document;
import com.example.esDemo.service.DocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class DocumentController {

    private static final Logger log = LoggerFactory.getLogger(DocumentController.class);

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * 上传文档（支持 PDF / Word / TXT 等），最大 10MB
     */
    @PostMapping("/documents/upload")
    public AppResult<Document> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return AppResult.isFail("文件不能为空");
        }

        // 仅日志记录，大小限制由 spring.servlet.multipart 控制
        log.info("Receiving upload: {} ({} bytes, {})",
                file.getOriginalFilename(), file.getSize(), file.getContentType());

        try {
            Document doc = documentService.upload(file);
            return AppResult.isSuccess(doc);
        } catch (IllegalArgumentException e) {
            log.warn("Upload rejected: {}", e.getMessage());
            return AppResult.isFail(e.getMessage());
        } catch (Exception e) {
            log.error("Upload failed: {} -> {}", e.getClass().getName(), e.getMessage(), e);
            return AppResult.isFail("上传失败: [" + e.getClass().getSimpleName() + "] " + e.getMessage());
        }
    }

    /**
     * 列出所有已上传文档
     */
    @GetMapping("/documents")
    public AppResult<List<Document>> list() {
        return AppResult.isSuccess(documentService.listAll());
    }

    /**
     * 删除文档及其所有向量分块
     */
    @DeleteMapping("/documents/{id}")
    public AppResult<Void> delete(@PathVariable String id) {
        try {
            documentService.deleteById(id);
            return AppResult.isSuccess(null);
        } catch (Exception e) {
            log.error("Delete failed for document: {}", id, e);
            return AppResult.isFail("删除失败: " + e.getMessage());
        }
    }
}
