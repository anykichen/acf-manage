package com.acf.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 库存管理Service接口
 */
public interface InventoryService {

    /**
     * 入库（绑定储位）
     * @param lotNumber LOT号
     * @param warehouseLocation 储位
     * @param operator 操作人
     */
    void inbound(String lotNumber, String warehouseLocation, String operator);

    /**
     * 发料（FIFO推荐 + 扣减库存）
     * @param lotNumber LOT号
     * @param quantity 数量
     * @param warehouseLocation 发料位置
     * @param operator 操作人
     */
    void outbound(String lotNumber, BigDecimal quantity, String warehouseLocation, String operator);

    /**
     * 退库（恢复库存 + 计算回温时间）
     * @param lotNumber LOT号
     * @param quantity 数量
     * @param warehouseLocation 退库储位
     * @param operator 操作人
     */
    void returnStock(String lotNumber, BigDecimal quantity, String warehouseLocation, String operator);

    /**
     * 报废
     * @param lotNumber LOT号
     * @param remark 报废原因
     * @param operator 操作人
     */
    void scrap(String lotNumber, String remark, String operator);

    /**
     * FIFO推荐
     * @param materialCode 料号
     * @param limit 推荐数量
     * @return 推荐列表
     */
    java.util.List<java.util.Map<String, Object>> fifoRecommend(String materialCode, int limit);

    /**
     * 查询库存（多维度）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param materialCode 料号
     * @param lotNumber LOT号
     * @param warehouseLocation 储位
     * @param status 状态
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 分页结果
     */
    Page<java.util.Map<String, Object>> queryInventory(int pageNum, int pageSize, String materialCode,
                                                         String lotNumber, String warehouseLocation, String status,
                                                         LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 查询库存统计
     * @return 统计数据
     */
    Map<String, Object> queryInventoryStatistics();
}
