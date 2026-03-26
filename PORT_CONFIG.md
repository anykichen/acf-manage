# ACF管控系统 - 端口配置说明

## 📋 新端口配置

由于服务器上其他项目占用了默认端口,已修改为以下端口:

| 服务 | 容器端口 | 主机端口 | 原端口 |
|------|---------|---------|--------|
| 前端(Nginx) | 80 | **8081** | 80 |
| 后端(Spring Boot) | 8080 | **18080** | 8080 |
| MySQL | 3306 | **13306** | 3306 |
| Redis | 6379 | **16379** | 6379 |

---

## 🌐 访问地址

### 访问系统
- **前端页面**: http://10.201.2.40:8081
- **后端API**: http://10.201.2.40:18080
- **API文档**: http://10.201.2.40:18080/swagger-ui.html
- **健康检查**: http://10.201.2.40:18080/actuator/health

### 数据库连接
- **MySQL主机**: 10.201.2.40
- **MySQL端口**: 13306
- **MySQL用户**: acf_user / acf123
- **Redis主机**: 10.201.2.40
- **Redis端口**: 16379
- **Redis密码**: redis123

---

## 🔧 端口冲突排查

### 检查端口占用
```bash
# 在服务器上执行
ssh tpk@10.201.2.40 "sudo lsof -i:80 -i:8080 -i:3306 -i:6379 -i:8081 -i:18080 -i:13306 -i:16379"
```

### 如果新端口仍然冲突,可以继续修改

修改 `docker-compose.yml` 中的端口映射:

```yaml
services:
  mysql:
    ports:
      - "13306:3306"  # 修改为其他端口,如 23306

  redis:
    ports:
      - "16379:6379"  # 修改为其他端口,如 26379

  backend:
    ports:
      - "18080:8080"  # 修改为其他端口,如 28080

  frontend:
    ports:
      - "8081:80"    # 修改为其他端口,如 8082
```

---

## 📝 注意事项

1. **容器内部端口不变**: Docker容器内部仍然使用原端口(80, 8080, 3306, 6379)
2. **主机端口已修改**: 只有从外部访问时使用新端口
3. **容器间通信正常**: 容器之间仍然使用原端口通信,不受影响

---

## 🚀 部署命令

### 重新部署
```bash
cd /Users/chenqing/Desktop/acf
nohup ./deploy.sh > /tmp/acf-deploy.log 2>&1 &
```

### 监控部署
```bash
tail -f /tmp/acf-deploy.log
```

### 验证部署
```bash
# 检查容器状态
ssh tpk@10.201.2.40 "cd /home/tpk/acf && docker-compose ps"

# 检查端口映射
ssh tpk@10.201.2.40 "docker port acf-frontend"
ssh tpk@10.201.2.40 "docker port acf-backend"
ssh tpk@10.201.2.40 "docker port acf-mysql"
ssh tpk@10.201.2.40 "docker port acf-redis"

# 测试访问
curl -I http://10.201.2.40:8081
curl http://10.201.2.40:18080/actuator/health
```

---

## 🎯 快速验证

部署完成后,在浏览器访问:

1. **前端页面**: http://10.201.2.40:8081
2. 使用账号登录: admin / admin123
3. 如果登录成功,说明系统运行正常!

---

*端口配置更新: 2026-03-26*
