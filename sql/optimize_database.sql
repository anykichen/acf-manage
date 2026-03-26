-- 数据库优化脚本
-- 添加索引以提高查询性能

-- acf_lot 表索引
CREATE INDEX idx_lot_material ON acf_lot(material_code);
CREATE INDEX idx_lot_status ON acf_lot(status);
CREATE INDEX idx_lot_expire ON acf_lot(expire_time);
CREATE INDEX idx_lot_inbound ON acf_lot(inbound_time);
CREATE INDEX idx_lot_storage ON acf_lot(storage_location);

-- acf_transaction 表索引
CREATE INDEX idx_trans_lot ON acf_transaction(lot_number);
CREATE INDEX idx_trans_type ON acf_transaction(transaction_type);
CREATE INDEX idx_trans_time ON acf_transaction(transaction_time);
CREATE INDEX idx_trans_operator ON acf_transaction(operator);

-- acf_alert 表索引
CREATE INDEX idx_alert_type ON acf_alert(alert_type);
CREATE INDEX idx_alert_level ON acf_alert(alert_level);
CREATE INDEX idx_alert_status ON acf_alert(status);
CREATE INDEX idx_alert_time ON acf_alert(alert_time);

-- acf_operation_log 表索引
CREATE INDEX idx_op_lot ON acf_operation_log(lot_number);
CREATE INDEX idx_op_type ON acf_operation_log(operation_type);
CREATE INDEX idx_op_time ON acf_operation_log(operation_time);
CREATE INDEX idx_op_user ON acf_operation_log(user_id);

-- acf_material 表索引
CREATE INDEX idx_mat_code ON acf_material(material_code);
CREATE INDEX idx_mat_status ON acf_material(status);

-- acf_lot_rule 表索引
CREATE INDEX idx_rule_status ON acf_lot_rule(status);
CREATE INDEX idx_rule_default ON acf_lot_rule(is_default);

-- acf_label_template 表索引
CREATE INDEX idx_template_status ON acf_label_template(status);

-- acf_alert_rule 表索引
CREATE INDEX idx_alert_rule_active ON acf_alert_rule(is_active);

-- 查看索引
SHOW INDEX FROM acf_lot;
SHOW INDEX FROM acf_transaction;
SHOW INDEX FROM acf_alert;
SHOW INDEX FROM acf_operation_log;
SHOW INDEX FROM acf_material;
SHOW INDEX FROM acf_lot_rule;
SHOW INDEX FROM acf_label_template;
SHOW INDEX FROM acf_alert_rule;
