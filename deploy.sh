#!/bin/bash

# 配置信息
REGISTRY="crpi-o08rhi2pxibxdenj.cn-beijing.personal.cr.aliyuncs.com"
NAMESPACE="jeecg-gfjdh"

echo "========================================================"
echo " Starting deployment on Server..."
echo "========================================================"

# 1. 拉取最新镜像
echo ""
echo "[Step 1] Pulling latest images from Aliyun..."
docker pull $REGISTRY/$NAMESPACE/jeecg-system:latest
docker pull $REGISTRY/$NAMESPACE/jeecg-vue:latest
docker pull $REGISTRY/$NAMESPACE/jeecg-mysql:latest

# 2. 启动/更新服务
echo ""
echo "[Step 2] Redeploying with Docker Compose..."
# -d 后台运行
# --remove-orphans 清理定义中不再存在的容器
docker compose -f docker-compose-prod.yml up -d --remove-orphans

echo ""
echo "========================================================"
echo " Deployment Successful!"
echo " check logs with: docker logs -f jeecg-boot-system"
echo "========================================================"
