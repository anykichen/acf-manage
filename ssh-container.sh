#!/bin/bash

# 在服务器上进入容器执行命令

SERVER="tpk@10.201.2.40"
CONTAINER=${1:-"backend"}

if [ "$CONTAINER" = "backend" ]; then
    CONTAINER_NAME="acf-backend"
elif [ "$CONTAINER" = "mysql" ]; then
    CONTAINER_NAME="acf-mysql"
elif [ "$CONTAINER" = "redis" ]; then
    CONTAINER_NAME="acf-redis"
else
    echo "用法: $0 [backend|mysql|redis]"
    exit 1
fi

echo -e "${YELLOW}进入容器: $CONTAINER_NAME${NC}"
ssh -t $SERVER "docker exec -it $CONTAINER_NAME /bin/bash || docker exec -it $CONTAINER_NAME /bin/sh"
