# ACF管控系统 - 端口配置(最终版本)

## 📋 端口配置

根据服务器当前端口占用情况,配置如下端口:

| 服务 | 容器端口 | 主机端口 | 说明 |
|------|---------|---------|------|
| **前端** | 80 | **8082** | Web界面 |
| **后端** | 8080 | **28080** | API服务 |
| **MySQL** | 3306 | **23306** | 数据库 |
| **Redis** | 6379 | **26379** | 缓存 |

---

## 🌐 访问地址

### 访问系统
- **前端页面**: http://10.201.2.40:8082
- **后端API**: http://10.201.2.40:28080
- **API文档**: http://10.201.2.40:28080/swagger-ui.html
- **健康检查**: http://10.201.2.40:28080/actuator/health

### 数据库连接
- **MySQL主机**: 10.201.2.40
- **MySQL端口**: 23306
- **MySQL用户**: acf_user / acf123
- **Redis主机**: 10.201.2.40
- **Redis端口**: 26379
- **Redis密码**: redis123

---

## 🚀 部署命令

### 启动部署
```bash
cd /Users/chenqing/Desktop/acf
nohup ./deploy.sh > /tmp/acf-deploy-final.log 2>&1 &
```

### 监控部署
```bash
tail -f /tmp/acf-deploy-final.log
```

### 验证部署
```bash
# 检查容器状态
ssh tpk@10.201.2.40 "cd /home/tpk/acf && docker-compose ps"

# 测试访问
curl -I http://10.201.2.40:8082
curl http://10.201.2.40:28080/actuator/health
```

---

## ✅ 验证清单

部署完成后,按以下步骤验证:

### 1. 检查容器
```bash
ssh tpk@10.201.2.40 "docker ps | grep acf"
```

应该看到4个容器: acf-mysql, acf-redis, acf-backend, acf-frontend

### 2. 检查端口映射
```bash
ssh tpk@10.201.2.40 "docker port acf-frontend"
ssh tpk@10.201.2.40 "docker port acf-backend"
ssh tpk@10.201.2.40 "docker port acf-mysql"
ssh tpk@10.201.2.40 "docker port acf-redis"
```

### 3. 测试前端
在浏览器打开: http://10.201.2.40:8082

### 4. 测试后端
```bash
curl http://10.201.2.40:28080/actuator/health
```

应该返回: {"status":"UP"}

### 5. 登录系统
- 用户名: admin
- 密码: admin123

---

## 📝 重要提示

1. **端口选择**: 使用了高位端口(2xxx),避免与其他项目冲突
2. **容器内部不变**: Docker容器内部仍使用原端口(80, 8080, 3306, 6379)
3. **仅主机端口改变**: 从外部访问时使用新端口
4. **容器间通信正常**: 容器之间通信不受影响

---

## 🔧 管理命令

### 查看日志
```bash
ssh tpk@10.201.2.40 "cd /home/tpk/acf && docker-compose logs -f"
```

### 重启服务
```bash
ssh tpk@10.201.2.40 "cd /home/tpk/acf && docker-compose restart"
```

### 停止服务
```bash
ssh tpk@10.201.2.40 "cd /home/tpk/acf && docker-compose stop"
```

### 查看状态
```bash
ssh tpk@10.201.2.40 "cd /home/tpk/acf && docker-compose ps"
```

---

## 🎯 快速开始

1. **部署系统**: 执行部署命令
2. **等待完成**: 约10-20分钟
3. **访问系统**: http://10.201.2.40:8082
4. **登录**: admin / admin123
5. **修改密码**: 登录后立即修改!

---

*端口配置更新: 2026-03-26*
