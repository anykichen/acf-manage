#!/bin/bash

# 手动部署指南 - 无需SSH密钥的情况下使用

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}  ACF手动部署指南${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

echo -e "${YELLOW}部署前准备:${NC}"
echo "1. 确保服务器已安装Docker和Docker Compose"
echo "2. 确保服务器至少有4GB内存和50GB磁盘空间"
echo ""

echo -e "${YELLOW}步骤1: 在本地打包项目${NC}"
echo "----------------------------------------"
cd /Users/chenqing/Desktop/acf
tar -czf acf-deploy.tar.gz \
    --exclude='node_modules' \
    --exclude='target' \
    --exclude='dist' \
    --exclude='.git' \
    acf-backend/ \
    acf-frontend/ \
    sql/ \
    docker-compose.yml \
    acf/

echo -e "${GREEN}✓ 打包完成: acf-deploy.tar.gz${NC}"
echo ""

echo -e "${YELLOW}步骤2: 上传到服务器${NC}"
echo "----------------------------------------"
echo "请手动执行以下命令(需要输入密码):"
echo ""
echo "  scp acf-deploy.tar.gz tpk@10.201.2.40:/tmp/"
echo ""
echo "或者使用文件传输工具上传到: tpk@10.201.2.40:/tmp/"
echo ""

read -p "上传完成后按回车继续..."

echo ""
echo -e "${YELLOW}步骤3: SSH登录服务器${NC}"
echo "----------------------------------------"
echo "请执行以下命令登录服务器:"
echo ""
echo "  ssh tpk@10.201.2.40"
echo ""

read -p "登录后按回车继续..."

echo ""
echo -e "${YELLOW}步骤4: 在服务器上执行以下命令${NC}"
echo "----------------------------------------"
echo ""
echo "# 创建部署目录"
echo "mkdir -p /home/tpk/acf"
echo "cd /home/tpk/acf"
echo ""
echo "# 解压文件"
echo "tar -xzf /tmp/acf-deploy.tar.gz"
echo ""
echo "# 设置权限"
echo "chmod +x acf-backend/Dockerfile"
echo "chmod +x acf-frontend/Dockerfile"
echo "chmod +x docker-compose.yml"
echo ""
echo "# 构建镜像"
echo "docker-compose build"
echo ""
echo "# 启动容器"
echo "docker-compose up -d"
echo ""
echo "# 查看状态"
echo "docker-compose ps"
echo ""
echo "# 查看日志"
echo "docker-compose logs -f --tail=50"
echo ""

echo -e "${YELLOW}步骤5: 验证部署${NC}"
echo "----------------------------------------"
echo ""
echo "# 检查容器状态"
echo "docker-compose ps"
echo ""
echo "# 检查后端健康"
echo "curl http://localhost:8080/actuator/health"
echo ""
echo "# 检查前端"
echo "curl http://localhost:80"
echo ""

echo -e "${BLUE}========================================${NC}"
echo -e "${GREEN}部署完成!${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""
echo -e "${YELLOW}访问地址:${NC}"
echo "  前端: http://10.201.2.40:80"
echo "  后端: http://10.201.2.40:8080"
echo "  API文档: http://10.201.2.40:8080/swagger-ui.html"
echo ""
echo -e "${YELLOW}默认账号:${NC}"
echo "  系统管理员: admin / admin123"
echo ""
