#!/bin/bash

# 监控部署进度

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}  ACF部署进度监控${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# 检查日志文件
if [ ! -f "/tmp/acf-deploy.log" ]; then
    echo -e "${RED}✗ 未找到部署日志文件${NC}"
    echo -e "${YELLOW}请先执行: nohup ./deploy.sh > /tmp/acf-deploy.log 2>&1 &${NC}"
    exit 1
fi

echo -e "${YELLOW}查看部署日志...${NC}"
echo ""

# 显示最后50行日志
tail -50 /tmp/acf-deploy.log

echo ""
echo -e "${YELLOW}========================================${NC}"
echo -e "${YELLOW}  按Ctrl+C退出监控${NC}"
echo -e "${YELLOW}========================================${NC}"
echo ""

# 持续监控
tail -f /tmp/acf-deploy.log
