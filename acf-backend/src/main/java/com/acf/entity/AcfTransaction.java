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
     * 备注
     */
    private String remark;

    /**
     * 交易时间
     */
    private LocalDateTime transactionTime;

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
