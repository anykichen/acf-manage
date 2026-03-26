#!/bin/bash

# 查看服务状态

SERVER="tpk@10.201.2.40"

echo -e "${BLUE}ACF服务状态${NC}"
echo "========================================"
echo ""

ssh $SERVER << 'ENDSSH'
    cd /home/tpk/acf
    
    echo "容器状态:"
    docker-compose ps
    
    echo ""
    echo "资源使用:"
    docker stats --no-stream --format "table {{.Name}}\t{{.CPUPerc}}\t{{.MemUsage}}\t{{.NetIO}}"
ENDSSH
