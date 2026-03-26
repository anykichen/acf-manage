package com.acf.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * LOT号主表实体类
 *
 * @author ACF Team
 */
@Data
@TableName("acf_lot")
public class AcfLot implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * LOT号
     */
    private String lotNumber;

    /**
     * 料号
     */
    private String materialCode;

    /**
     * 二维码
     */
    private String qrCode;

    /**
     * 入库时间
     */
    private LocalDateTime inboundDate;

    /**
     * 过期时间
     */
    private LocalDateTime expireDate;

    /**
     * 初始数量
     */
    private BigDecimal initialQuantity;

    /**
     * 当前数量
     */
    private BigDecimal currentQuantity;

    /**
     * 使用次数
     */
    private Integer usageTimes;

    /**
     * 最大使用次数
     */
    private Integer maxUsageTimes;

    /**
     * 入库时间
     */
    private LocalDateTime inStockTime;

    /**
     * 回温开始时间
     */
    private LocalDateTime warmupStartTime;

    /**
     * 回温结束时间
     */
    private LocalDateTime warmupEndTime;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 储位
     */
    private String warehouseLocation;

    /**
     * LOT状态（IN_STOCK-在库, IN_USE-使用中, SCRAPPED-报废）
     */
    private String lotStatus;

    /**
     * 状态(别名,为了兼容DashboardService)
     */
    public String getStatus() {
        return lotStatus;
    }

    public void setStatus(String status) {
        this.lotStatus = status;
    }

    /**
     * 入库时间(别名,为了兼容InboundService)
     */
    public LocalDateTime getInStockTime() {
        return inStockTime != null ? inStockTime : inboundDate;
    }

    public void setInStockTime(LocalDateTime inStockTime) {
        this.inStockTime = inStockTime;
        this.inboundDate = inStockTime;
    }

    /**
     * 过期时间(别名,为了兼容AlertService)
     */
    public LocalDateTime getExpireTime() {
        return expireDate;
    }

    public void setExpireTime(LocalDateTime expireTime) {
        this.expireDate = expireTime;
    }

    /**
     * 数量(别名,为了兼容InboundService)
     */
    public Integer getQuantity() {
        return initialQuantity != null ? initialQuantity.intValue() : null;
    }

    public void setQuantity(Integer quantity) {
        this.initialQuantity = quantity != null ? BigDecimal.valueOf(quantity) : null;
    }

    /**
     * 使用次数(别名)
     */
    public Integer getUsageCount() {
        return usageTimes;
    }

    public void setUsageCount(Integer usageCount) {
        this.usageTimes = usageCount;
    }

    /**
     * 最大使用次数(别名)
     */
    public Integer getMaxUsageCount() {
        return maxUsageTimes;
    }

    public void setMaxUsageCount(Integer maxUsageCount) {
        this.maxUsageTimes = maxUsageCount;
    }

    /**
     * 储位(别名,为了兼容DashboardService)
     */
    public String getStorageLocation() {
        return warehouseLocation;
    }

    public void setStorageLocation(String storageLocation) {
        this.warehouseLocation = storageLocation;
    }

    /**
     * 入库时间(别名,为了兼容DashboardService)
     */
    public LocalDateTime getInboundTime() {
        return inboundDate;
    }

    public void setInboundTime(LocalDateTime inboundTime) {
        this.inboundDate = inboundTime;
    }

    /**
     * 备注
     */
    private String remark;

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
}
