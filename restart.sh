#!/bin/bash

# 重启服务

SERVER="tpk@10.201.2.40"

echo -e "${YELLOW}重启ACF服务...${NC}"

ssh $SERVER << 'ENDSSH'
    cd /home/tpk/acf
    docker-compose restart
    echo "服务已重启"
    docker-compose ps
ENDSSH

echo -e "${GREEN}✓ 服务重启成功${NC}"
