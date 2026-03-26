#!/bin/bash

# 检查部署状态

SERVER="tpk@10.201.2.40"

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}  ACF部署状态检查${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# 1. 检查本地部署进程
echo -e "${YELLOW}[1/5] 检查本地部署进程...${NC}"
if pgrep -f "deploy.sh" > /dev/null; then
    echo -e "${GREEN}✓ 部署进程正在运行${NC}"
    echo -e "  PID: $(pgrep -f 'deploy.sh')"
else
    echo -e "${YELLOW}⚠ 部署进程未运行${NC}"
fi
echo ""

# 2. 检查部署日志
echo -e "${YELLOW}[2/5] 检查部署日志...${NC}"
if [ -f "/tmp/acf-deploy.log" ]; then
    LOG_SIZE=$(du -h /tmp/acf-deploy.log | awk '{print $1}')
    LAST_LINE=$(tail -1 /tmp/acf-deploy.log)
    echo -e "${GREEN}✓ 日志文件存在 (大小: $LOG_SIZE)${NC}"
    echo -e "  最后一行: $LAST_LINE"
else
    echo -e "${RED}✗ 日志文件不存在${NC}"
fi
echo ""

# 3. 检查服务器连接
echo -e "${YELLOW}[3/5] 检查服务器连接...${NC}"
if ssh -o ConnectTimeout=5 $SERVER "echo 'OK'" > /dev/null 2>&1; then
    echo -e "${GREEN}✓ 服务器连接正常${NC}"
else
    echo -e "${RED}✗ 服务器连接失败${NC}"
    exit 1
fi
echo ""

# 4. 检查服务器容器状态
echo -e "${YELLOW}[4/5] 检查服务器容器状态...${NC}"
ssh $SERVER "cd /home/tpk/acf 2>/dev/null && docker-compose ps 2>/dev/null || echo '容器未启动'" 2>/dev/null
echo ""

# 5. 检查服务健康
echo -e "${YELLOW}[5/5] 检查服务健康状态...${NC}"

# 检查后端
if curl -s -o /dev/null -w "%{http_code}" http://10.201.2.40:18080/actuator/health | grep -q "200"; then
    echo -e "${GREEN}✓ 后端服务健康${NC} (http://10.201.2.40:18080)"
else
    echo -e "${YELLOW}⚠ 后端服务未就绪或不可访问${NC}"
fi

# 检查前端
if curl -s -o /dev/null -w "%{http_code}" http://10.201.2.40:8081 | grep -q "200"; then
    echo -e "${GREEN}✓ 前端服务健康${NC} (http://10.201.2.40:8081)"
else
    echo -e "${YELLOW}⚠ 前端服务未就绪或不可访问${NC}"
fi

echo ""
echo -e "${BLUE}========================================${NC}"
echo ""

# 提示
echo -e "${YELLOW}常用命令:${NC}"
echo -e "  查看实时日志: ${GREEN}./watch-deploy.sh${NC}"
echo -e "  查看部署日志: ${GREEN}tail -f /tmp/acf-deploy.log${NC}"
echo -e "  检查部署状态: ${GREEN}./check-deploy.sh${NC}"
echo -e "  重启部署: ${GREEN}nohup ./deploy.sh > /tmp/acf-deploy.log 2>&1 &${NC}"
echo ""
