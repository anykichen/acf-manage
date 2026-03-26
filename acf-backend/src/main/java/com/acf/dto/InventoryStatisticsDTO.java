package com.acf.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 库存统计DTO
 *
 * @author ACF Team
 */
@Data
public class InventoryStatisticsDTO {

    /**
     * 总数量
     */
    private BigDecimal totalQuantity;

    /**
     * 过期数量
     */
    private BigDecimal expiredQuantity;

    /**
     * 预警数量
     */
    private BigDecimal alertQuantity;

    /**
     * 在库数量
     */
    private BigDecimal inStockQuantity;

    /**
     * 使用中数量
     */
    private BigDecimal inUseQuantity;
}
