package com.example.esDemo;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import java.util.List;

public class RocketMQConsumerTest {

    public static void main(String[] args) throws MQClientException {
        // 1. 创建消费者实例，指定消费者组名（需唯一）
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("consumer_group_demo");

        // 2. 指定 Name Server 地址（替换为你的实际地址）
        consumer.setNamesrvAddr("192.168.1.3:9876"); // 宿主机IP:9876

        // 3. 订阅 Topic 和 Tag（"*" 表示所有 Tag）
        consumer.subscribe("TopicTest", "*");

        // 4. 注册消息监听器
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(
                    List<MessageExt> messages,
                    ConsumeConcurrentlyContext context
            ) {
                for (MessageExt msg : messages) {
                    System.out.printf("收到消息: Topic=%s, Tag=%s, Body=%s%n",
                            msg.getTopic(),
                            msg.getTags(),
                            new String(msg.getBody())
                    );
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS; // 消费成功
            }
        });

        // 5. 启动消费者
        consumer.start();
        System.out.println("消费者已启动，等待消息...");
    }
}
