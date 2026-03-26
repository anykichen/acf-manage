#!/bin/bash

# 从GitHub部署到服务器

set -e

SERVER="tpk@10.201.2.40"
REPO="https://github.com/anykichen/acf-manage.git"
DEPLOY_DIR="/home/tpk/acf"

echo "==================================="
echo "ACF管控系统 - 从GitHub部署"
echo "==================================="

# 在服务器上拉取代码并部署
ssh $SERVER << 'ENDSSH'
set -e

echo ""
echo "步骤 1/4: 拉取代码..."
cd /home/tpk
if [ -d "acf" ]; then
    echo "更新已有代码..."
    cd acf
    git pull
else
    echo "克隆代码..."
    git clone https://github.com/anykichen/acf-manage.git acf
    cd acf
fi

echo "✓ 代码已更新"

echo ""
echo "步骤 2/4: 启动服务..."
docker-compose up -d --build

echo "✓ 服务启动中..."

echo ""
echo "步骤 3/4: 等待服务启动..."
sleep 10

echo ""
echo "步骤 4/4: 检查服务状态..."
docker-compose ps
echo ""
docker-compose logs --tail=20

ENDSSH

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
