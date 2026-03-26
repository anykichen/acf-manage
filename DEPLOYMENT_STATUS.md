# ACF管控系统 - 部署状态

## ✅ 当前状态: 正在部署中

### 📊 部署进度

| 阶段 | 状态 | 说明 |
|------|------|------|
| 本地环境检查 | ✅ 完成 | 已通过 |
| 服务器连接 | ✅ 完成 | 免密登录已配置 |
| 文件上传 | ✅ 完成 | 已上传到服务器 |
| Docker镜像构建 | 🔄 进行中 | 正在下载基础镜像 |
| 容器启动 | ⏳ 等待中 | 等待镜像构建完成 |
| 服务验证 | ⏳ 等待中 | 等待容器启动完成 |

### 📝 部署日志最后更新
```
#5 [backend stage-1 1/4] FROM docker.io/library/eclipse-temurin:17-jre...
正在下载JRE镜像 (817807f3c64e0b90b66edc7d90297f121cad2a7c2a3ee05a731557762f91e6c7)
进度: 2.10MB / 29.73MB (7%)
```

---

## 🔍 监控命令

### 查看实时日志
```bash
tail -f /tmp/acf-deploy.log
```

### 检查部署状态
```bash
bash /Users/chenqing/Desktop/acf/check-deploy.sh
```

### 查看部署进程
```bash
ps aux | grep deploy.sh
```

---

## ⏱️ 预计时间

- Docker镜像下载: 10-15分钟
- Maven依赖下载: 5-10分钟
- 后端构建: 3-5分钟
- 前端构建: 2-3分钟
- 容器启动: 1-2分钟

**总计预计: 20-35分钟**

---

## 📋 部署完成后

### 访问地址
- **前端**: http://10.201.2.40:80
- **后端**: http://10.201.2.40:8080
- **API文档**: http://10.201.2.40:8080/swagger-ui.html

### 默认账号
- **系统管理员**: admin / admin123
- **数据库root**: root / root123
- **应用数据库**: acf_user / acf123
- **Redis密码**: redis123

⚠️ **部署完成后立即修改默认密码!**

### 验证部署
```bash
# 检查容器状态
ssh tpk@10.201.2.40 "cd /home/tpk/acf && docker-compose ps"

# 检查后端健康
curl http://10.201.2.40:8080/actuator/health

# 检查前端
curl -I http://10.201.2.40:80
```

---

## 🛠️ 部署完成后的管理命令

```bash
# 查看日志
./view-logs.sh

# 重启服务
./restart.sh

# 查看状态
./status.sh

# 停止服务
./stop.sh

# 进入后端容器
./ssh-container.sh backend

# 进入MySQL容器
./ssh-container.sh mysql

# 进入Redis容器
./ssh-container.sh redis
```

---

## 📖 相关文档

- `DEPLOY_GUIDE.md` - 详细部署指南
- `QUICK_START.md` - 快速参考
- `DEPLOY_CHECK.md` - 部署前检查
- `USER_MANUAL.md` - 用户操作手册

---

*状态更新时间: 2026-03-26 23:55*
