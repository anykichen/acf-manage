package com.acf.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 库存统计DTO
 *
 * @author ACF Team
 */
@Data
public class InventoryStatisticsDTO {

    /**
     * 总批次数量
     */
    private long totalBatchCount;

    /**
     * 总数量
     */
    private BigDecimal totalQuantity;

    /**
     * 过期批次数量
     */
    private long expiredBatchCount;

    /**
     * 过期数量
     */
    private BigDecimal expiredQuantity;

    /**
     * 预警批次数量
     */
    private long warningBatchCount;

    /**
     * 预警数量
     */
    private BigDecimal alertQuantity;

    /**
     * 今日入库数量
     */
    private long todayInboundCount;

    /**
     * 料号数量映射
     */
    private Map<String, Integer> materialQuantityMap;

    /**
     * 在库数量
     */
    private BigDecimal inStockQuantity;

    /**
     * 使用中数量
     */
    private BigDecimal inUseQuantity;
}
