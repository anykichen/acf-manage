-- ACF管控系统 - 数据库初始化脚本
-- 数据库: acf_control_system
-- 字符集: utf8mb4
-- 排序规则: utf8mb4_unicode_ci
-- 时区: Asia/Bangkok (UTC+7)

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `acf_control_system` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `acf_control_system`;

-- 设置时区
SET time_zone = '+07:00';

-- ============================================
-- 1. 料号表
-- ============================================
DROP TABLE IF EXISTS `acf_material`;
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

-- ============================================
-- 2. 标签模板表
-- ============================================
DROP TABLE IF EXISTS `acf_label_template`;
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

-- ============================================
-- 3. LOT号规则表
-- ============================================
DROP TABLE IF EXISTS `acf_lot_rule`;
CREATE TABLE `acf_lot_rule` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `rule_name` VARCHAR(100) NOT NULL COMMENT '规则名称',
  `rule_code` VARCHAR(50) NOT NULL COMMENT '规则编码',
  `lot_prefix` VARCHAR(20) COMMENT 'LOT号前缀',
  `lot_format` VARCHAR(100) NOT NULL COMMENT 'LOT号格式（如：LOT{YYYYMMDD}{SEQ:4}）',
  `qr_code_format` VARCHAR(100) COMMENT '二维码格式',
  `description` VARCHAR(500) COMMENT '描述',
  `is_active` TINYINT NOT NULL DEFAULT 1 COMMENT '是否激活（1-是, 0-否）',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记（0-正常, 1-删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_rule_code` (`rule_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='LOT号生成规则表';

-- ============================================
-- 4. 预警规则表
-- ============================================
DROP TABLE IF EXISTS `acf_alert_rule`;
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

-- ============================================
-- 5. LOT号主表
-- ============================================
DROP TABLE IF EXISTS `acf_lot`;
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

-- ============================================
-- 6. 出入库记录表
-- ============================================
DROP TABLE IF EXISTS `acf_transaction`;
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

-- ============================================
-- 7. 操作日志表
-- ============================================
DROP TABLE IF EXISTS `acf_operation_log`;
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

-- ============================================
-- 8. 用户表
-- ============================================
DROP TABLE IF EXISTS `sys_user`;
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

-- ============================================
-- 9. 角色表
-- ============================================
DROP TABLE IF EXISTS `sys_role`;
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

-- ============================================
-- 10. 菜单表
-- ============================================
DROP TABLE IF EXISTS `sys_menu`;
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

-- ============================================
-- 初始化数据
-- ============================================

-- 默认预警规则
INSERT INTO `acf_alert_rule` (`rule_name`, `rule_type`, `alert_days_before_expire`, `min_stock_quantity`, `max_usage_times`, `alert_method`, `is_active`)
VALUES
('过期预警规则', 'EXPIRE', 7, NULL, NULL, 'SYSTEM', 1),
('库存预警规则', 'STOCK', NULL, 10.00, NULL, 'SYSTEM', 1),
('使用次数预警规则', 'USAGE', NULL, NULL, 3, 'SYSTEM', 1);

-- 默认标签模板
INSERT INTO `acf_label_template` (`template_name`, `template_code`, `width`, `height`, `fields_config`, `is_default`, `status`)
VALUES
('ACF标准标签模板', 'ACF_STANDARD', 5.00, 10.00,
'{"fields":["lot_number","model_spec","shelf_life","usage1","usage2","usage3"],"layout":"standard"}',
1, 1);

-- 默认LOT号规则
INSERT INTO `acf_lot_rule` (`rule_name`, `rule_code`, `lot_prefix`, `lot_format`, `qr_code_format`, `is_active`)
VALUES
('标准LOT号规则', 'STANDARD', 'LOT', 'LOT{YYYYMMDD}{SEQ:4}', '{lot_number}', 1);

-- 默认管理员用户（密码：Admin@123）
INSERT INTO `sys_user` (`username`, `password`, `real_name`, `user_type`, `status`)
VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', 'ADMIN', 1);

-- 默认角色
INSERT INTO `sys_role` (`role_code`, `role_name`, `role_desc`, `status`)
VALUES
('ADMIN', '管理员', '系统管理员，拥有所有权限', 1),
('OPERATOR', '操作员', '仓库操作员，负责日常出入库操作', 1),
('VIEWER', '查看者', '只读用户，仅可查看数据', 1);

