package com.example.esDemo.utils;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.rpc.service.GenericService;

/**
 * 泛化调用的工具类
 */
public class InvokeUtil {

    public static void main(String[] args) {
        // 配置应用信息
        ApplicationConfig application = new ApplicationConfig();
        application.setName("generic-consumer");
        // 配置注册中心信息
        RegistryConfig registry = new RegistryConfig();
        registry.setAddress("zookeeper://127.0.0.1:2181");
        // 配置泛化调用
        ReferenceConfig<GenericService> reference = new ReferenceConfig<>();
        reference.setApplication(application);
        reference.setRegistry(registry);
        reference.setInterface("com.example.SomeService"); // 接口名
        reference.setGeneric(true);
        // 获取 GenericService 对象
        GenericService someService = reference.get();
        // 使用泛化调用进行远程调用
        Object result = someService.$invoke("methodName", new String[] {"java.lang.String"}, new Object[] {"parameter"});
        System.out.println("Result: " + result);
    }
}
