# SSH免密登录配置说明

## 你的SSH公钥内容

```
ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIBS77fweFGM8V5Kr00l4ktIHfcPbYLn6lpr6BAlrV9hq deployment@macbook
```

## 方法1: 手动配置 (推荐)

### 步骤1: 登录服务器
```bash
ssh tpk@10.201.2.40
```
输入密码: `Cc88081/`

### 步骤2: 在服务器上执行以下命令
```bash
# 创建.ssh目录(如果不存在)
mkdir -p ~/.ssh

# 设置正确的权限
chmod 700 ~/.ssh

# 添加公钥到authorized_keys
echo "ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIBS77fweFGM8V5Kr00l4ktIHfcPbYLn6lpr6BAlrV9hq deployment@macbook" >> ~/.ssh/authorized_keys

# 设置authorized_keys权限
chmod 600 ~/.ssh/authorized_keys

# 验证配置
cat ~/.ssh/authorized_keys
```

### 步骤3: 退出服务器
```bash
exit
```

### 步骤4: 测试免密登录
```bash
ssh tpk@10.201.2.40
```

如果成功,应该不需要输入密码直接登录。

---

## 方法2: 使用ssh-copy-id命令 (如果可用)

```bash
ssh-copy-id tpk@10.201.2.40
```

输入密码后,公钥会自动复制到服务器。

---

## 方法3: 一条命令完成

登录服务器后,执行这一条命令:

```bash
mkdir -p ~/.ssh && chmod 700 ~/.ssh && echo "ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIBS77fweFGM8V5Kr00l4ktIHfcPbYLn6lpr6BAlrV9hq deployment@macbook" >> ~/.ssh/authorized_keys && chmod 600 ~/.ssh/authorized_keys && echo "SSH免密配置成功!"
```

---

## 验证配置成功

配置完成后,在本地执行:

```bash
ssh tpk@10.201.2.40 "echo '免密登录成功!'"
```

如果显示"免密登录成功!",说明配置成功。

---

## 配置成功后,可以直接使用以下命令

### 快速部署
```bash
cd /Users/chenqing/Desktop/acf
./deploy.sh
```

### 查看日志
```bash
./view-logs.sh
```

### 重启服务
```bash
./restart.sh
```

### 查看状态
```bash
./status.sh
```

### 停止服务
```bash
./stop.sh
```

---

## 故障排查

### 问题1: 仍然需要密码

**解决方案:**
1. 检查.ssh目录权限: `chmod 700 ~/.ssh`
2. 检查authorized_keys权限: `chmod 600 ~/.ssh/authorized_keys`
3. 检查SSH服务配置: `/etc/ssh/sshd_config`
   - 确保以下配置正确:
   ```
   PubkeyAuthentication yes
   AuthorizedKeysFile .ssh/authorized_keys
   ```
4. 重启SSH服务: `sudo systemctl restart sshd`

### 问题2: Permission denied

**解决方案:**
1. 检查文件权限:
   ```bash
   ls -la ~/.ssh/
   ```
   应该是:
   ```
   drwx------  2 tpk tpk .ssh
   -rw-------  1 tpk tpk authorized_keys
   ```

2. 如果权限不对,执行:
   ```bash
   chmod 700 ~/.ssh
   chmod 600 ~/.ssh/authorized_keys
   chown tpk:tpk ~/.ssh -R
   ```

### 问题3: Connection refused

**解决方案:**
1. 检查SSH服务是否运行:
   ```bash
   sudo systemctl status sshd
   ```

2. 如果未运行,启动服务:
   ```bash
   sudo systemctl start sshd
   sudo systemctl enable sshd
   ```

---

## 安全建议

1. **不要泄露私钥**: `~/.ssh/id_ed25519` 必须保密
2. **使用强密码**: 即使有密钥认证,也建议使用强密码
3. **定期更换**: 可以定期生成新的SSH密钥
4. **限制来源**: 在 `/etc/hosts.allow` 中限制允许访问的IP

---

配置完成后,你就可以直接使用 `./deploy.sh` 进行一键部署了!