-- 默认菜单
INSERT INTO `sys_menu` (`parent_id`, `menu_name`, `menu_code`, `menu_type`, `menu_url`, `menu_icon`, `sort_order`)
VALUES
(0, '系统管理', 'SYSTEM', 'DIRECTORY', '', 'Setting', 1),
(1, '用户管理', 'USER_MANAGE', 'MENU', '/system/user', 'User', 1),
(1, '角色管理', 'ROLE_MANAGE', 'MENU', '/system/role', 'UserGroup', 2),
(1, '菜单管理', 'MENU_MANAGE', 'MENU', '/system/menu', 'Menu', 3),

(0, '基础数据', 'BASIC_DATA', 'DIRECTORY', '', 'DataLine', 2),
(5, '料号管理', 'MATERIAL_MANAGE', 'MENU', '/basic/material', 'Box', 1),
(5, '标签模板', 'LABEL_TEMPLATE', 'MENU', '/basic/label', 'Document', 2),
(5, 'LOT规则', 'LOT_RULE', 'MENU', '/basic/lot-rule', 'Setting', 3),
(5, '预警规则', 'ALERT_RULE', 'MENU', '/basic/alert-rule', 'AlertTriangle', 4),

(0, '来料管理', 'INBOUND', 'DIRECTORY', '', 'Upload', 3),
(9, '来料登记', 'INBOUND_REGISTER', 'MENU', '/inbound/register', 'Plus', 1),
(9, '标签打印', 'LABEL_PRINT', 'MENU', '/inbound/print', 'Printer', 2),

(0, '库存管理', 'INVENTORY', 'DIRECTORY', '', 'Inventory', 4),
(12, '入库管理', 'INBOUND_MANAGE', 'MENU', '/inventory/inbound', 'Inbox', 1),
(12, '发料管理', 'OUTBOUND_MANAGE', 'MENU', '/inventory/outbound', 'Outbox', 2),
(12, '退库管理', 'RETURN_MANAGE', 'MENU', '/inventory/return', 'Refresh', 3),
(12, '报废管理', 'SCRAPPED_MANAGE', 'MENU', '/inventory/scrapped', 'Delete', 4),
(12, '库存查询', 'INVENTORY_QUERY', 'MENU', '/inventory/query', 'Search', 5),

(0, '监控预警', 'MONITOR', 'DIRECTORY', '', 'Monitor', 5),
(17, '库存监控', 'STOCK_MONITOR', 'MENU', '/monitor/stock', 'Trend', 1),
(17, '预警管理', 'ALERT_MANAGE', 'MENU', '/monitor/alert', 'Alert', 2),

(0, '报表统计', 'REPORT', 'DIRECTORY', '', 'Chart', 6),
(20, '库存台账', 'INVENTORY_LEDGER', 'MENU', '/report/ledger', 'List', 1),
(20, '出入库明细', 'TRANSACTION_DETAIL', 'MENU', '/report/transaction', 'Document', 2),
(20, '预警清单', 'ALERT_LIST', 'MENU', '/report/alert', 'AlertTriangle', 3),

(0, '可视化看板', 'DASHBOARD', 'DIRECTORY', '', 'Dashboard', 7),
(24, '大屏看板', 'BIG_SCREEN', 'MENU', '/dashboard/big-screen', 'Monitor', 1),
(24, '业务看板', 'BUSINESS_SCREEN', 'MENU', '/dashboard/business', 'Data', 2);

-- 示例料号数据
INSERT INTO `acf_material` (`material_code`, `material_name`, `material_desc`, `unit`, `barcode_rule`, `manufacturer`, `model`, `shelf_life_months`, `max_usage_times`, `status`, `remark`)
VALUES
('ACF-CP9731SA-25', 'ACF胶料 CP9731SA 25um', '索尼ACF胶料，厚度25um', '卷', '^ACF-CP\\d{4}SA-\\d{2}$', 'Sony', 'CP9731SA', 6, 3, 1, '高频型号'),
('ACF-CP9731SA-30', 'ACF胶料 CP9731SA 30um', '索尼ACF胶料，厚度30um', '卷', '^ACF-CP\\d{4}SA-\\d{2}$', 'Sony', 'CP9731SA', 6, 3, 1, '标准型号'),
('ACF-CP9731SA-35', 'ACF胶料 CP9731SA 35um', '索尼ACF胶料，厚度35um', '卷', '^ACF-CP\\d{4}SA-\\d{2}$', 'Sony', 'CP9731SA', 6, 3, 1, '厚膜型号'),
('ACF-DE6891SA-25', 'ACF胶料 DE6891SA 25um', '索尼ACF胶料，厚度25um', '卷', '^ACF-DE\\d{4}SA-\\d{2}$', 'Sony', 'DE6891SA', 6, 3, 1, '经济型号'),
('ACF-DE6891SA-30', 'ACF胶料 DE6891SA 30um', '索尼ACF胶料，厚度30um', '卷', '^ACF-DE\\d{4}SA-\\d{2}$', 'Sony', 'DE6891SA', 6, 3, 1, '标准型号');

