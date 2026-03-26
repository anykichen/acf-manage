package com.acf.service;

import com.acf.entity.AcfLot;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.List;

/**
 * LOT号Service
 *
 * @author ACF Team
 */
public interface AcfLotService extends IService<AcfLot> {

    /**
     * 根据LOT号查询
     */
    AcfLot getByLotNumber(String lotNumber);

    /**
     * 生成LOT号
     */
    String generateLotNumber(String materialCode);

    /**
     * FIFO推荐LOT号
     */
    List<AcfLot> getFifoRecommendations(String materialCode, int limit);

    /**
     * 发料（出库）
     */
    void outbound(String lotNumber, BigDecimal quantity, String warehouseLocation, Long operatorId, String operatorName);

    /**
     * 退库
     */
    void returnStock(String lotNumber, BigDecimal quantity, String warehouseLocation, Long operatorId, String operatorName);

    /**
     * 报废
     */
    void scrap(String lotNumber, String remark, Long operatorId, String operatorName);

    /**
     * 获取库存预警LOT
     */
    List<AcfLot> getAlertLots();
}
