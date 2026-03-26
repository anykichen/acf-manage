#!/bin/bash

# 在服务器上查看日志

SERVER="tpk@10.201.2.40"

echo -e "${BLUE}ACF服务日志${NC}"
echo "========================================"
echo ""

ssh $SERVER << 'ENDSSH'
    cd /home/tpk/acf
    docker-compose logs -f --tail=100
ENDSSH
