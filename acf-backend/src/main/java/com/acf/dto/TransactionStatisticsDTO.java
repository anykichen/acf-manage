package com.acf.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 交易统计DTO
 *
 * @author ACF Team
 */
@Data
public class TransactionStatisticsDTO {

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
