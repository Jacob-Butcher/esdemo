#!/bin/bash

# 配置参数
CONTAINER_NAME="my-java-app"  # 容器名称
IMAGE_NAME="my-java-image"    # 镜像名称
TAG="latest"                  # 镜像标签
PORT=8888                     # 暴露端口
JAR_PATH="target/*.jar"       # JAR文件路径

# 1. 清理现有容器
echo "[1/4] 清理旧容器: $CONTAINER_NAME..."
docker stop $CONTAINER_NAME >/dev/null 2>&1 || true
docker rm $CONTAINER_NAME >/dev/null 2>&1 || true

# 2. 构建JAR包
echo "[2/4] 构建Java应用..."
mvn clean package -DskipTests

# 3. 构建Docker镜像
echo "[3/4] 构建Docker镜像..."
docker build --build-arg JAR_FILE=$JAR_PATH -t $IMAGE_NAME:$TAG .

# 4. 启动新容器
echo "[4/4] 启动新容器..."
docker run -d \
  --name $CONTAINER_NAME \
  -network elk-net \
  -p $PORT:8888 \
  -e JAVA_OPTS="-Xmx512m -Xms256m" \
  $IMAGE_NAME:$TAG

echo "✅ 部署完成！容器正在运行，访问地址: http://localhost:$PORT"