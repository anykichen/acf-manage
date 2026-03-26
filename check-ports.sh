#!/bin/bash

# 检查服务器端口占用情况

SERVER="tpk@10.201.2.40"

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}  检查服务器端口占用${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# 需要检查的端口列表
PORTS=(80 8080 8081 8082 3000 9000 9001 9002 9003 9004 9005 13306 13307 13308 16379 16380 16381 16382)

echo -e "${YELLOW}检查端口占用...${NC}"
echo ""

AVAILABLE_PORTS=()
OCCUPIED_PORTS=()

for port in "${PORTS[@]}"; do
    if ssh -o ConnectTimeout=2 $SERVER "lsof -i:$port 2>/dev/null | grep LISTEN" > /dev/null 2>&1; then
        echo -e "${RED}✗ $port${NC} - 已占用"
        OCCUPIED_PORTS+=($port)
    else
        echo -e "${GREEN}✓ $port${NC} - 可用"
        AVAILABLE_PORTS+=($port)
    fi
done

echo ""
echo -e "${BLUE}========================================${NC}"
echo -e "${GREEN}可用端口推荐:${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# 推荐端口配置
echo -e "前端可用端口: ${GREEN}${AVAILABLE_PORTS[0]:-8082}${NC}"
echo -e "后端可用端口: ${GREEN}${AVAILABLE_PORTS[1]:-9001}${NC}"
echo -e "MySQL可用端口: ${GREEN}${AVAILABLE_PORTS[2]:-13307}${NC}"
echo -e "Redis可用端口: ${GREEN}${AVAILABLE_PORTS[3]:-16380}${NC}"
echo ""

echo -e "${YELLOW}自动修改端口配置...${NC}"

FRONTEND_PORT=${AVAILABLE_PORTS[0]:-8082}
BACKEND_PORT=${AVAILABLE_PORTS[1]:-9001}
MYSQL_PORT=${AVAILABLE_PORTS[2]:-13307}
REDIS_PORT=${AVAILABLE_PORTS[3]:-16380}

echo ""
echo -e "新端口配置:"
echo -e "  前端: ${GREEN}$FRONTEND_PORT${NC}"
echo -e "  后端: ${GREEN}$BACKEND_PORT${NC}"
echo -e "  MySQL: ${GREEN}$MYSQL_PORT${NC}"
echo -e "  Redis: ${GREEN}$REDIS_PORT${NC}"
echo ""

# 更新docker-compose.yml
cat > /Users/chenqing/Desktop/acf/docker-compose.yml << EOF
version: '3.8'

services:
  # MySQL数据库
  mysql:
    image: mysql:8.0
    container_name: acf-mysql
    environment:
      MYSQL_ROOT_PASSWORD: root123
      MYSQL_DATABASE: acf_db
      MYSQL_USER: acf_user
      MYSQL_PASSWORD: acf123
    ports:
      - "$MYSQL_PORT:3306"
    volumes:
      - mysql-data:/var/lib/mysql
      - ./sql/init_database.sql:/docker-entrypoint-initdb.d/01-init.sql
      - ./sql/add_lot_rule_table.sql:/docker-entrypoint-initdb.d/02-lot-rule.sql
      - ./sql/optimize_database.sql:/docker-entrypoint-initdb.d/03-optimize.sql
    networks:
      - acf-network
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      timeout: 20s
      retries: 10

  # Redis缓存
  redis:
    image: redis:6-alpine
    container_name: acf-redis
    command: redis-server --requirepass redis123
    ports:
      - "$REDIS_PORT:6379"
    volumes:
      - redis-data:/data
    networks:
      - acf-network
    healthcheck:
      test: ["CMD", "redis-cli", "-a", "redis123", "ping"]
      interval: 10s
      timeout: 3s
      retries: 5

  # 后端服务
  backend:
    build:
      context: ./acf-backend
      dockerfile: Dockerfile
    container_name: acf-backend
    environment:
      DB_HOST: mysql
      DB_PORT: 3306
      DB_NAME: acf_db
      DB_USERNAME: acf_user
      DB_PASSWORD: acf123
      REDIS_HOST: redis
      REDIS_PORT: 6379
      REDIS_PASSWORD: redis123
      JWT_SECRET: your-production-secret-key-must-be-at-least-256-bits-long-for-hs256-algorithm
      JWT_EXPIRATION: 86400000
      LOG_LEVEL: INFO
      SWAGGER_ENABLED: false
    ports:
      - "$BACKEND_PORT:8080"
    volumes:
      - backend-logs:/var/log/acf
    networks:
      - acf-network
    depends_on:
      mysql:
        condition: service_healthy
      redis:
        condition: service_healthy
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 60s

  # 前端服务
  frontend:
    build:
      context: ./acf-frontend
      dockerfile: Dockerfile
    container_name: acf-frontend
    ports:
      - "$FRONTEND_PORT:80"
    networks:
      - acf-network
    depends_on:
      - backend

networks:
  acf-network:
    driver: bridge

volumes:
  mysql-data:
    driver: local
  redis-data:
    driver: local
  backend-logs:
    driver: local
EOF

echo -e "${GREEN}✓ docker-compose.yml 已更新${NC}"

# 保存端口配置到文件
cat > /Users/chenqing/Desktop/acf/.port_config << EOF
FRONTEND_PORT=$FRONTEND_PORT
BACKEND_PORT=$BACKEND_PORT
MYSQL_PORT=$MYSQL_PORT
REDIS_PORT=$REDIS_PORT
EOF

echo -e "${GREEN}✓ 端口配置已保存${NC}"
echo ""
echo -e "${BLUE}========================================${NC}"
echo -e "${GREEN}  端口配置完成！${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""
echo -e "${YELLOW}访问地址:${NC}"
echo -e "  前端: ${GREEN}http://10.201.2.40:$FRONTEND_PORT${NC}"
echo -e "  后端: ${GREEN}http://10.201.2.40:$BACKEND_PORT${NC}"
echo -e "  API文档: ${GREEN}http://10.201.2.40:$BACKEND_PORT/swagger-ui.html${NC}"
echo ""
