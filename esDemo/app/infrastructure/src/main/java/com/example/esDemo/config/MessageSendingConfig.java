package com.example.esDemo.config;
//
//import dev.langchain4j.data.message.AiMessage;
//import dev.langchain4j.data.message.SystemMessage;
//import dev.langchain4j.data.message.UserMessage;
//import dev.langchain4j.model.chat.ChatLanguageModel;
//import dev.langchain4j.model.openai.OpenAiChatModel;
//import dev.langchain4j.model.output.Response;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.context.annotation.Bean;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//
//// 配置类，用于定义Spring容器中的Bean
@Configuration
public class MessageSendingConfig {

    @Bean
    OpenAiChatModel openAiChatModel(){
        OpenAiChatModel chatLanguageModel = OpenAiChatModel.builder()
                .baseUrl("http://langchain4j.dev/demo/openai/v1")
                .apiKey("demo")
                .modelName("gpt-4o-mini")
                .build();
        return chatLanguageModel;
    }
}
//    // 自动注入ChatLanguageModel实例
////    @Autowired
////    ChatLanguageModel chatLanguageModel;
//
//    // 定义一个名为messageSendingRunner的Bean，实现CommandLineRunner接口
//    @Bean(name = "messageSendingRunner")
//    CommandLineRunner messageSendingRunner() {
//        return args -> {
//            // 创建系统消息，告诉模型它的角色和回复要求
//            SystemMessage systemMessage = SystemMessage.from(" You are a helpful AI assistant that helps people find information.\n" +
//                    "                Your name is Alexa\n" +
//                    "                Start with telling your name and quick summary of answer you are going to provide in a sentence.\n" +
//                    "                Next, you should reply to the user's request. \n" +
//                    "                Finish with thanking the user for asking question in the end.");
//            // 创建用户消息，这里用占位符表示实际要查询的内容
//            String userMessageTxt = " Tell me about {{place}}.\n" +
//                    "                Write the answer briefly in form of a list.";
//            // 替换占位符，生成具体的用户消息
//            UserMessage userMessage = UserMessage.from(userMessageTxt.replace("{{place}}", args[0]));
//
//            OpenAiChatModel chatLanguageModel = OpenAiChatModel.builder()
//                    .baseUrl("http://langchain4j.dev/demo/openai/v1")
//                    .apiKey("demo")
//                    .modelName("gpt-4o-mini")
//                    .build();
//            //String answer = chatLanguageModel.generate("你是谁");
//
//            // 调用模型的generate方法，发送系统消息和用户消息，并获取回复
//            Response<AiMessage> response = chatLanguageModel.generate(systemMessage, userMessage);
//            // 打印模型的回复内容
//            System.out.println(response.content());
//        };
//    }
//}
