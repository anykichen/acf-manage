-- LOT号规则表
-- 补充到 init_database.sql

DROP TABLE IF EXISTS `acf_lot_rule`;
CREATE TABLE `acf_lot_rule` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `rule_name` VARCHAR(100) NOT NULL COMMENT '规则名称',
  `rule_description` VARCHAR(500) COMMENT '规则描述',
  `qr_code_format` VARCHAR(500) COMMENT '二维码格式（支持变量：{lotNumber}、{materialCode}、{year}、{month}、{day}、{seq}）',
  `text_description_format` VARCHAR(500) COMMENT '文本描述格式',
  `sequence_start` INT NOT NULL DEFAULT 1 COMMENT '序列号起始值',
  `sequence_length` INT NOT NULL DEFAULT 6 COMMENT '序列号长度（补零位数）',
  `is_default` TINYINT NOT NULL DEFAULT 0 COMMENT '是否默认规则（1-是, 0-否）',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态（1-启用, 0-停用）',
  `created_by` VARCHAR(50) COMMENT '创建人',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_by` VARCHAR(50) COMMENT '更新人',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记（0-正常, 1-删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_rule_name` (`rule_name`),
  KEY `idx_is_default` (`is_default`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='LOT号规则表';

-- 插入默认规则
INSERT INTO `acf_lot_rule` (`rule_name`, `rule_description`, `qr_code_format`, `text_description_format`, `sequence_start`, `sequence_length`, `is_default`, `status`, `created_by`)
VALUES
('默认规则', '系统默认的LOT号生成规则', '{lotNumber}', '{materialCode}-{date}-{seq}', 1, 6, 1, 1, 'system');
