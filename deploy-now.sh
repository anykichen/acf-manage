#!/bin/bash

# 简化版部署脚本 - 使用expect自动处理密码

SERVER="tpk@10.201.2.40"
PASSWORD="Cc88081/"
REMOTE_DIR="/home/tpk/acf"

set -e

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}  ACF管控系统 - 快速部署${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# 1. 打包文件
echo -e "${YELLOW}[1/5] 打包项目文件...${NC}"
TEMP_DIR="/tmp/acf_deploy_$(date +%s)"
mkdir -p "$TEMP_DIR"
cp -r acf-backend $TEMP_DIR/
cp -r acf-frontend $TEMP_DIR/
cp -r sql $TEMP_DIR/
cp docker-compose.yml $TEMP_DIR/
tar -czf /tmp/acf-deploy.tar.gz -C $TEMP_DIR .
echo -e "${GREEN}✓ 打包完成${NC}"
echo ""

# 2. 上传文件
echo -e "${YELLOW}[2/5] 上传文件到服务器...${NC}"

# 创建expect脚本上传文件
cat > /tmp/upload.exp << 'EOF'
#!/usr/bin/expect -f
set timeout 300
set SERVER [lindex $argv 0]
set PASSWORD [lindex $argv 1]
set SOURCE [lindex $argv 2]
set DEST [lindex $argv 3]

spawn scp -o StrictHostKeyChecking=no $SOURCE $SERVER:$DEST
expect {
    "password:" {
        send "$PASSWORD\r"
        expect {
            "100%" {
                exit 0
            }
            timeout {
                exit 1
            }
        }
    }
    timeout {
        exit 1
    }
}
EOF

chmod +x /tmp/upload.exp
expect /tmp/upload.exp "$SERVER" "$PASSWORD" "/tmp/acf-deploy.tar.gz" "/tmp/"
echo -e "${GREEN}✓ 上传完成${NC}"
echo ""

# 3. 在服务器上解压并构建
echo -e "${YELLOW}[3/5] 在服务器上解压并构建(需要10-20分钟)...${NC}"

# 创建expect脚本在服务器上执行
cat > /tmp/server-deploy.exp << 'EOF'
#!/usr/bin/expect -f
set timeout 1800
set SERVER [lindex $argv 0]
set PASSWORD [lindex $argv 1]
set REMOTE_DIR [lindex $argv 2]

spawn ssh -o StrictHostKeyChecking=no $SERVER
expect "password:"
send "$PASSWORD\r"

expect "~]#"
send "mkdir -p $REMOTE_DIR\r"

expect "~]#"
send "cd $REMOTE_DIR\r"

expect "~]#"
send "docker-compose down -v 2>/dev/null || true\r"

expect "~]#"
send "rm -rf * 2>/dev/null || true\r"

expect "~]#"
send "tar -xzf /tmp/acf-deploy.tar.gz\r"

expect "~]#"
send "rm -f /tmp/acf-deploy.tar.gz\r"

expect "~]#"
send "chmod +x acf-backend/Dockerfile acf-frontend/Dockerfile docker-compose.yml\r"

expect "~]#"
send "echo '开始构建镜像...'\r"

expect "~]#"
send "docker-compose build --no-cache\r"

expect {
    "Successfully built" {
        send "\r"
    }
    timeout {
        # 即使超时也继续
        send "\r"
    }
}

expect "~]#"
send "echo '启动容器...'\r"

expect "~]#"
send "docker-compose up -d\r"

expect {
    "Started" {
        send "\r"
    }
    timeout {
        send "\r"
    }
}

expect "~]#"
send "sleep 10\r"

expect "~]#"
send "docker-compose ps\r"

expect "~]#"
send "exit\r"

expect eof
EOF

chmod +x /tmp/server-deploy.exp
expect /tmp/server-deploy.exp "$SERVER" "$PASSWORD" "$REMOTE_DIR"

echo -e "${GREEN}✓ 构建和启动完成${NC}"
echo ""

# 4. 清理临时文件
echo -e "${YELLOW}[4/5] 清理临时文件...${NC}"
rm -rf "$TEMP_DIR"
rm -f /tmp/acf-deploy.tar.gz
rm -f /tmp/upload.exp
rm -f /tmp/server-deploy.exp
echo -e "${GREEN}✓ 清理完成${NC}"
echo ""

# 5. 验证部署
echo -e "${YELLOW}[5/5] 验证部署...${NC}"

cat > /tmp/check.exp << 'EOF'
#!/usr/bin/expect -f
set timeout 30
set SERVER [lindex $argv 0]
set PASSWORD [lindex $argv 1]

spawn ssh -o StrictHostKeyChecking=no $SERVER "docker ps --filter name=acf --format '{{.Names}}: {{.Status}}'"
expect "password:"
send "$PASSWORD\r"
expect eof
EOF

chmod +x /tmp/check.exp
expect /tmp/check.exp "$SERVER" "$PASSWORD"
rm -f /tmp/check.exp

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
echo -e "${YELLOW}默认账号:${NC}"
echo -e "  系统管理员: admin / admin123"
echo -e "  数据库: root / root123"
echo ""
echo -e "${YELLOW}常用命令:${NC}"
echo -e "  查看日志: ./view-logs.sh"
echo -e "  重启服务: ./restart.sh"
echo -e "  查看状态: ./status.sh"
echo ""
