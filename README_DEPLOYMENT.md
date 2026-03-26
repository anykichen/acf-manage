# ACF管控系统 - 部署文档

## 环境要求

### 系统要求
- 操作系统：Linux (CentOS 7+ / Ubuntu 18.04+) 或 Windows Server
- 内存：至少 4GB RAM（推荐 8GB+）
- 磁盘：至少 50GB 可用空间
- CPU：至少 2 核（推荐 4 核+）

### 软件要求
- Java JDK 17+
- MySQL 8.0+
- Redis 6.0+
- Nginx 1.18+（可选，用于前端部署）
- Docker 20.10+（可选，用于容器化部署）

---

## 数据库部署

### 1. 创建数据库

```sql
-- 创建数据库
CREATE DATABASE acf_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建用户
CREATE USER 'acf_user'@'%' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON acf_db.* TO 'acf_user'@'%';
FLUSH PRIVILEGES;
```

### 2. 执行初始化脚本

```bash
mysql -u acf_user -p acf_db < sql/init_database.sql
mysql -u acf_user -p acf_db < sql/add_lot_rule_table.sql
mysql -u acf_user -p acf_db < sql/optimize_database.sql
```

### 3. 验证数据库

```bash
mysql -u acf_user -p acf_db -e "SHOW TABLES;"
```

---

## 后端部署

### 方式一：传统部署

#### 1. 编译打包

```bash
cd acf-backend
mvn clean package -DskipTests
```

#### 2. 配置文件

修改 `application-prod.yml`：

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://your_mysql_host:3306/acf_db?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Bangkok
    username: acf_user
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
    
  data:
    redis:
      host: your_redis_host
      port: 6379
      password: your_redis_password
      database: 0

mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.slf4j.Slf4jImpl
```

#### 3. 启动服务

```bash
java -jar target/acf-backend-1.0.0.jar --spring.profiles.active=prod
```

#### 4. 后台运行

```bash
nohup java -jar target/acf-backend-1.0.0.jar --spring.profiles.active=prod > app.log 2>&1 &
```

### 方式二：Docker部署

#### 1. 构建镜像

```dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/acf-backend-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

```bash
docker build -t acf-backend:1.0.0 .
```

#### 2. 运行容器

```bash
docker run -d \
  --name acf-backend \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/acf_db \
  -e SPRING_DATASOURCE_USERNAME=acf_user \
  -e SPRING_DATASOURCE_PASSWORD=your_password \
  -e SPRING_DATA_REDIS_HOST=redis \
  acf-backend:1.0.0
```

---

## 前端部署

### 方式一：Nginx部署

#### 1. 编译打包

```bash
cd acf-frontend
npm install
npm run build
```

#### 2. Nginx配置

```nginx
server {
    listen 80;
    server_name your_domain.com;

    root /var/www/acf/dist;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}
```

#### 3. 部署文件

```bash
cp -r dist/* /var/www/acf/
systemctl restart nginx
```

### 方式二：Docker部署

```dockerfile
FROM nginx:alpine
COPY dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
```

```bash
docker build -t acf-frontend:1.0.0 .
docker run -d --name acf-frontend -p 80:80 acf-frontend:1.0.0
```

---

## Redis部署

### 使用Docker部署Redis

```bash
docker run -d \
  --name redis \
  -p 6379:6379 \
  -v redis-data:/data \
  redis:6-alpine \
  redis-server --requirepass your_password
```

---

## 验证部署

### 1. 检查后端服务

```bash
curl http://localhost:8080/api/material/list
```

### 2. 检查前端页面

浏览器访问：`http://your_domain.com`

### 3. 检查数据库连接

```bash
mysql -u acf_user -p -h your_mysql_host acf_db -e "SELECT COUNT(*) FROM acf_material;"
```

### 4. 检查Redis连接

```bash
redis-cli -h your_redis_host -p 6379 -a your_password ping
```

---

## 监控和日志

### 日志位置

- 后端日志：`/var/log/acf/app.log`
- Nginx日志：`/var/log/nginx/access.log` 和 `/var/log/nginx/error.log`

### 监控指标

- 应用启动状态
- 数据库连接数
- Redis连接状态
- 内存使用情况
- CPU使用情况

---

## 备份和恢复

### 数据库备份

```bash
# 备份
mysqldump -u acf_user -p acf_db > backup_$(date +%Y%m%d).sql

# 恢复
mysql -u acf_user -p acf_db < backup_20260326.sql
```

### Redis备份

```bash
# 备份
redis-cli -h your_redis_host -a your_password SAVE
cp /var/lib/redis/dump.rdb backup/

# 恢复
cp backup/dump.rdb /var/lib/redis/
redis-cli -h your_redis_host -a your_password SHUTDOWN NOSAVE
redis-server /etc/redis/redis.conf
```

---

## 常见问题

### 1. 端口被占用

```bash
# 查看端口占用
lsof -i:8080

# 杀死进程
kill -9 <PID>
```

### 2. 数据库连接失败

- 检查数据库地址、端口、用户名、密码
- 检查防火墙设置
- 检查数据库权限

### 3. Redis连接失败

- 检查Redis服务是否启动
- 检查Redis密码配置
- 检查防火墙设置

---

## 性能优化建议

1. **数据库优化**
   - 定期执行 `ANALYZE TABLE` 更新统计信息
   - 定期清理慢查询日志
   - 考虑使用读写分离

2. **应用优化**
   - 启用Redis缓存热点数据
   - 调整JVM参数（-Xms, -Xmx）
   - 使用连接池（Druid）

3. **Nginx优化**
   - 启用Gzip压缩
   - 配置缓存策略
   - 使用负载均衡

---

## 安全建议

1. 修改默认密码（数据库、Redis）
2. 启用HTTPS（SSL证书）
3. 配置防火墙规则
4. 定期更新系统和依赖
5. 启用操作日志审计

---

*文档版本：1.0.0*
*最后更新：2026-03-26*
