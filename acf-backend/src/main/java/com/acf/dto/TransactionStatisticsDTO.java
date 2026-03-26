package com.acf.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 交易统计DTO
 *
 * @author ACF Team
 */
@Data
public class TransactionStatisticsDTO {

    /**
     * 总交易数
     */
    private long totalTransactions;

    /**
     * 总数量
     */
    private int totalQuantity;

    /**
     * 类型数量映射
     */
    private Map<String, Long> typeCountMap;

    /**
     * 类型数量映射
     */
    private Map<Integer, Integer> typeQuantityMap;

    /**
     * 失败交易数
     */
    private long failedTransactions;

    /**
     * 入库数量
     */
    private BigDecimal inboundQuantity;

    /**
     * 出库数量
     */
    private BigDecimal outboundQuantity;

    /**
     * 退库数量
     */
    private BigDecimal returnQuantity;

    /**
     * 报废数量
     */
    private BigDecimal scrapQuantity;
}
