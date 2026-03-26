#!/bin/bash

# 前端启动脚本 - 用于预览

set -e

echo "==================================="
echo "ACF管控系统 - 前端启动"
echo "==================================="

cd /Users/chenqing/Desktop/acf/acf-frontend

# 检查依赖
echo ""
echo "检查前端依赖..."
if [ ! -d "node_modules" ]; then
    echo "安装前端依赖(这可能需要几分钟)..."
    npm install
else
    echo "✓ 前端依赖已安装"
fi

# 创建日志目录
mkdir -p logs

# 启动前端
echo ""
echo "启动前端开发服务器..."
npm run dev

# 或者后台运行:
# nohup npm run dev > logs/frontend.log 2>&1 &
# echo "前端已在后台启动,日志: tail -f logs/frontend.log"
