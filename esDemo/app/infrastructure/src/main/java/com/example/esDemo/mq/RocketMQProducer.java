package com.example.esDemo.mq;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class RocketMQProducer {

    /***************************消息生产者***************************/
//    @Autowired
//    private MQTransactionListener mqTransactionListener;        //TODO 事务消息监听器
    //TODO 消息生产者配置信息
    @Value("${rocketmq.producer.namesrvAddr:127.0.0.1:9876}")
    private String pNamesrvAddr;                                //TODO 生产者nameservice地址
    @Value("${rocketmq.producer.maxMessageSize:4096}")
    private Integer maxMessageSize ;                            //TODO 消息最大大小，默认4M
    @Value("${rocketmq.producer.sendMsgTimeout:30000}")
    private Integer sendMsgTimeout;                             //TODO 消息发送超时时间，默认3秒
    @Value("${rocketmq.producer.retryTimesWhenSendFailed:2}")
    private Integer retryTimesWhenSendFailed;
    //TODO 消息发送失败重试次数，默认2次
    //private static ExecutorService executor = ThreadUtil.newExecutor(32);//TODO 执行任务的线程池

    @Value("${rocketmq.group:producer_group_demo}")
    private String groupName ;

    //普通消息生产者
    @Bean("default")
    public DefaultMQProducer getDefaultMQProducer() {
        DefaultMQProducer producer = new DefaultMQProducer(this.groupName);
        producer.setNamesrvAddr(this.pNamesrvAddr);
        producer.setMaxMessageSize(this.maxMessageSize);
        producer.setSendMsgTimeout(this.sendMsgTimeout);
        producer.setRetryTimesWhenSendFailed(this.retryTimesWhenSendFailed);
        try {
            producer.start();
        } catch (MQClientException e) {
            System.out.println(e.getErrorMessage());
        }
        return producer;
    }

    //事务消息生产者（rocketmq支持柔性事务）
//    @Bean("transaction")
//    public TransactionMQProducer getTransactionMQProducer() {
//        //初始化事务消息基本与普通消息生产者一致
//        TransactionMQProducer producer = new TransactionMQProducer("transaction_" + this.groupName);
//        producer.setNamesrvAddr(this.pNamesrvAddr);
//        producer.setMaxMessageSize(this.maxMessageSize);
//        producer.setSendMsgTimeout(this.sendMsgTimeout);
//        producer.setRetryTimesWhenSendFailed(this.retryTimesWhenSendFailed);
//
//        //添加事务消息处理线程池
//        producer.setExecutorService(executor);
//        //添加事务消息监听
//        producer.setTransactionListener(mqTransactionListener);
//        try {
//            producer.start();
//        } catch (MQClientException e) {
//            System.out.println(e.getErrorMessage());
//        }
//        return producer;
//    }

}
