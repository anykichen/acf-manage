#!/bin/bash

# 停止服务

SERVER="tpk@10.201.2.40"

echo -e "${YELLOW}停止ACF服务...${NC}"

ssh $SERVER << 'ENDSSH'
    cd /home/tpk/acf
    docker-compose down
    echo "服务已停止"
ENDSSH

echo -e "${GREEN}✓ 服务已停止${NC}"
