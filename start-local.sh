#!/bin/bash

# 本地启动脚本 - 用于预览项目

set -e

echo "==================================="
echo "ACF管控系统 - 本地启动"
echo "==================================="

# 检查是否已安装依赖
echo ""
echo "检查前端依赖..."
cd /Users/chenqing/Desktop/acf/acf-frontend
if [ ! -d "node_modules" ]; then
    echo "安装前端依赖..."
    npm install
else
    echo "✓ 前端依赖已安装"
fi

# 检查后端配置
echo ""
echo "检查后端配置..."
cd /Users/chenqing/Desktop/acf/acf-backend
if [ ! -f "target/acf-backend-1.0.0.jar" ]; then
    echo "构建后端项目..."
    mvn clean package -DskipTests
else
    echo "✓ 后端JAR文件已存在"
fi

# 创建日志目录
mkdir -p logs

# 启动后端
echo ""
echo "启动后端服务..."
nohup java -jar target/acf-backend-1.0.0.jar > ../logs/backend.log 2>&1 &
BACKEND_PID=$!
echo "后端PID: $BACKEND_PID"
echo "后端日志: ../logs/backend.log"

# 等待后端启动
echo ""
echo "等待后端启动..."
sleep 5

# 检查后端是否启动成功
if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
    echo "✓ 后端启动成功 (http://localhost:8080)"
else
    echo "⚠ 后端可能还在启动中,请检查日志: tail -f logs/backend.log"
fi

# 启动前端
echo ""
echo "启动前端服务..."
cd /Users/chenqing/Desktop/acf/acf-frontend
nohup npm run dev > ../logs/frontend.log 2>&1 &
FRONTEND_PID=$!
echo "前端PID: $FRONTEND_PID"
echo "前端日志: ../logs/frontend.log"

# 等待前端启动
echo ""
echo "等待前端启动..."
sleep 5

echo ""
echo "==================================="
echo "✓ 服务启动完成!"
echo "==================================="
echo ""
echo "访问地址:"
echo "  前端: http://localhost:5173"
echo "  后端: http://localhost:8080"
echo "  API文档: http://localhost:8080/swagger-ui.html"
echo ""
echo "日志文件:"
echo "  后端: tail -f logs/backend.log"
echo "  前端: tail -f logs/frontend.log"
echo ""
echo "停止服务:"
echo "  kill $BACKEND_PID  # 停止后端"
echo "  kill $FRONTEND_PID  # 停止前端"
echo ""
