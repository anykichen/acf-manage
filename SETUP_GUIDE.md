# ACF管控系统 - 开发环境搭建指南

## 环境要求

### 必需软件
- **JDK**: 17 或更高版本
- **Maven**: 3.8+
- **Node.js**: 18 或更高版本
- **MySQL**: 8.0+
- **Redis**: 7.0+
- **Git**: 2.x

### 推荐IDE
- **后端**: IntelliJ IDEA 2023+
- **前端**: VS Code 或 IntelliJ IDEA

---

## 项目目录结构

```
acf/
├── sql/                          # 数据库脚本
│   └── init_database.sql         # 数据库初始化脚本
├── acf-backend/                  # 后端项目
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   └── com/acf/
│   │       │       ├── config/   # 配置类
│   │       │       ├── controller/# 控制器
│   │       │       ├── service/  # 服务层
│   │       │       ├── mapper/   # 数据访问层
│   │       │       ├── entity/   # 实体类
│   │       │       ├── dto/      # 数据传输对象
│   │       │       ├── vo/       # 视图对象
│   │       │       ├── common/   # 公共类
│   │       │       └── AcfApplication.java
│   │       └── resources/
│   │           ├── application.yml
│   │           ├── application-dev.yml
│   │           ├── application-prod.yml
│   │           └── mapper/       # MyBatis XML
│   └── pom.xml
├── acf-frontend/                 # 前端项目
│   ├── public/
│   ├── src/
│   │   ├── api/                  # API接口
│   │   ├── assets/               # 静态资源
│   │   ├── components/           # 组件
│   │   ├── router/               # 路由
│   │   ├── store/                # 状态管理
│   │   ├── utils/                # 工具类
│   │   ├── views/                # 页面
│   │   ├── App.vue
│   │   └── main.ts
│   ├── package.json
│   └── vite.config.ts
├── docs/                         # 文档
│   ├── 需求.md
│   ├── DEVELOPMENT_PLAN.md
│   └── DATABASE_DESIGN.md
└── README.md
```

---

## 后端环境搭建

### 1. 创建后端项目

```bash
# 创建项目目录
mkdir -p acf/acf-backend/src/main/{java/com/acf,resources}

# 使用Spring Initializr创建项目（推荐）
# 访问 https://start.spring.io/
# 选择：
#   - Project: Maven
#   - Language: Java
#   - Spring Boot: 3.2.x
#   - Packaging: Jar
#   - Java: 17
# Dependencies:
#   - Spring Web
#   - MyBatis Framework
#   - MySQL Driver
#   - Spring Data Redis
#   - Validation
#   - Lombok
```

### 2. 配置pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.3</version>
        <relativePath/>
    </parent>

    <groupId>com.acf</groupId>
    <artifactId>acf-backend</artifactId>
    <version>1.0.0</version>
    <name>ACF Backend</name>
    <description>ACF管控系统后端服务</description>

    <properties>
        <java.version>17</java.version>
        <mybatis-plus.version>3.5.5</mybatis-plus.version>
        <druid.version>1.2.20</druid.version>
        <hutool.version>5.8.23</hutool.version>
        <jwt.version>0.12.3</jwt.version>
        <springdoc.version>2.3.0</springdoc.version>
    </properties>

    <dependencies>
        <!-- Spring Boot Starters -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-cache</artifactId>
        </dependency>

        <!-- MyBatis Plus -->
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>${mybatis-plus.version}</version>
        </dependency>

        <!-- Druid -->
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid-spring-boot-starter</artifactId>
            <version>${druid.version}</version>
        </dependency>

        <!-- MySQL Driver -->
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <scope>runtime</scope>
        </dependency>

        <!-- Lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- Hutool -->
        <dependency>
            <groupId>cn.hutool</groupId>
            <artifactId>hutool-all</artifactId>
            <version>${hutool.version}</version>
        </dependency>

        <!-- JWT -->
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>${jwt.version}</version>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>${jwt.version}</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>${jwt.version}</version>
            <scope>runtime</scope>
        </dependency>

        <!-- SpringDoc OpenAPI -->
        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
            <version>${springdoc.version}</version>
        </dependency>

        <!-- Test -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

### 3. 配置application.yml

