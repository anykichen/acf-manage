package com.acf.service;

import java.util.Map;

/**
 * 看板数据服务接口
 */
public interface DashboardService {

    /**
     * 获取大屏看板数据
     * @return 看板数据
     */
    Map<String, Object> getDashboardData();

    /**
     * 获取业务状态看板数据
     * @return 业务状态数据
     */
    Map<String, Object> getBusinessStatusData();

    /**
     * 获取库存分布数据（用于饼图）
     * @return 库存分布数据
     */
    Map<String, Object> getInventoryDistribution();

    /**
     * 获取交易趋势数据（用于折线图）
     * @param days 查询天数
     * @return 交易趋势数据
     */
    Map<String, Object> getTransactionTrend(int days);

    /**
     * 获取预警统计数据（用于柱状图）
     * @return 预警统计数据
     */
    Map<String, Object> getAlertStatistics();

    /**
     * 获取料号使用量对比数据
     * @return 料号使用量数据
     */
    Map<String, Object> getMaterialUsageComparison();
}
