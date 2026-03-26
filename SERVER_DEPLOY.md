# ACF管控系统 - 服务器部署指南

## 📋 当前端口占用情况

你的服务器上已运行的容器:
- label-printer: 5000
- label-printer-old: 8080
- docker-manager-app: 8081
- attendance-system: 3000

**因此我们使用以下端口:**
- 前端: 8082
- 后端: 28080
- MySQL: 23306
- Redis: 26379

---

## 🚀 快速部署步骤

### 方式1: 在服务器上手动部署(推荐)

登录服务器:
```bash
ssh tpk@10.201.2.40
```

在服务器上依次执行:

```bash
# 1. 创建部署目录
cd /home/tpk
rm -rf acf
mkdir -p acf
cd acf

# 2. 复制文件(从你的Mac)
# 在Mac上执行:
tar -czf acf.tar.gz acf-backend acf-frontend sql docker-compose.yml
scp acf.tar.gz tpk@10.201.2.40:/tmp/

# 3. 在服务器上解压
cd /home/tpk
tar -xzf /tmp/acf.tar.gz -C acf

# 4. 启动服务
cd acf
docker-compose up -d --build

# 5. 查看日志
docker-compose logs -f

# 6. 检查状态
docker-compose ps
```

---

### 方式2: 使用本地快速部署脚本

在你的Mac上执行:

```bash
cd /Users/chenqing/Desktop/acf
./deploy-fast.sh
```

这个脚本会自动:
1. 打包项目文件
2. 上传到服务器
3. 在服务器上构建并启动

---

## 📊 验证部署

部署完成后,在浏览器访问:

- **前端**: http://10.201.2.40:8082
- **后端**: http://10.201.2.40:28080
- **API文档**: http://10.201.2.40:28080/swagger-ui.html

默认登录账号: `admin` / `admin123`

---

## 🔍 常用命令

在服务器上执行:

```bash
cd /home/tpk/acf

# 查看日志
docker-compose logs -f

# 查看容器状态
docker-compose ps

# 重启服务
docker-compose restart

# 停止服务
docker-compose down

# 重新构建
docker-compose up -d --build

# 进入后端容器
docker-compose exec backend bash

# 进入MySQL容器
docker-compose exec mysql mysql -u acf_user -p acf
```

---

## ❓ 故障排查

### 1. 端口冲突
```bash
# 检查端口占用
netstat -tlnp | grep -E '8082|28080|23306|26379'

# 如果冲突,修改 docker-compose.yml 中的端口
```

### 2. 容器启动失败
```bash
# 查看详细日志
docker-compose logs backend
docker-compose logs frontend
docker-compose logs mysql
docker-compose logs redis
```

### 3. 数据库连接失败
```bash
# 检查MySQL容器是否正常运行
docker-compose ps mysql

# 测试数据库连接
docker-compose exec mysql mysql -u acf_user -pacf123 acf
```

---

## 📝 注意事项

1. **首次部署需要构建镜像**,可能需要10-20分钟
2. **部署完成后立即修改默认密码**
3. **定期备份数据库**: `docker-compose exec mysql mysqldump -u acf_user -p acf > backup.sql`
4. **定期清理未使用的镜像**: `docker system prune -a`

---

## 🆘 需要帮助?

如果遇到问题,请提供:
1. `docker-compose ps` 输出
2. `docker-compose logs` 输出
3. 具体的错误信息
