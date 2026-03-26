package com.acf.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 出入库记录实体类
 *
 * @author ACF Team
 */
@Data
@TableName("acf_transaction")
public class AcfTransaction implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 交易单号
     */
    private String transactionNo;

    /**
     * LOT号
     */
    private String lotNumber;

    /**
     * 交易类型（INBOUND-入库, OUTBOUND-发料, RETURN-退库, SCRAPPED-报废）
     */
    private String transactionType;

    /**
     * 数量
     */
    private BigDecimal quantity;

    /**
     * 料号
     */
    private String materialCode;

    /**
     * 操作人
     */
    private String operator;

    /**
     * 交易结果（SUCCESS-成功, FAILED-失败）
     */
    private String transactionResult;

    /**
     * 变更前数量
     */
    private BigDecimal beforeQuantity;

    /**
     * 变更后数量
     */
    private BigDecimal afterQuantity;

    /**
     * 储位
     */
    private String warehouseLocation;

    /**
     * 操作人ID
     */
    private Long operatorId;

    /**
     * 操作人姓名
     */
    private String operatorName;

    /**
     * 回温开始时间
     */
    private LocalDateTime warmupStartTime;

    /**
     * 回温结束时间
     */
    private LocalDateTime warmupEndTime;

    /**
     * 回温时长（分钟）
     */
    private Integer warmupDuration;

    /**
     * 交易时间
     */
    private LocalDateTime transactionTime;

    /**
     * 回温时长(分钟,别名)
     */
    public Integer getWarmupDurationMinutes() {
        return warmupDuration;
    }

    public void setWarmupDurationMinutes(Integer warmupDurationMinutes) {
        this.warmupDuration = warmupDurationMinutes;
    }

    /**
     * 交易单号(别名)
     */
    public String getTransactionNumber() {
        return transactionNo;
    }

    public void setTransactionNumber(String transactionNumber) {
        this.transactionNo = transactionNumber;
    }

    /**
     * 交易结果(别名)
     */
    public String getResult() {
        return transactionResult;
    }

    public void setResult(String result) {
        this.transactionResult = result;
    }

    /**
     * 操作人(别名)
     */
    public String getOperator() {
        return operatorName;
    }

    public void setOperator(String operator) {
        this.operatorName = operator;
    }

    /**
     * 手动添加getter/setter以确保编译通过
     */
    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public LocalDateTime getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(LocalDateTime transactionTime) {
        this.transactionTime = transactionTime;
    }
}
