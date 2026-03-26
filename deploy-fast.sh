#!/bin/bash

# 快速部署脚本 - 直接在服务器上构建,不使用多阶段Docker构建

set -e

echo "==================================="
echo "ACF管控系统 - 快速部署"
echo "==================================="

# 服务器信息
SERVER="tpk@10.201.2.40"
REMOTE_DIR="/home/tpk/acf"

# 1. 打包项目
echo ""
echo "步骤 1/5: 打包项目文件..."
cd /Users/chenqing/Desktop/acf
tar -czf acf-fast.tar.gz \
    acf-backend/pom.xml \
    acf-backend/src \
    acf-frontend/package.json \
    acf-frontend/tsconfig.json \
    acf-frontend/vite.config.ts \
    acf-frontend/src \
    acf-frontend/Dockerfile \
    acf-backend/Dockerfile \
    sql/*.sql \
    docker-compose.yml \
    --exclude='*.class' \
    --exclude='target' \
    --exclude='node_modules' \
    --exclude='dist'

echo "✓ 项目文件打包完成 (acf-fast.tar.gz)"

# 2. 上传到服务器
echo ""
echo "步骤 2/5: 上传文件到服务器..."
scp acf-fast.tar.gz $SERVER:/tmp/
ssh $SERVER "rm -rf $REMOTE_DIR && mkdir -p $REMOTE_DIR && cd /home/tpk && tar -xzf /tmp/acf-fast.tar.gz -C $REMOTE_DIR"
echo "✓ 文件上传完成"

# 3. 启动服务
echo ""
echo "步骤 3/5: 启动Docker服务..."
ssh $SERVER "cd $REMOTE_DIR && docker-compose up -d --build"
echo "✓ 服务启动中..."

# 4. 等待服务就绪
echo ""
echo "步骤 4/5: 等待服务启动..."
sleep 10

# 5. 验证部署
echo ""
echo "步骤 5/5: 验证部署..."
ssh $SERVER "cd $REMOTE_DIR && docker-compose ps"
ssh $SERVER "docker-compose logs --tail=20"

# 访问信息
echo ""
echo "==================================="
echo "✓ 部署完成!"
echo "==================================="
echo ""
echo "访问地址:"
echo "  前端: http://10.201.2.40:8082"
echo "  后端: http://10.201.2.40:28080"
echo "  API文档: http://10.201.2.40:28080/swagger-ui.html"
echo ""
echo "默认账号: admin / admin123"
echo "⚠️  请立即修改默认密码!"
echo ""
echo "常用命令:"
echo "  查看日志: ssh $SERVER 'cd $REMOTE_DIR && docker-compose logs -f'"
echo "  重启服务: ssh $SERVER 'cd $REMOTE_DIR && docker-compose restart'"
echo "  停止服务: ssh $SERVER 'cd $REMOTE_DIR && docker-compose down'"
echo ""
