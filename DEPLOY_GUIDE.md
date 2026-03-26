# ACF管控系统 - 部署说明

## 方式一:使用部署脚本 (推荐)

### 前置条件

1. **配置SSH密钥认证** (推荐)
```bash
# 在本地生成SSH密钥(如果没有)
ssh-keygen -t rsa -b 4096 -C "your_email@example.com"

# 将公钥复制到服务器
ssh-copy-id tpk@10.201.2.40

# 测试连接
ssh tpk@10.201.2.40
```

2. **服务器环境要求**
   - Docker 20.10+
   - Docker Compose 1.29+
   - 至少 4GB RAM
   - 至少 50GB 磁盘空间

### 部署步骤

#### 1. 首次完整部署

```bash
cd /Users/chenqing/Desktop/acf
./deploy.sh
```

**部署流程:**
1. 检查本地环境配置文件
2. 测试服务器连接
3. 检查服务器Docker环境
4. 清理服务器旧容器
5. 上传文件到服务器
6. 构建并启动容器
7. 验证服务状态

#### 2. 快速部署 (不重新构建)

如果只是修改了配置文件或少量代码,可以使用快速部署:

```bash
./quick-deploy.sh
```

#### 3. 查看服务日志

```bash
./view-logs.sh
```

#### 4. 重启服务

```bash
./restart.sh
```

#### 5. 停止服务

```bash
./stop.sh
```

#### 6. 查看服务状态

```bash
./status.sh
```

#### 7. 进入容器

```bash
# 进入后端容器
./ssh-container.sh backend

# 进入MySQL容器
./ssh-container.sh mysql

# 进入Redis容器
./ssh-container.sh redis
```

---

## 方式二: 手动部署

### 1. 上传项目文件到服务器

```bash
# 方式A: 使用rsync (推荐)
rsync -avz --exclude 'node_modules' --exclude 'target' \
    /Users/chenqing/Desktop/acf/ \
    tpk@10.201.2.40:/home/tpk/acf/

# 方式B: 使用scp
cd /Users/chenqing/Desktop/acf
tar -czf acf.tar.gz \
    --exclude='node_modules' \
    --exclude='target' \
    --exclude='dist' \
    .

scp acf.tar.gz tpk@10.201.2.40:/tmp/

# 在服务器上解压
ssh tpk@10.201.2.40
cd /home/tpk
tar -xzf /tmp/acf.tar.gz
```

### 2. 在服务器上配置环境变量

```bash
ssh tpk@10.201.2.40
cd /home/tpk/acf

# 创建.env文件(可选,用于覆盖docker-compose.yml中的配置)
cat > .env << 'EOF'
# 数据库配置
MYSQL_ROOT_PASSWORD=root123
MYSQL_DATABASE=acf_db
MYSQL_USER=acf_user
MYSQL_PASSWORD=acf123

# Redis配置
REDIS_PASSWORD=redis123

# JWT密钥(生产环境请修改)
JWT_SECRET=your-production-secret-key-must-be-at-least-256-bits-long-for-hs256-algorithm
JWT_EXPIRATION=86400000

# 日志级别
LOG_LEVEL=INFO

# Swagger开关
SWAGGER_ENABLED=false
EOF
```

### 3. 构建并启动容器

```bash
cd /home/tpk/acf

# 构建镜像
docker-compose build

# 启动容器
docker-compose up -d

# 查看日志
docker-compose logs -f
```

### 4. 验证部署

```bash
# 检查容器状态
docker-compose ps

# 检查后端健康状态
curl http://localhost:8080/actuator/health

# 检查前端
curl http://localhost:80

# 查看详细日志
docker-compose logs --tail=50
```

---

## 配置说明

### 1. 修改端口映射

如果服务器上的端口被占用,可以修改`docker-compose.yml`:

```yaml
services:
  mysql:
    ports:
      - "3307:3306"  # 将MySQL端口改为3307
  
  redis:
    ports:
      - "6380:6379"  # 将Redis端口改为6380
  
  backend:
    ports:
      - "9090:8080"  # 将后端端口改为9090
  
  frontend:
    ports:
      - "8080:80"    # 将前端端口改为8080
```

### 2. 修改数据库密码

**方式A: 修改docker-compose.yml**
```yaml
services:
  mysql:
    environment:
      MYSQL_ROOT_PASSWORD: your_new_root_password
      MYSQL_PASSWORD: your_new_user_password
  
  backend:
    environment:
      DB_PASSWORD: your_new_user_password
```

**方式B: 使用.env文件**
```bash
MYSQL_ROOT_PASSWORD=your_new_root_password
MYSQL_PASSWORD=your_new_user_password
```

### 3. 修改Redis密码

```yaml
services:
  redis:
    command: redis-server --requirepass your_new_redis_password
  
  backend:
    environment:
      REDIS_PASSWORD: your_new_redis_password
```

### 4. 修改JWT密钥 (重要!)

生产环境必须修改JWT密钥:

```yaml
services:
  backend:
    environment:
      JWT_SECRET: $(openssl rand -base64 32)
```

或使用.env文件:
```bash
JWT_SECRET=$(openssl rand -base64 32)
```

---

## 数据库迁移

### 首次部署

数据库会自动初始化,无需手动操作。初始化脚本按顺序执行:
1. `sql/init_database.sql` - 创建表和初始数据
2. `sql/add_lot_rule_table.sql` - 添加LOT号规则表
3. `sql/optimize_database.sql` - 添加索引优化

