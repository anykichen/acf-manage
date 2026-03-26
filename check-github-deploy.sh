#!/bin/bash

# 检查GitHub部署状态

SERVER="tpk@10.201.2.40"

echo "==================================="
echo "检查部署状态"
echo "==================================="

echo ""
echo "1. 检查Docker镜像构建..."
ssh $SERVER "docker images | grep acf"

echo ""
echo "2. 检查容器状态..."
ssh $SERVER "docker-compose -f /home/tpk/acf/docker-compose.yml ps"

echo ""
echo "3. 查看最近日志..."
ssh $SERVER "docker-compose -f /home/tpk/acf/docker-compose.yml logs --tail=20"

echo ""
echo "==================================="
echo "访问地址:"
echo "  前端: http://10.201.2.40:8082"
echo "  后端: http://10.201.2.40:28080"
echo "==================================="
