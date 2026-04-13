package com.example.esDemo;


import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

public class RocketMQProducerTest {

    public static void main(String[] args) throws MQClientException, InterruptedException {
        // 1. 创建生产者实例，指定生产者组名（需唯一）
        DefaultMQProducer producer = new DefaultMQProducer("producer_group_demo");

        // 2. 指定 Name Server 地址（替换为你的实际地址）
        producer.setNamesrvAddr("192.168.1.3:9876"); // 宿主机IP:9876

        // 3. 启动生产者
        producer.start();

        try {
            for (int i = 0; i < 10; i++) {
                // 4. 创建消息对象，指定 Topic、Tag 和消息内容
                Message msg = new Message(
                        "TopicTest",                  // Topic
                        "TagA",                       // Tag（消息分类）
                        ("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET) // 消息体
                );

                // 5. 发送消息并获取结果
                SendResult sendResult = producer.send(msg);
                System.out.printf("消息发送成功: %s%n", sendResult);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 6. 关闭生产者
            producer.shutdown();
        }
    }
}
