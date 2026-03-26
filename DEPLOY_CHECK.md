# ACF管控系统 - 部署前检查清单

## ✅ 文件检查

### 部署脚本文件
- [x] `deploy.sh` - 完整部署脚本
- [x] `quick-deploy.sh` - 快速部署脚本
- [x] `manual-deploy.sh` - 手动部署指南
- [x] `view-logs.sh` - 查看日志
- [x] `restart.sh` - 重启服务
- [x] `stop.sh` - 停止服务
- [x] `status.sh` - 查看状态
- [x] `ssh-container.sh` - 进入容器

### Docker配置文件
- [x] `docker-compose.yml` - Docker编排配置
- [x] `acf-backend/Dockerfile` - 后端镜像构建
- [x] `acf-frontend/Dockerfile` - 前端镜像构建
- [x] `acf-frontend/nginx.conf` - Nginx配置

### 数据库脚本
- [x] `sql/init_database.sql` - 数据库初始化
- [x] `sql/add_lot_rule_table.sql` - LOT号规则表
- [x] `sql/optimize_database.sql` - 数据库优化索引

### 后端配置
- [x] `acf-backend/src/main/resources/application-prod.yml` - 生产环境配置
- [x] `acf-backend/pom.xml` - Maven依赖配置
- [x] `acf-backend/src/main/java/...` - 后端源代码

### 前端配置
- [x] `acf-frontend/package.json` - 前端依赖
- [x] `acf-frontend/src/...` - 前端源代码

### 文档
- [x] `DEPLOY_GUIDE.md` - 详细部署指南
- [x] `README_DEPLOYMENT.md` - 部署文档
- [x] `USER_MANUAL.md` - 用户手册

---

## ✅ 配置检查

### 服务器信息
- 服务器地址: `10.201.2.40`
- 用户名: `tpk`
- 部署目录: `/home/tpk/acf`

### 端口配置
- 前端: `80`
- 后端: `8080`
- MySQL: `3306`
- Redis: `6379`

### 默认账号密码
```
系统管理员: admin / admin123
数据库root: root / root123
应用数据库: acf_user / acf123
Redis密码: redis123
```

⚠️ **生产环境部署后必须修改默认密码!**

---

## 📋 部署方式选择

### 方式1: 自动部署 (需要SSH密钥)

**前提条件:**
- SSH密钥已配置 (ssh-copy-id)
- 服务器已安装Docker和Docker Compose

**执行步骤:**
```bash
cd /Users/chenqing/Desktop/acf
./deploy.sh
```

**优点:**
- 自动化程度高
- 一键完成所有部署
- 包含完整验证

**缺点:**
- 需要配置SSH密钥
- 首次部署时间较长(需要构建镜像)

---

### 方式2: 手动部署 (推荐)

**前提条件:**
- 可以SSH登录到服务器
- 服务器已安装Docker和Docker Compose

**执行步骤:**
```bash
cd /Users/chenqing/Desktop/acf
./manual-deploy.sh
```

**优点:**
- 不需要SSH密钥
- 每个步骤都可以控制
- 适合生产环境

**缺点:**
- 需要手动执行多个步骤

---

### 方式3: 完全手动

**适用场景:**
- 无法使用自动脚本
- 需要完全控制每个环节

**详细步骤:**
参见 `DEPLOY_GUIDE.md` 中的"方式二: 手动部署"章节

---

## 🔍 部署前服务器检查

### 1. 检查服务器连接
```bash
ssh tpk@10.201.2.40 "echo 'Connection OK'"
```

### 2. 检查Docker版本
```bash
ssh tpk@10.201.2.40 "docker --version"
```

### 3. 检查Docker Compose版本
```bash
ssh tpk@10.201.2.40 "docker-compose --version"
```

### 4. 检查系统资源
```bash
ssh tpk@10.201.2.40 "free -h; df -h"
```

### 5. 检查端口占用
```bash
ssh tpk@10.201.2.40 "sudo lsof -i:80 -i:8080 -i:3306 -i:6379 || echo 'Ports are free'"
```

---

## 📦 部署文件大小

```bash
# 检查部署包大小
ls -lh acf-deploy.tar.gz

# 预计大小: 约50-100MB
```

---

## ⏱️ 预计部署时间

### 自动部署
- 上传文件: 5-10分钟 (取决于网络)
- 构建镜像: 10-20分钟
- 启动容器: 2-5分钟
- **总计: 约20-40分钟**

### 手动部署
- 上传文件: 5-10分钟
- 构建镜像: 10-20分钟
- 启动容器: 2-5分钟
- **总计: 约20-40分钟**

---

## ✅ 部署成功验证

### 1. 检查容器状态
```bash
ssh tpk@10.201.2.40 "cd /home/tpk/acf && docker-compose ps"
```

所有容器应该是 `Up` 状态。

### 2. 检查后端健康
```bash
curl http://10.201.2.40:8080/actuator/health
```

应该返回 `{"status":"UP"}`

### 3. 检查前端页面
```bash
curl -I http://10.201.2.40:80
```

应该返回 `HTTP/1.1 200 OK`

### 4. 检查数据库
```bash
ssh tpk@10.201.2.40 "docker exec acf-mysql mysqladmin ping -h localhost -u root -proot123"
```

应该返回 `mysqld is alive`

### 5. 检查Redis
```bash
ssh tpk@10.201.2.40 "docker exec acf-redis redis-cli -a redis123 ping"
```

应该返回 `PONG`

---

## 🚨 常见问题

### Q1: SSH连接失败
**A:** 
1. 检查网络连接
2. 检查用户名和密码
3. 考虑使用SSH密钥认证

### Q2: Docker未安装
**A:**
参考 `DEPLOY_GUIDE.md` 安装Docker和Docker Compose

### Q3: 端口被占用
**A:**
修改 `docker-compose.yml` 中的端口映射

### Q4: 内存不足
**A:**
1. 增加服务器内存
2. 调整JVM参数 (减少堆内存)
3. 调整MySQL配置 (减少buffer pool)

### Q5: 容器启动失败
**A:**
```bash
ssh tpk@10.201.2.40
cd /home/tpk/acf
docker-compose logs --tail=100
```
查看日志找出具体错误

---

## 📞 技术支持

### 文档资源
- `DEPLOY_GUIDE.md` - 详细部署指南
- `README_DEPLOYMENT.md` - 部署文档
- `USER_MANUAL.md` - 用户操作手册

### 常用命令
```bash
# 查看日志
./view-logs.sh

# 重启服务
./restart.sh

# 查看状态
./status.sh

# 停止服务
./stop.sh

# 进入容器
./ssh-container.sh backend
./ssh-container.sh mysql
./ssh-container.sh redis
```

---

## ✅ 部署完成后的安全配置

### 1. 修改默认密码
```bash
ssh tpk@10.201.2.40
cd /home/tpk/acf

# 修改.env文件中的密码
vi .env
```

### 2. 配置防火墙
```bash
# 仅开放必要的端口
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw allow 22/tcp
sudo ufw enable
```

### 3. 配置SSL证书 (推荐)
使用Let's Encrypt免费SSL证书:
```bash
sudo apt install certbot python3-certbot-nginx
sudo certbot --nginx -d your-domain.com
```

### 4. 启用日志审计
```bash
# 查看操作日志
docker exec acf-backend cat /var/log/acf/app.log
```

---

*检查清单版本: 1.0.0*  
*最后更新: 2026-03-26*