-- ============================================
-- 创建视图（可选）
-- ============================================

-- 库存汇总视图
CREATE OR REPLACE VIEW `v_inventory_summary` AS
SELECT
    m.material_code,
    m.material_name,
    m.manufacturer,
    m.model,
    COUNT(l.id) AS lot_count,
    SUM(l.current_quantity) AS total_quantity,
    MIN(l.inbound_date) AS earliest_inbound,
    MIN(l.expire_date) AS nearest_expire,
    SUM(CASE WHEN l.usage_times >= m.max_usage_times THEN 1 ELSE 0 END) AS over_usage_count
FROM `acf_material` m
LEFT JOIN `acf_lot` l ON m.material_code = l.material_code AND l.deleted = 0 AND l.lot_status = 'IN_STOCK'
WHERE m.deleted = 0
GROUP BY m.material_code, m.material_name, m.manufacturer, m.model;

-- 预警视图
CREATE OR REPLACE VIEW `v_inventory_alert` AS
SELECT
    l.id,
    l.lot_number,
    l.material_code,
    m.material_name,
    l.current_quantity,
    l.usage_times,
    m.max_usage_times,
    l.inbound_date,
    l.expire_date,
    DATEDIFF(l.expire_date, NOW()) AS days_until_expire,
    CASE
        WHEN DATEDIFF(l.expire_date, NOW()) < 0 THEN 'EXPIRED'
        WHEN DATEDIFF(l.expire_date, NOW()) <= (SELECT alert_days_before_expire FROM acf_alert_rule WHERE rule_type = 'EXPIRE' AND is_active = 1 LIMIT 1) THEN 'WARNING'
        ELSE 'NORMAL'
    END AS expire_status,
    CASE
        WHEN l.usage_times >= m.max_usage_times THEN 'OVER_USAGE'
        WHEN l.usage_times >= m.max_usage_times - 1 THEN 'WARNING'
        ELSE 'NORMAL'
    END AS usage_status,
    l.warehouse_location
FROM `acf_lot` l
LEFT JOIN `acf_material` m ON l.material_code = m.material_code
WHERE l.deleted = 0 AND l.lot_status = 'IN_STOCK';

-- ============================================
-- 存储过程：生成交易单号
-- ============================================
DELIMITER //
DROP PROCEDURE IF EXISTS `sp_generate_transaction_no`//
CREATE PROCEDURE `sp_generate_transaction_no`(
    IN p_type VARCHAR(20),
    OUT p_transaction_no VARCHAR(50)
)
BEGIN
    DECLARE v_date_str VARCHAR(8);
    DECLARE v_seq INT;
    SET v_date_str = DATE_FORMAT(NOW(), '%Y%m%d');

    SELECT IFNULL(MAX(CAST(SUBSTRING(transaction_no, 14) AS UNSIGNED)), 0) + 1
    INTO v_seq
    FROM acf_transaction
    WHERE transaction_type = p_type
    AND transaction_no LIKE CONCAT(p_type, '_', v_date_str, '%');

    SET p_transaction_no = CONCAT(p_type, '_', v_date_str, LPAD(v_seq, 4, '0'));
END //
DELIMITER ;

-- ============================================
-- 存储过程：生成LOT号
-- ============================================
DELIMITER //
DROP PROCEDURE IF EXISTS `sp_generate_lot_number`//
CREATE PROCEDURE `sp_generate_lot_number`(
    IN p_material_code VARCHAR(50),
    OUT p_lot_number VARCHAR(100)
)
BEGIN
    DECLARE v_date_str VARCHAR(8);
    DECLARE v_seq INT;
    DECLARE v_lot_prefix VARCHAR(20);

    SET v_date_str = DATE_FORMAT(NOW(), '%Y%m%d');
    SELECT lot_prefix INTO v_lot_prefix FROM acf_lot_rule WHERE is_active = 1 LIMIT 1;

    IF v_lot_prefix IS NULL THEN
        SET v_lot_prefix = 'LOT';
    END IF;

    SELECT IFNULL(MAX(CAST(SUBSTRING(lot_number, 13) AS UNSIGNED)), 0) + 1
    INTO v_seq
    FROM acf_lot
    WHERE lot_number LIKE CONCAT(v_lot_prefix, v_date_str, '%');

    SET p_lot_number = CONCAT(v_lot_prefix, v_date_str, LPAD(v_seq, 4, '0'));
END //
DELIMITER ;

-- ============================================
-- 完成
-- ============================================
SELECT 'Database initialization completed successfully!' AS message;
