# ACF管控系统 - 数据库设计

## 数据库命名规范

### 数据库信息
- **数据库名**：acf_control_system
- **字符集**：utf8mb4
- **排序规则**：utf8mb4_unicode_ci
- **时区**：Asia/Bangkok (UTC+7)

### 表命名规范
- 表名：小写字母+下划线，如 `acf_inventory`
- 字段名：小写字母+下划线，如 `create_time`
- 主键：`id` (BIGINT AUTO_INCREMENT)
- 必须字段：
  - `create_time` (DATETIME) - 创建时间
  - `update_time` (DATETIME) - 更新时间
  - `deleted` (TINYINT) - 软删除标记 (0-正常, 1-删除)

---

## 表结构设计

### 1. 料号表 (acf_material)
```sql
CREATE TABLE `acf_material` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `material_code` VARCHAR(50) NOT NULL COMMENT '料号',
  `material_name` VARCHAR(200) NOT NULL COMMENT '料号名称',
  `material_desc` VARCHAR(500) COMMENT '物料描述',
  `unit` VARCHAR(20) NOT NULL COMMENT '单位',
  `barcode_rule` VARCHAR(500) COMMENT '条码解析规则（正则表达式）',
  `manufacturer` VARCHAR(100) COMMENT '厂商',
  `model` VARCHAR(100) COMMENT '型号',
  `shelf_life_months` INT NOT NULL DEFAULT 6 COMMENT '保存期限（月）',
  `max_usage_times` INT NOT NULL DEFAULT 3 COMMENT '最大使用次数',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态（1-启用, 0-停用）',
  `remark` VARCHAR(500) COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记（0-正常, 1-删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_material_code` (`material_code`),
  KEY `idx_manufacturer_model` (`manufacturer`, `model`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='ACF料号表';
```

### 2. 标签模板表 (acf_label_template)
```sql
CREATE TABLE `acf_label_template` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `template_name` VARCHAR(100) NOT NULL COMMENT '模板名称',
  `template_code` VARCHAR(50) NOT NULL COMMENT '模板编码',
  `width` DECIMAL(5,2) NOT NULL COMMENT '宽度(cm)',
  `height` DECIMAL(5,2) NOT NULL COMMENT '高度(cm)',
  `fields_config` JSON NOT NULL COMMENT '字段配置（JSON格式）',
  `print_driver` VARCHAR(100) COMMENT '打印驱动类型',
  `is_default` TINYINT NOT NULL DEFAULT 0 COMMENT '是否默认模板（1-是, 0-否）',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态（1-启用, 0-停用）',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记（0-正常, 1-删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_template_code` (`template_code`),
  KEY `idx_is_default` (`is_default`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标签模板表';
```

### 3. LOT号规则表 (acf_lot_rule)
```sql
CREATE TABLE `acf_lot_rule` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `rule_name` VARCHAR(100) NOT NULL COMMENT '规则名称',
  `rule_code` VARCHAR(50) NOT NULL COMMENT '规则编码',
  `lot_prefix` VARCHAR(20) COMMENT 'LOT号前缀',
  `lot_format` VARCHAR(100) NOT NULL COMMENT 'LOT号格式（如：LOT{YYYYMMDD}{SEQ}）',
  `qr_code_format` VARCHAR(100) COMMENT '二维码格式',
  `description` VARCHAR(500) COMMENT '描述',
  `is_active` TINYINT NOT NULL DEFAULT 1 COMMENT '是否激活（1-是, 0-否）',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记（0-正常, 1-删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_rule_code` (`rule_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='LOT号生成规则表';
```

### 4. 预警规则表 (acf_alert_rule)
```sql
CREATE TABLE `acf_alert_rule` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `rule_name` VARCHAR(100) NOT NULL COMMENT '规则名称',
  `rule_type` VARCHAR(20) NOT NULL COMMENT '预警类型（EXPIRE-过期, USAGE-使用次数, STOCK-库存）',
  `alert_days_before_expire` INT COMMENT '过期预警天数',
  `min_stock_quantity` DECIMAL(10,2) COMMENT '最小库存数量',
  `max_usage_times` INT COMMENT '最大使用次数',
  `alert_method` VARCHAR(50) NOT NULL DEFAULT 'SYSTEM' COMMENT '预警方式（SYSTEM-系统, EMAIL-邮件, SMS-短信）',
  `is_active` TINYINT NOT NULL DEFAULT 1 COMMENT '是否激活（1-是, 0-否）',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记（0-正常, 1-删除）',
  PRIMARY KEY (`id`),
  KEY `idx_rule_type` (`rule_type`),
  KEY `idx_is_active` (`is_active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='预警规则表';
```

### 5. LOT号主表 (acf_lot)
```sql
CREATE TABLE `acf_lot` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `lot_number` VARCHAR(100) NOT NULL COMMENT 'LOT号',
  `material_code` VARCHAR(50) NOT NULL COMMENT '料号',
  `qr_code` VARCHAR(200) COMMENT '二维码',
  `inbound_date` DATETIME NOT NULL COMMENT '入库时间',
  `expire_date` DATETIME NOT NULL COMMENT '过期时间',
  `initial_quantity` DECIMAL(10,2) NOT NULL COMMENT '初始数量',
  `current_quantity` DECIMAL(10,2) NOT NULL COMMENT '当前数量',
  `usage_times` INT NOT NULL DEFAULT 0 COMMENT '使用次数',
  `warehouse_location` VARCHAR(50) COMMENT '储位',
  `lot_status` VARCHAR(20) NOT NULL DEFAULT 'IN_STOCK' COMMENT 'LOT状态（IN_STOCK-在库, IN_USE-使用中, SCRAPPED-报废）',
  `remark` VARCHAR(500) COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记（0-正常, 1-删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_lot_number` (`lot_number`),
  KEY `idx_material_code` (`material_code`),
  KEY `idx_inbound_date` (`inbound_date`),
  KEY `idx_expire_date` (`expire_date`),
  KEY `idx_lot_status` (`lot_status`),
  KEY `idx_warehouse_location` (`warehouse_location`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='LOT号主表';
```

### 6. 出入库记录表 (acf_transaction)
```sql
CREATE TABLE `acf_transaction` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `transaction_no` VARCHAR(50) NOT NULL COMMENT '交易单号',
  `lot_number` VARCHAR(100) NOT NULL COMMENT 'LOT号',
  `transaction_type` VARCHAR(20) NOT NULL COMMENT '交易类型（INBOUND-入库, OUTBOUND-发料, RETURN-退库, SCRAPPED-报废）',
  `quantity` DECIMAL(10,2) NOT NULL COMMENT '数量',
  `warehouse_location` VARCHAR(50) COMMENT '储位',
  `operator_id` BIGINT NOT NULL COMMENT '操作人ID',
  `operator_name` VARCHAR(50) NOT NULL COMMENT '操作人姓名',
  `warmup_start_time` DATETIME COMMENT '回温开始时间',
  `warmup_end_time` DATETIME COMMENT '回温结束时间',
  `warmup_duration` INT COMMENT '回温时长（分钟）',
  `remark` VARCHAR(500) COMMENT '备注',
  `transaction_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '交易时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记（0-正常, 1-删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_transaction_no` (`transaction_no`),
  KEY `idx_lot_number` (`lot_number`),
  KEY `idx_transaction_type` (`transaction_type`),
  KEY `idx_transaction_time` (`transaction_time`),
  KEY `idx_operator_id` (`operator_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='出入库记录表';
```

### 7. 操作日志表 (acf_operation_log)
```sql
CREATE TABLE `acf_operation_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `operation_type` VARCHAR(50) NOT NULL COMMENT '操作类型',
  `lot_number` VARCHAR(100) COMMENT 'LOT号',
  `operation_module` VARCHAR(50) COMMENT '操作模块',
  `operation_detail` TEXT COMMENT '操作详情',
  `operator_id` BIGINT NOT NULL COMMENT '操作人ID',
  `operator_name` VARCHAR(50) NOT NULL COMMENT '操作人姓名',
  `ip_address` VARCHAR(50) COMMENT 'IP地址',
  `operation_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`id`),
  KEY `idx_operation_type` (`operation_type`),
  KEY `idx_lot_number` (`lot_number`),
  KEY `idx_operator_id` (`operator_id`),
  KEY `idx_operation_time` (`operation_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';
```

### 8. 用户表 (sys_user)
```sql
CREATE TABLE `sys_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(100) NOT NULL COMMENT '密码',
  `real_name` VARCHAR(50) NOT NULL COMMENT '真实姓名',
  `email` VARCHAR(100) COMMENT '邮箱',
  `phone` VARCHAR(20) COMMENT '手机号',
  `department` VARCHAR(100) COMMENT '部门',
  `user_type` VARCHAR(20) NOT NULL DEFAULT 'OPERATOR' COMMENT '用户类型（ADMIN-管理员, OPERATOR-操作员, VIEWER-查看者）',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态（1-启用, 0-停用）',
  `last_login_time` DATETIME COMMENT '最后登录时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记（0-正常, 1-删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_status` (`status`),
  KEY `idx_user_type` (`user_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';
```

### 9. 角色表 (sys_role)
```sql
CREATE TABLE `sys_role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_code` VARCHAR(50) NOT NULL COMMENT '角色编码',
  `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称',
  `role_desc` VARCHAR(200) COMMENT '角色描述',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态（1-启用, 0-停用）',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记（0-正常, 1-删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';
```

### 10. 菜单表 (sys_menu)
```sql
CREATE TABLE `sys_menu` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `parent_id` BIGINT NOT NULL DEFAULT 0 COMMENT '父菜单ID',
  `menu_name` VARCHAR(50) NOT NULL COMMENT '菜单名称',
  `menu_code` VARCHAR(50) NOT NULL COMMENT '菜单编码',
  `menu_type` VARCHAR(20) NOT NULL COMMENT '菜单类型（DIRECTORY-目录, MENU-菜单, BUTTON-按钮）',
  `menu_url` VARCHAR(200) COMMENT '菜单URL',
  `menu_icon` VARCHAR(50) COMMENT '菜单图标',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态（1-启用, 0-停用）',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记（0-正常, 1-删除）',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_menu_type` (`menu_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜单表';
```

---

## 索引设计说明

### 核心索引
1. **acf_lot表**：
   - `uk_lot_number`: LOT号唯一索引
   - `idx_material_code`: 按料号查询
   - `idx_inbound_date`: FIFO排序查询
   - `idx_expire_date`: 过期预警查询
   - `idx_lot_status`: 按状态筛选

2. **acf_transaction表**：
   - `uk_transaction_no`: 交易单号唯一索引
   - `idx_lot_number`: 按LOT号查询
   - `idx_transaction_type`: 按交易类型查询
   - `idx_transaction_time`: 按时间范围查询

3. **acf_inventory表**（如需要）：
   - 复合索引：`(material_code, warehouse_location)`

---

## 初始化数据

### 默认预警规则
```sql
INSERT INTO `acf_alert_rule` (`rule_name`, `rule_type`, `alert_days_before_expire`, `min_stock_quantity`, `max_usage_times`, `alert_method`, `is_active`)
VALUES
('过期预警规则', 'EXPIRE', 7, NULL, NULL, 'SYSTEM', 1),
('库存预警规则', 'STOCK', NULL, 10.00, NULL, 'SYSTEM', 1),
('使用次数预警规则', 'USAGE', NULL, NULL, 3, 'SYSTEM', 1);
```

### 默认标签模板
```sql
INSERT INTO `acf_label_template` (`template_name`, `template_code`, `width`, `height`, `fields_config`, `is_default`, `status`)
VALUES
('ACF标准标签模板', 'ACF_STANDARD', 5.00, 10.00,
'{"fields":["lot_number","model_spec","shelf_life","usage1","usage2","usage3"],"layout":"standard"}',
1, 1);
```

### 默认LOT号规则
```sql
INSERT INTO `acf_lot_rule` (`rule_name`, `rule_code`, `lot_prefix`, `lot_format`, `qr_code_format`, `is_active`)
VALUES
('标准LOT号规则', 'STANDARD', 'LOT', 'LOT{YYYYMMDD}{SEQ:4}', '{lot_number}', 1);
```

### 默认管理员用户
```sql
INSERT INTO `sys_user` (`username`, `password`, `real_name`, `user_type`, `status`)
VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', 'ADMIN', 1);
-- 默认密码：Admin@123
```

---

*最后更新时间：2026-03-26*
