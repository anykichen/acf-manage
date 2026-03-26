# ACF管控系统 - 快速部署参考

## 🚀 快速部署 (3种方式)

### 方式1: 自动部署 (需SSH密钥)
```bash
cd /Users/chenqing/Desktop/acf
./deploy.sh
```

### 方式2: 手动部署 (推荐)
```bash
cd /Users/chenqing/Desktop/acf
./manual-deploy.sh
```

### 方式3: 完全手动
1. 打包: `tar -czf acf.tar.gz acf-backend acf-frontend sql docker-compose.yml`
2. 上传: `scp acf.tar.gz tpk@10.201.2.40:/tmp/`
3. 解压: `ssh tpk@10.201.2.40 "cd /home/tpk/acf && tar -xzf /tmp/acf.tar.gz"`
4. 启动: `docker-compose up -d`

---

## 📋 部署后验证

```bash
# 检查容器状态
ssh tpk@10.201.2.40 "cd /home/tpk/acf && docker-compose ps"

# 检查后端健康
curl http://10.201.2.40:8080/actuator/health

# 检查前端
curl http://10.201.2.40:80

# 查看日志
./view-logs.sh
```

---

## 🔧 常用管理命令

```bash
# 查看日志
./view-logs.sh

# 重启服务
./restart.sh

# 停止服务
./stop.sh

# 查看状态
./status.sh

# 进入后端容器
./ssh-container.sh backend

# 进入MySQL容器
./ssh-container.sh mysql

# 进入Redis容器
./ssh-container.sh redis
```

---

## 🌐 访问地址

- **前端**: http://10.201.2.40:8081
- **后端**: http://10.201.2.40:18080
- **API文档**: http://10.201.2.40:18080/swagger-ui.html

---

## 🔐 默认账号

- **系统管理员**: `admin` / `admin123`
- **数据库root**: `root` / `root123`
- **应用数据库**: `acf_user` / `acf123`
- **Redis密码**: `redis123`

⚠️ **部署后立即修改密码!**

---

## 📊 端口说明

| 服务 | 容器端口 | 主机端口 |
|------|---------|---------|
| 前端(Nginx) | 80 | 8081 |
| 后端(Spring Boot) | 8080 | 18080 |
| MySQL | 3306 | 13306 |
| Redis | 6379 | 16379 |

---

## 🐳 Docker命令

```bash
# 查看所有容器
ssh tpk@10.201.2.40 "docker ps -a"

# 查看日志
ssh tpk@10.201.2.40 "docker logs acf-backend"

# 重启单个容器
ssh tpk@10.201.2.40 "docker restart acf-backend"

# 停止所有容器
ssh tpk@10.201.2.40 "cd /home/tpk/acf && docker-compose stop"

# 启动所有容器
ssh tpk@10.201.2.40 "cd /home/tpk/acf && docker-compose start"

# 删除所有容器(保留数据)
ssh tpk@10.201.2.40 "cd /home/tpk/acf && docker-compose down"

# 删除所有容器和数据(危险!)
ssh tpk@10.201.2.40 "cd /home/tpk/acf && docker-compose down -v"
```

---

## 💾 数据库操作

```bash
# 连接数据库
ssh tpk@10.201.2.40 "docker exec -it acf-mysql mysql -u acf_user -pacf123 acf_db"

# 备份数据库
ssh tpk@10.201.2.40 "docker exec acf-mysql mysqldump -u acf_user -pacf123 acf_db > backup.sql"

# 恢复数据库
ssh tpk@10.201.2.40 "docker exec -i acf-mysql mysql -u acf_user -pacf123 acf_db < backup.sql"

# 查看表
ssh tpk@10.201.2.40 "docker exec acf-mysql mysql -u acf_user -pacf123 acf_db -e 'SHOW TABLES;'"
```

---

## 🔴 Redis操作

```bash
# 连接Redis
ssh tpk@10.201.2.40 "docker exec -it acf-redis redis-cli -a redis123"

# 查看所有键
ssh tpk@10.201.2.40 "docker exec acf-redis redis-cli -a redis123 KEYS '*'"

# 清空缓存
ssh tpk@10.201.2.40 "docker exec acf-redis redis-cli -a redis123 FLUSHALL"

# 查看内存使用
ssh tpk@10.201.2.40 "docker exec acf-redis redis-cli -a redis123 INFO memory"
```

---

## 📈 性能监控

```bash
# 查看容器资源使用
ssh tpk@10.201.2.40 "docker stats"

# 查看磁盘使用
ssh tpk@10.201.2.40 "df -h"

# 查看内存使用
ssh tpk@10.201.2.40 "free -h"

# 查看CPU使用
ssh tpk@10.201.2.40 "top -n 1"
```

---

## 🐛 故障排查

### 容器无法启动
```bash
# 查看详细日志
./view-logs.sh

# 查看容器状态
./status.sh

# 检查配置文件
ssh tpk@10.201.2.40 "cd /home/tpk/acf && cat docker-compose.yml"
```

### 数据库连接失败
```bash
# 测试数据库连接
ssh tpk@10.201.2.40 "docker exec acf-mysql mysqladmin ping -h localhost -u root -proot123"

# 检查数据库日志
ssh tpk@10.201.2.40 "docker logs acf-mysql"
```

### Redis连接失败
```bash
# 测试Redis连接
ssh tpk@10.201.2.40 "docker exec acf-redis redis-cli -a redis123 ping"

# 检查Redis日志
ssh tpk@10.201.2.40 "docker logs acf-redis"
```

### 端口被占用
```bash
# 查看端口占用
ssh tpk@10.201.2.40 "sudo lsof -i:80 -i:8080 -i:3306 -i:6379"

# 杀死占用进程
ssh tpk@10.201.2.40 "sudo kill -9 <PID>"
```

---

## 📚 文档索引

- `DEPLOY_GUIDE.md` - 详细部署指南
- `DEPLOY_CHECK.md` - 部署前检查清单
- `README_DEPLOYMENT.md` - 部署文档
- `USER_MANUAL.md` - 用户操作手册
- `DEVELOPMENT_PROGRESS.md` - 开发进度
- `PROJECT_SUMMARY.md` - 项目总结

---

## 🎯 部署清单

- [ ] 配置SSH密钥或准备好密码
- [ ] 检查服务器Docker环境
- [ ] 检查服务器内存和磁盘空间
- [ ] 检查端口是否被占用
- [ ] 执行部署脚本
- [ ] 验证容器状态
- [ ] 验证服务健康
- [ ] 修改默认密码
- [ ] 配置防火墙
- [ ] 配置SSL证书(可选)

---

*快速参考版本: 1.0.0*  
*最后更新: 2026-03-26*
