# 使用 OpenJDK 官方镜像
FROM openjdk:8-jdk-alpine

# 设置工作目录
WORKDIR /app

# 复制 JAR 文件到镜像中
COPY target/esDemo-0.0.1-SNAPSHOT.jar esDemo2.jar

# 暴露应用端口（与 Java 应用配置的端口一致）
EXPOSE 8888

# 启动命令
CMD ["java", "-jar", "esDemo2.jar"]