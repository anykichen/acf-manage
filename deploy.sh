#!/bin/bash

# ACF管控系统 - 服务器部署脚本
# 目标服务器: tpk@10.201.2.40

set -e  # 遇到错误立即退出

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 配置
SERVER="tpk@10.201.2.40"
REMOTE_DIR="/home/tpk/acf"
DOCKER_COMPOSE_FILE="docker-compose.yml"

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}  ACF管控系统 - 服务器部署脚本${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# 1. 检查本地环境
echo -e "${YELLOW}[1/7] 检查本地环境...${NC}"

# 检查docker-compose是否存在
if [ ! -f "$DOCKER_COMPOSE_FILE" ]; then
    echo -e "${RED}✗ 未找到 docker-compose.yml 文件${NC}"
    exit 1
fi

# 检查后端Dockerfile
if [ ! -f "acf-backend/Dockerfile" ]; then
    echo -e "${RED}✗ 未找到后端 Dockerfile${NC}"
    exit 1
fi

# 检查前端Dockerfile
if [ ! -f "acf-frontend/Dockerfile" ]; then
    echo -e "${RED}✗ 未找到前端 Dockerfile${NC}"
    exit 1
fi

# 检查SQL文件
if [ ! -f "sql/init_database.sql" ]; then
    echo -e "${RED}✗ 未找到数据库初始化脚本${NC}"
    exit 1
fi

# 检查nginx配置
if [ ! -f "acf-frontend/nginx.conf" ]; then
    echo -e "${RED}✗ 未找到 nginx.conf 配置文件${NC}"
    exit 1
fi

echo -e "${GREEN}✓ 本地环境检查通过${NC}"
echo ""

# 2. 测试服务器连接
echo -e "${YELLOW}[2/7] 测试服务器连接...${NC}"
if ssh -o ConnectTimeout=10 $SERVER "echo 'Connection OK'" > /dev/null 2>&1; then
    echo -e "${GREEN}✓ 服务器连接成功${NC}"
else
    echo -e "${RED}✗ 无法连接到服务器 ${SERVER}${NC}"
    exit 1
fi
echo ""

# 3. 检查服务器Docker环境
echo -e "${YELLOW}[3/7] 检查服务器Docker环境...${NC}"
ssh $SERVER << 'ENDSSH'
    # 检查Docker
    if ! command -v docker &> /dev/null; then
        echo -e "\033[0;31m✗ Docker未安装\033[0m"
        exit 1
    fi
    
    # 检查docker-compose
    if ! command -v docker-compose &> /dev/null; then
        echo -e "\033[0;31m✗ Docker Compose未安装\033[0m"
        exit 1
    fi
    
    echo -e "\033[0;32m✓ Docker环境检查通过\033[0m"
    docker --version
    docker-compose --version
ENDSSH

if [ $? -ne 0 ]; then
    exit 1
fi
echo ""

# 4. 清理服务器旧容器
echo -e "${YELLOW}[4/7] 清理服务器旧容器...${NC}"
ssh $SERVER << 'ENDSSH'
    cd /home/tpk/acf 2>/dev/null || exit 0
    
    # 停止并删除旧容器
    if [ -f "docker-compose.yml" ]; then
        echo "停止旧容器..."
        docker-compose down -v 2>/dev/null || true
    fi
    
    echo -e "\033[0;32m✓ 旧容器已清理\033[0m"
ENDSSH
echo ""

# 5. 上传文件到服务器
echo -e "${YELLOW}[5/7] 上传文件到服务器...${NC}"

# 创建临时打包目录
echo "  - 准备上传文件..."
TEMP_DIR=$(mktemp -d)
cp -r acf-backend $TEMP_DIR/
cp -r acf-frontend $TEMP_DIR/
cp -r sql $TEMP_DIR/
cp docker-compose.yml $TEMP_DIR/
cp -r acf $TEMP_DIR/ 2>/dev/null || echo "  - 跳过acf目录"

# 打包
echo "  - 打包文件..."
cd $TEMP_DIR
tar -czf acf-deploy.tar.gz .
cd -

echo "  - 上传文件到服务器..."
scp -C acf-deploy.tar.gz $SERVER:/tmp/acf-deploy.tar.gz

echo "  - 在服务器上解压文件..."
ssh $SERVER << 'ENDSSH'
    # 创建部署目录
    mkdir -p /home/tpk/acf
    cd /home/tpk/acf
    
    # 解压文件
    echo "解压文件..."
    tar -xzf /tmp/acf-deploy.tar.gz
    rm -f /tmp/acf-deploy.tar.gz
    
    # 设置权限
    chmod +x acf-backend/Dockerfile 2>/dev/null || true
    chmod +x acf-frontend/Dockerfile 2>/dev/null || true
    chmod +x docker-compose.yml
    
    echo -e "\033[0;32m✓ 文件上传成功\033[0m"
ENDSSH

# 清理临时文件
rm -rf $TEMP_DIR

echo ""

# 6. 在服务器上构建和启动容器
echo -e "${YELLOW}[6/7] 在服务器上构建和启动容器...${NC}"
ssh $SERVER << 'ENDSSH'
    cd /home/tpk/acf
    
    echo "  - 构建镜像..."
    docker-compose build --no-cache
    
    echo "  - 启动容器..."
    docker-compose up -d
    
    echo ""
    echo -e "\033[0;32m✓ 容器启动成功\033[0m"
ENDSSH

if [ $? -ne 0 ]; then
    echo -e "${RED}✗ 容器启动失败${NC}"
    exit 1
fi
echo ""

# 7. 等待服务启动并验证
echo -e "${YELLOW}[7/7] 验证服务状态...${NC}"
sleep 15  # 等待服务启动

ssh $SERVER << 'ENDSSH'
    cd /home/tpk/acf
    
    echo "  - 容器状态:"
    docker-compose ps
    
    echo ""
    echo "  - 检查服务日志:"
    docker-compose logs --tail=20
ENDSSH

# 检查服务健康状态
echo ""
echo -e "${YELLOW}检查服务健康状态...${NC}"

# 检查后端服务
if curl -f -s http://10.201.2.40:18080/actuator/health > /dev/null 2>&1; then
    echo -e "${GREEN}✓ 后端服务健康 (http://10.201.2.40:18080)${NC}"
else
    echo -e "${YELLOW}⚠ 后端服务可能还在启动中 (http://10.201.2.40:18080)${NC}"
fi

# 检查前端服务
if curl -f -s http://10.201.2.40:8081 > /dev/null 2>&1; then
    echo -e "${GREEN}✓ 前端服务健康 (http://10.201.2.40:8081)${NC}"
else
    echo -e "${YELLOW}⚠ 前端服务可能还在启动中 (http://10.201.2.40:8081)${NC}"
fi

# 检查MySQL
if ssh $SERVER "docker exec acf-mysql mysqladmin ping -h localhost -u root -proot123" > /dev/null 2>&1; then
    echo -e "${GREEN}✓ MySQL服务健康${NC}"
else
    echo -e "${RED}✗ MySQL服务异常${NC}"
fi

# 检查Redis
if ssh $SERVER "docker exec acf-redis redis-cli -a redis123 ping" > /dev/null 2>&1; then
    echo -e "${GREEN}✓ Redis服务健康${NC}"
else
    echo -e "${RED}✗ Redis服务异常${NC}"
fi

echo ""
echo -e "${BLUE}========================================${NC}"
echo -e "${GREEN}  部署完成！${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""
echo -e "${YELLOW}访问地址:${NC}"
echo -e "  前端: ${GREEN}http://10.201.2.40:8081${NC}"
echo -e "  后端: ${GREEN}http://10.201.2.40:18080${NC}"
echo -e "  API文档: ${GREEN}http://10.201.2.40:18080/swagger-ui.html${NC}"
echo ""
echo -e "${YELLOW}常用命令:${NC}"
echo -e "  查看日志: ssh $SERVER 'cd /home/tpk/acf && docker-compose logs -f'"
echo -e "  重启服务: ssh $SERVER 'cd /home/tpk/acf && docker-compose restart'"
echo -e "  停止服务: ssh $SERVER 'cd /home/tpk/acf && docker-compose down'"
echo -e "  查看状态: ssh $SERVER 'cd /home/tpk/acf && docker-compose ps'"
echo ""
echo -e "${YELLOW}默认账号:${NC}"
echo -e "  数据库: root / root123"
echo -e "  应用数据库: acf_user / acf123"
echo -e "  Redis密码: redis123"
echo -e "  系统管理员: admin / admin123"
echo ""