```yaml
spring:
  application:
    name: acf-backend
  profiles:
    active: dev

  # 数据源配置
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    druid:
      driver-class-name: com.mysql.cj.jdbc.Driver
      url: jdbc:mysql://localhost:3306/acf_control_system?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Bangkok&allowPublicKeyRetrieval=true
      username: root
      password: your_password
      initial-size: 5
      min-idle: 5
      max-active: 20
      max-wait: 60000
      time-between-eviction-runs-millis: 60000
      min-evictable-idle-time-millis: 300000
      validation-query: SELECT 1 FROM DUAL
      test-while-idle: true
      test-on-borrow: false
      test-on-return: false
      filters: stat,wall
      connection-properties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=5000

  # Redis配置
  redis:
    host: localhost
    port: 6379
    password:
    database: 0
    timeout: 3000
    lettuce:
      pool:
        max-active: 8
        max-wait: -1
        max-idle: 8
        min-idle: 0

  # 缓存配置
  cache:
    type: redis
    redis:
      time-to-live: 1800000

# MyBatis Plus配置
mybatis-plus:
  mapper-locations: classpath*:mapper/**/*.xml
  type-aliases-package: com.acf.entity
  configuration:
    map-underscore-to-camel-case: true
    cache-enabled: false
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      id-type: auto
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0

# 服务器配置
server:
  port: 8080
  servlet:
    context-path: /api

# JWT配置
jwt:
  secret: your-secret-key-change-this-in-production
  expiration: 86400000

# SpringDoc配置
springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui.html
  default-flat-param-object: true

# 日志配置
logging:
  level:
    com.acf.mapper: debug
    com.acf: info
  pattern:
    console: '%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n'
```

### 4. 启动类

```java
package com.acf;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.acf.mapper")
public class AcfApplication {
    public static void main(String[] args) {
        SpringApplication.run(AcfApplication.class, args);
    }
}
```

---

## 前端环境搭建

### 1. 创建Vue 3项目

```bash
# 使用Vite创建项目
cd acf
npm create vite@latest acf-frontend -- --template vue-ts

cd acf-frontend

# 安装依赖
npm install
```

### 2. 安装额外依赖

```bash
# UI框架
npm install element-plus

# 状态管理
npm install pinia

# 路由
npm install vue-router

# HTTP客户端
npm install axios

# 图表库
npm install echarts

# 工具库
npm install dayjs
npm install lodash-es

# 日期选择器中文包
npm install dayjs
```

### 3. 配置vite.config.ts

```typescript
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '/api')
      }
    }
  }
})
```

### 4. tsconfig.json路径别名

```json
{
  "compilerOptions": {
    "target": "ES2020",
    "useDefineForClassFields": true,
    "module": "ESNext",
    "lib": ["ES2020", "DOM", "DOM.Iterable"],
    "skipLibCheck": true,
    "moduleResolution": "bundler",
    "allowImportingTsExtensions": true,
    "resolveJsonModule": true,
    "isolatedModules": true,
    "noEmit": true,
    "jsx": "preserve",
    "strict": true,
    "noUnusedLocals": true,
    "noUnusedParameters": true,
    "noFallthroughCasesInSwitch": true,
    "baseUrl": ".",
    "paths": {
      "@/*": ["src/*"]
    }
  },
  "include": ["src/**/*.ts", "src/**/*.d.ts", "src/**/*.tsx", "src/**/*.vue"],
  "references": [{ "path": "./tsconfig.node.json" }]
}
```

---

## 数据库初始化

### 1. 创建数据库

```bash
# 登录MySQL
mysql -u root -p

# 执行初始化脚本
source /Users/chenqing/Desktop/acf/sql/init_database.sql

# 或使用命令行
mysql -u root -p < /Users/chenqing/Desktop/acf/sql/init_database.sql
```

### 2. 验证数据库

```bash
# 查看数据库
SHOW DATABASES;

# 使用数据库
USE acf_control_system;

# 查看表
SHOW TABLES;

# 验证数据
SELECT * FROM acf_material;
SELECT * FROM sys_user;
```

---

## 启动项目

### 后端启动

```bash
cd acf/acf-backend

# 编译项目
mvn clean install

# 运行项目
mvn spring-boot:run

# 或使用IDE运行 AcfApplication.main()
```

### 前端启动

```bash
cd acf/acf-frontend

# 安装依赖
npm install

# 开发模式启动
npm run dev

# 构建生产版本
npm run build
```

---

## 访问地址

- **后端API**: http://localhost:8080/api
- **API文档**: http://localhost:8080/api/swagger-ui.html
- **前端页面**: http://localhost:3000
- **默认账号**: admin / Admin@123

---

## 常见问题

### 1. 数据库连接失败
- 检查MySQL服务是否启动
- 检查用户名密码是否正确
- 检查数据库是否已创建

### 2. Redis连接失败
- 检查Redis服务是否启动
- 检查Redis端口是否正确

### 3. 前端编译报错
- 清除node_modules重新安装
- 检查Node.js版本是否满足要求

### 4. 后端启动失败
- 检查JDK版本是否为17+
- 检查Maven依赖是否完整下载
- 查看日志定位具体错误

---

*最后更新时间：2026-03-26*