### 数据库备份

```bash
# 备份
docker exec acf-mysql mysqldump -u acf_user -pacf123 acf_db > backup_$(date +%Y%m%d).sql

# 恢复
docker exec -i acf-mysql mysql -u acf_user -pacf123 acf_db < backup_20260326.sql
```

### 数据库升级

如果需要执行新的SQL脚本:

```bash
# 上传SQL脚本
scp new_migration.sql tpk@10.201.2.40:/tmp/

# 在服务器上执行
ssh tpk@10.201.2.40
docker exec -i acf-mysql mysql -u acf_user -pacf123 acf_db < /tmp/new_migration.sql
```

---

## 故障排查

### 1. 容器无法启动

```bash
# 查看容器日志
docker-compose logs backend
docker-compose logs mysql
docker-compose logs redis

# 查看容器详细状态
docker-compose ps -a
```

### 2. 数据库连接失败

```bash
# 测试数据库连接
docker exec acf-mysql mysql -u acf_user -pacf123 -e "SELECT 1;"

# 检查数据库网络
docker network inspect acf_acf-network
```

### 3. Redis连接失败

```bash
# 测试Redis连接
docker exec acf-redis redis-cli -a redis123 ping

# 检查Redis日志
docker-compose logs redis
```

### 4. 端口被占用

```bash
# 查看端口占用
ssh tpk@10.201.2.40
sudo lsof -i:80
sudo lsof -i:8080
sudo lsof -i:3306
sudo lsof -i:6379

# 修改docker-compose.yml中的端口映射
```

### 5. 内存不足

```bash
# 查看系统资源
ssh tpk@10.201.2.40
free -h
df -h

# 查看Docker资源使用
docker stats
```

---

## 性能优化

### 1. 调整JVM参数

修改`docker-compose.yml`中的JVM参数:

```yaml
services:
  backend:
    environment:
      JAVA_OPTS: "-Xms512m -Xmx1024m -XX:+UseG1GC"
```

- `-Xms`: 初始堆内存
- `-Xmx`: 最大堆内存
- `-XX:+UseG1GC`: 使用G1垃圾收集器

### 2. 调整MySQL配置

```yaml
services:
  mysql:
    command: [
      "--character-set-server=utf8mb4",
      "--collation-server=utf8mb4_unicode_ci",
      "--max-connections=200",
      "--innodb-buffer-pool-size=1G"
    ]
```

### 3. 启用Nginx缓存

修改`acf-frontend/nginx.conf`:

```nginx
proxy_cache_path /var/cache/nginx levels=1:2 keys_zone=api_cache:10m max_size=1g inactive=60m;

location /api {
    proxy_cache api_cache;
    proxy_cache_valid 200 5m;
    proxy_pass http://backend:8080;
}
```

---

## 监控和日志

### 查看实时日志

```bash
# 查看所有服务日志
docker-compose logs -f

# 查看特定服务日志
docker-compose logs -f backend
docker-compose logs -f mysql

# 查看最近N行日志
docker-compose logs --tail=100
```

### 日志文件持久化

日志会自动保存到Docker volume:
- 后端日志: `backend-logs` volume
- MySQL日志: `mysql-data` volume

### 健康检查

```bash
# 检查后端健康状态
curl http://10.201.2.40:8080/actuator/health

# 检查数据库健康状态
docker exec acf-mysql mysqladmin ping -h localhost -u root -proot123

# 检查Redis健康状态
docker exec acf-redis redis-cli -a redis123 ping
```

---

## 访问地址

部署成功后,可以通过以下地址访问系统:

- **前端页面**: http://10.201.2.40:80
- **后端API**: http://10.201.2.40:8080
- **API文档**: http://10.201.2.40:8080/swagger-ui.html
- **健康检查**: http://10.201.2.40:8080/actuator/health

## 默认账号

- **系统管理员**: `admin` / `admin123`
- **数据库root**: `root` / `root123`
- **应用数据库**: `acf_user` / `acf123`
- **Redis密码**: `redis123`

⚠️ **重要**: 生产环境部署后请立即修改默认密码!

---

## 升级部署

### 升级后端代码

```bash
# 1. 上传新的jar包
scp acf-backend/target/acf-backend-1.0.0.jar tpk@10.201.2.40:/tmp/

# 2. 在服务器上替换jar包
ssh tpk@10.201.2.40
docker cp /tmp/acf-backend-1.0.0.jar acf-backend:/app/app.jar
docker restart acf-backend
```

### 升级前端代码

```bash
# 1. 本地构建前端
cd acf-frontend
npm run build

# 2. 上传构建产物
cd dist
tar -czf dist.tar.gz .
scp dist.tar.gz tpk@10.201.2.40:/tmp/

# 3. 在服务器上替换前端文件
ssh tpk@10.201.2.40
docker exec acf-frontend sh -c "rm -rf /usr/share/nginx/html/*"
docker cp - /tmp/dist.tar.gz | docker exec -i acf-frontend sh -c "tar -xzf - -C /usr/share/nginx/html/"
docker restart acf-frontend
```

### 升级整个应用

```bash
cd /Users/chenqing/Desktop/acf
./deploy.sh
```

---

## 技术支持

如遇到问题,请:
1. 查看日志: `./view-logs.sh`
2. 检查状态: `./status.sh`
3. 查看本文档的故障排查章节

---

*部署版本: 1.0.0*  
*最后更新: 2026-03-26*
