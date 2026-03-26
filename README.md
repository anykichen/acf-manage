# ACF管控系统

ACF（各向异性导电胶）物料全生命周期管控系统，实现从来料入库、存储、发料、退库到报废的完整追踪，精准管控库存、有效期、使用次数与在库时长。

## 项目简介

### 核心功能

- **基础数据管理**：料号管理、标签模板、LOT号规则、预警规则配置
- **来料管理**：来料扫码识别、LOT号生成、标签打印
- **库存管理**：入库、发料、退库、报废
- **库存监控**：实时库存查询、多维度预警、报表统计
- **可视化看板**：大屏动态展示、业务状态看板

### 技术亮点

- 🎯 **全生命周期追踪**：记录每卷ACF从入库到报废的所有操作
- ⏱️ **时区统一管理**：所有时间字段使用曼谷时区（UTC+7）
- 📦 **FIFO先进先出**：智能推荐入库最早且未过期的物料
- 🚨 **智能预警机制**：过期预警、使用次数预警、库存低水位预警
- 🏷️ **标签自动打印**：5cm×10cm标准标签，支持扫码填充

## 技术架构

### 后端技术栈

- **框架**：Spring Boot 3.2.3
- **ORM**：MyBatis-Plus 3.5.5
- **数据库**：MySQL 8.0
- **缓存**：Redis 7.0
- **API文档**：SpringDoc OpenAPI 3
- **工具库**：Hutool、Lombok

### 前端技术栈

- **框架**：Vue 3.4 + TypeScript
- **UI框架**：Element Plus 2.6
- **状态管理**：Pinia 2.1
- **路由**：Vue Router 4.2
- **图表**：ECharts 5.5
- **HTTP客户端**：Axios 1.6
- **构建工具**：Vite 5.0

## 快速开始

### 环境要求

- JDK 17+
- Node.js 18+
- MySQL 8.0+
- Redis 7.0+
- Maven 3.8+

### 数据库初始化

```bash
# 创建数据库
mysql -u root -p < sql/init_database.sql
```

### 后端启动

```bash
cd acf-backend

# 修改配置文件中的数据库密码
# vim src/main/resources/application.yml

# 启动项目
mvn spring-boot:run
```

后端地址：http://localhost:8080/api
API文档：http://localhost:8080/api/swagger-ui.html

### 前端启动

```bash
cd acf-frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端地址：http://localhost:3000

### 默认账号

- 用户名：admin
- 密码：Admin@123

## 项目结构

```
acf/
├── sql/                          # 数据库脚本
│   └── init_database.sql         # 数据库初始化
├── acf-backend/                  # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/acf/
│   │   │   │   ├── config/       # 配置类
│   │   │   │   ├── controller/   # 控制器
│   │   │   │   ├── service/      # 服务层
│   │   │   │   ├── mapper/       # 数据访问层
│   │   │   │   ├── entity/       # 实体类
│   │   │   │   ├── dto/          # 数据传输对象
│   │   │   │   ├── vo/           # 视图对象
│   │   │   │   └── common/       # 公共类
│   │   │   └── resources/
│   │   │       ├── application.yml
│   │   │       └── mapper/       # MyBatis XML
│   │   └── test/
│   └── pom.xml
├── acf-frontend/                 # 前端项目
│   ├── public/
│   ├── src/
│   │   ├── api/                  # API接口
│   │   ├── assets/               # 静态资源
│   │   ├── components/           # 组件
│   │   ├── layout/               # 布局
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
│   ├── DATABASE_DESIGN.md
│   └── SETUP_GUIDE.md
└── README.md
```

## 开发规范

### 代码规范

- 后端遵循阿里巴巴Java开发规范
- 前端遵循Vue官方风格指南
- 统一使用UTF-8编码

### Git提交规范

```
feat: 新功能
fix: 修复bug
docs: 文档更新
style: 代码格式调整
refactor: 重构
test: 测试相关
chore: 构建/工具相关
```

### 接口规范

- RESTful API设计
- 统一响应格式
- 统一异常处理

## 开发进度

### Phase 1: 项目初始化 ✅
- [x] 项目脚手架搭建
- [x] 数据库设计与建表脚本
- [x] 后端基础框架（配置、实体、Mapper、Service、Controller）
- [x] 前端基础框架（路由、API、状态管理、页面）

### Phase 2: 基础数据管理 🚧
- [ ] 料号管理完善
- [ ] 标签模板管理
- [ ] LOT号规则管理
- [ ] 预警规则管理

### Phase 3: 来料与标签打印
- [ ] 来料扫码识别
- [ ] LOT号生成
- [ ] 标签打印服务

### Phase 4: 库存管理
- [ ] 入库管理
- [ ] 发料管理
- [ ] 退库管理
- [ ] 报废管理

### Phase 5: 库存监控与预警
- [ ] 实时库存查询
- [ ] 预警机制
- [ ] 报表统计

### Phase 6: 可视化看板
- [ ] 大屏动态看板
- [ ] 业务状态看板

### Phase 7: 系统优化与测试
- [ ] 性能优化
- [ ] 单元测试
- [ ] 集成测试

### Phase 8: 部署上线
- [ ] 生产环境配置
- [ ] 正式部署
- [ ] 用户培训

详细开发计划请查看：[DEVELOPMENT_PLAN.md](./DEVELOPMENT_PLAN.md)

## 文档

- [需求文档](./需求.md)
- [开发计划](./DEVELOPMENT_PLAN.md)
- [数据库设计](./DATABASE_DESIGN.md)
- [开发环境搭建](./SETUP_GUIDE.md)

## 标签模板

系统提供5cm×10cm标准标签模板，包含：
- LOT号（竖排）
- 型号/规格（竖排）
- 保存期限
- 三次领用记录（领发开始时间、归还时间、回温时间、使用人员、备注）

标签预览：[acf_label_v2.html](./acf_label_v2.html)

## 许可证

Copyright © 2026 ACF Team. All rights reserved.

## 联系方式

- 项目负责人：ACF Team
- 技术支持：support@acf.com
