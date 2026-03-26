#!/bin/bash

echo "检查ACF项目部署状态"
echo "===================="

echo ""
echo "1. 检查服务器连接:"
ssh tpk@10.201.2.40 "echo '✓ 服务器连接正常'"

echo ""
echo "2. 检查Docker服务:"
ssh tpk@10.201.2.40 "docker --version && docker-compose --version"

echo ""
echo "3. 检查ACF目录:"
ssh tpk@10.201.2.40 "ls -la /home/tpk/acf | head -20"

echo ""
echo "4. 检查Docker进程:"
ssh tpk@10.201.2.40 "docker ps -a | grep acf"

echo ""
echo "5. 检查Docker镜像:"
ssh tpk@10.201.2.40 "docker images | grep -E 'acf|mysql|redis'"

echo ""
echo "===================="
echo "如果看到没有ACF容器,说明Docker构建可能还在进行中或失败了"
echo "可以手动登录服务器运行: cd /home/tpk/acf && docker-compose up --build"
echo "===================="
