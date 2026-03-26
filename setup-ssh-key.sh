#!/bin/bash

# SSH免密配置脚本

SERVER="tpk@10.201.2.40"
PASSWORD="Cc88081/"
SSH_KEY="$HOME/.ssh/id_ed25519.pub"

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}  SSH免密登录配置${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# 1. 检查SSH密钥
echo -e "${YELLOW}[1/3] 检查SSH密钥...${NC}"
if [ ! -f "$SSH_KEY" ]; then
    echo -e "${RED}✗ 未找到SSH公钥${NC}"
    echo -e "${YELLOW}正在生成SSH密钥...${NC}"
    ssh-keygen -t ed25519 -f ~/.ssh/id_ed25519 -N ""
    echo -e "${GREEN}✓ SSH密钥已生成${NC}"
else
    echo -e "${GREEN}✓ SSH密钥已存在${NC}"
    echo -e "  密钥文件: $SSH_KEY"
fi
echo ""

# 2. 读取公钥内容
echo -e "${YELLOW}[2/3] 读取公钥内容...${NC}"
PUB_KEY=$(cat $SSH_KEY)
echo -e "${GREEN}公钥内容:${NC}"
echo "  $PUB_KEY"
echo ""

# 3. 复制公钥到服务器
echo -e "${YELLOW}[3/3] 复制公钥到服务器...${NC}"
echo -e "${YELLOW}服务器: $SERVER${NC}"
echo ""

# 使用expect自动输入密码
cat > /tmp/ssh_copy.exp << 'EOF'
#!/usr/bin/expect -f
set timeout 60
set SERVER [lindex $argv 0]
set PASSWORD [lindex $argv 1]
set PUB_KEY [lindex $argv 2]

spawn ssh -o StrictHostKeyChecking=no $SERVER "mkdir -p ~/.ssh && echo $PUB_KEY >> ~/.ssh/authorized_keys && chmod 700 ~/.ssh && chmod 600 ~/.ssh/authorized_keys && echo 'SSH免密配置成功!'"

expect {
    "password:" {
        send "$PASSWORD\r"
        expect {
            "SSH免密配置成功!" {
                exit 0
            }
            timeout {
                exit 1
            }
        }
    }
    "already exists" {
        expect eof
        exit 0
    }
    timeout {
        exit 1
    }
    eof {
        wait
    }
}
EOF

chmod +x /tmp/ssh_copy.exp
expect /tmp/ssh_copy.exp "$SERVER" "$PASSWORD" "$PUB_KEY"

if [ $? -eq 0 ]; then
    echo ""
    echo -e "${GREEN}✓ SSH免密配置成功!${NC}"
else
    echo ""
    echo -e "${RED}✗ SSH免密配置失败${NC}"
    echo -e "${YELLOW}请手动执行以下步骤:${NC}"
    echo ""
    echo "1. 复制公钥内容:"
    echo "   $PUB_KEY"
    echo ""
    echo "2. 手动登录服务器:"
    echo "   ssh $SERVER"
    echo ""
    echo "3. 在服务器上执行:"
    echo "   mkdir -p ~/.ssh"
    echo "   echo '$PUB_KEY' >> ~/.ssh/authorized_keys"
    echo "   chmod 700 ~/.ssh"
    echo "   chmod 600 ~/.ssh/authorized_keys"
    echo ""
    exit 1
fi

# 4. 测试免密登录
echo ""
echo -e "${YELLOW}测试免密登录...${NC}"
if ssh -o StrictHostKeyChecking=no -o BatchMode=yes $SERVER "echo '免密登录测试成功!'" > /dev/null 2>&1; then
    echo -e "${GREEN}✓ 免密登录测试成功!${NC}"
else
    echo -e "${RED}✗ 免密登录测试失败${NC}"
    echo -e "${YELLOW}请检查服务器上的 ~/.ssh/authorized_keys 文件权限${NC}"
    exit 1
fi

echo ""
echo -e "${BLUE}========================================${NC}"
echo -e "${GREEN}  SSH免密登录配置完成!${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""
echo -e "${YELLOW}现在你可以直接执行以下命令:${NC}"
echo -e "  ssh $SERVER"
echo -e "  scp local_file $SERVER:/path/"
echo ""
