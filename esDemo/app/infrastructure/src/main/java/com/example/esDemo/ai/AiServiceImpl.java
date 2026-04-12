package com.example.esDemo.ai;

import com.example.esDemo.constants.CommonConstant;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AiServiceImpl implements AiService{

    @Autowired
    private OpenAiChatModel openAiChatModel;

    @Override
    public String chat(String message) {
        try {
            SystemMessage systemMessage = SystemMessage.from(CommonConstant.AI_INTENT_RECOGNITION);
            UserMessage userMessage = UserMessage.from(message);

            // 调用模型的generate方法，发送系统消息和用户消息，并获取回复
            return getMsg(aiRequest(systemMessage, userMessage), userMessage);

        }catch (Exception e){
            log.error("ai request error:{}", e.getMessage());
        }
        return null;
    }

    private String getMsg(String recognition, UserMessage userMessage){
        if(recognition == null){
            return null;
        }
        String msg = "";
        switch (recognition){
            case "FUNCTION_1": {
                msg = "本地查询";
                break;
            }
            case "FUNCTION_2": {
                msg = "文档查询";
                break;
            }
            case "OTHER": {
                SystemMessage systemMessage = SystemMessage.from(CommonConstant.AI_INTENT_JUESE);
                msg = aiRequest(systemMessage, userMessage);
                break;
            }
            default:{
                break;
            }
        }
        return msg;
    }

    private String aiRequest(SystemMessage systemMessage, UserMessage userMessage){
        try {
            //SystemMessage systemMessage = SystemMessage.from(CommonConstant.AI_INTENT_RECOGNITION);
            // 创建用户消息，这里用占位符表示实际要查询的内容
            //String userMessageTxt = " Tell me about {{place}}.\n" +
            //        "                Write the answer briefly in form of a list.";
            // 替换占位符，生成具体的用户消息
            //UserMessage userMessage = UserMessage.from(userMessageTxt.replace("{{place}}", message));
            //UserMessage userMessage = UserMessage.from(message);
            //String answer = chatLanguageModel.generate("你是谁");

            // 调用模型的generate方法，发送系统消息和用户消息，并获取回复
            Response<AiMessage> response = openAiChatModel.generate(systemMessage, userMessage);
            log.info("result: usage:{} finishReason:{}", response.tokenUsage(), response.finishReason());
            AiMessage aiMessage = response.content();
            // 打印模型的回复内容
            log.info("result: message:{}", aiMessage);
            return aiMessage.text();
        }catch (Exception e){
            log.error("ai request error:{}", e.getMessage());
        }
        return null;
    }

    public void testEasyRag() {
        String dir = System.getProperty("user.home") + "/Downloads/rag";
        List<Document> documents = FileSystemDocumentLoader.loadDocuments(dir);
        log.info("finish load document");
        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        EmbeddingStoreIngestor.ingest(documents, embeddingStore);
        log.info("finish inject to embedding store");
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(openAiChatModel)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .contentRetriever(EmbeddingStoreContentRetriever.from(embeddingStore))
                .build();
        String answer = assistant.chat("How to do Easy RAG with LangChain4j?");
        log.info("answer:{}", answer);
    }

    public static void main(String[] args) {
        OpenAiChatModel chatLanguageModel = OpenAiChatModel.builder()
                .baseUrl("http://langchain4j.dev/demo/openai/v1")
                .apiKey("demo")
                .modelName("gpt-4o-mini")
                .build();
        String answer = chatLanguageModel.generate("你是谁");
        System.out.println(answer); // Hello World


//        QianfanChatModel chatLanguageModel = QianfanChatModel.builder()
//                .apiKey(API_KEY)
//                .secretKey(SECRET_KEY)
//                .modelName("Yi-34B-Chat")
//                .build();
        // 目录中的所有文件，txt 似乎更快
//        List<Document> documents = FileSystemDocumentLoader.loadDocuments("/home/langchain4j/documentation");
//        // 为简单起见，我们将使用内存存储：
//        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
//        EmbeddingStoreIngestor.ingest(documents, embeddingStore);
//
//        IAiService assistant = AiServices.builder(IAiService.class)
//                .chatLanguageModel(chatLanguageModel)
//                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
//                .contentRetriever(EmbeddingStoreContentRetriever.from(embeddingStore))
//                .build();
//
//        String answer = assistant.chat("问题");
//        System.out.println(answer);

    }
}
