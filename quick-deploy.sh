#!/bin/bash

# 快速部署脚本 - 仅上传并重启,不重新构建镜像

set -e

SERVER="tpk@10.201.2.40"
REMOTE_DIR="/home/tpk/acf"

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}  ACF快速部署 (不重新构建)${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# 上传文件
echo -e "${YELLOW}[1/3] 上传文件...${NC}"

TEMP_DIR=$(mktemp -d)
cp docker-compose.yml $TEMP_DIR/
cp -r acf $TEMP_DIR/ 2>/dev/null || true

cd $TEMP_DIR
tar -czf acf-quick.tar.gz .
cd -

scp -C acf-quick.tar.gz $SERVER:/tmp/acf-quick.tar.gz

ssh $SERVER << 'ENDSSH'
    cd /home/tpk/acf
    tar -xzf /tmp/acf-quick.tar.gz
    rm -f /tmp/acf-quick.tar.gz
ENDSSH

rm -rf $TEMP_DIR

echo -e "${GREEN}✓ 文件上传成功${NC}"

# 重启服务
echo -e "${YELLOW}[2/3] 重启服务...${NC}"
ssh $SERVER << 'ENDSSH'
    cd /home/tpk/acf
    docker-compose restart
ENDSSH

echo -e "${GREEN}✓ 服务重启成功${NC}"

# 验证
echo -e "${YELLOW}[3/3] 验证服务...${NC}"
sleep 5
ssh $SERVER << 'ENDSSH'
    cd /home/tpk/acf
    docker-compose ps
ENDSSH

echo ""
echo -e "${GREEN}快速部署完成！${NC}"
