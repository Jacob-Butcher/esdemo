package com.example.esDemo.parser;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 文档解析器：使用 Apache Tika 解析多种格式文档并分块。
 * 支持 PDF、Word (doc/docx)、TXT、HTML、Markdown 等。
 */
@Component
public class DocumentParser {

    private static final int MAX_CHUNK_SIZE = 1000;
    private static final int CHUNK_OVERLAP = 200;
    private static final int MIN_CHUNK_SIZE = 50;

    private final Tika tika = new Tika();

    /**
     * 解析上传文件为纯文本
     */
    public String parseToString(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream()) {
            return tika.parseToString(is);
        } catch (TikaException e) {
            throw new IOException("Failed to parse document: " + file.getOriginalFilename(), e);
        }
    }

    /**
     * 将长文本按段落 + 长度限制分块，块之间有重叠。
     */
    public List<String> splitIntoChunks(String text) {
        List<String> chunks = new ArrayList<>();
        if (text == null || text.isBlank()) {
            return chunks;
        }

        // 按段落初步分割
        String[] paragraphs = text.split("\n{2,}");

        StringBuilder current = new StringBuilder();
        for (String para : paragraphs) {
            String trimmed = para.replaceAll("\\s+", " ").trim();
            if (trimmed.length() < MIN_CHUNK_SIZE / 2) {
                continue;
            }

            if (current.length() + trimmed.length() > MAX_CHUNK_SIZE && current.length() >= MIN_CHUNK_SIZE) {
                chunks.add(current.toString().trim());

                // 重叠：取上一块的尾部作为新块的开头
                String overlap = current.length() > CHUNK_OVERLAP
                        ? current.substring(current.length() - CHUNK_OVERLAP)
                        : current.toString();
                current = new StringBuilder(overlap);
            }

            if (current.length() > 0) {
                current.append("\n\n");
            }
            current.append(trimmed);
        }

        // 处理剩余内容
        if (current.length() >= MIN_CHUNK_SIZE) {
            chunks.add(current.toString().trim());
        }

        return chunks;
    }

    /**
     * 一步完成：解析 + 分块
     */
    public List<String> parseAndChunk(MultipartFile file) throws IOException {
        String text = parseToString(file);
        return splitIntoChunks(text);
    }
}
