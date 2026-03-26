package com.acf.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 预警规则实体类
 *
 * @author ACF Team
 */
@Data
@TableName("acf_alert_rule")
public class AcfAlertRule implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 预警类型（EXPIRE-过期, USAGE-使用次数, STOCK-库存）
     */
    private String ruleType;

    /**
     * 过期预警天数
     */
    private Integer alertDaysBeforeExpire;

    /**
     * 最小库存数量
     */
    private BigDecimal minStockQuantity;

    /**
     * 最大使用次数
     */
    private Integer maxUsageTimes;

    /**
     * 预警方式（SYSTEM-系统, EMAIL-邮件, SMS-短信）
     */
    private String alertMethod;

    /**
     * 是否激活（1-是, 0-否）
     */
    private Integer isActive;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 删除标记（0-正常, 1-删除）
     */
    private Integer deleted;

    // 手动添加getter/setter以确保编译通过
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getRuleType() {
        return ruleType;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    public Integer getAlertDaysBeforeExpire() {
        return alertDaysBeforeExpire;
    }

    public void setAlertDaysBeforeExpire(Integer alertDaysBeforeExpire) {
        this.alertDaysBeforeExpire = alertDaysBeforeExpire;
    }

    public BigDecimal getMinStockQuantity() {
        return minStockQuantity;
    }

    public void setMinStockQuantity(BigDecimal minStockQuantity) {
        this.minStockQuantity = minStockQuantity;
    }

    public Integer getMaxUsageTimes() {
        return maxUsageTimes;
    }

    public void setMaxUsageTimes(Integer maxUsageTimes) {
        this.maxUsageTimes = maxUsageTimes;
    }

    public String getAlertMethod() {
        return alertMethod;
    }

    public void setAlertMethod(String alertMethod) {
        this.alertMethod = alertMethod;
    }

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }
}
