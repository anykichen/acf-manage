package com.acf.service;

import com.acf.dto.InventoryStatisticsDTO;
import com.acf.dto.TransactionStatisticsDTO;

import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 报表服务接口
 */
public interface ReportService {

    /**
     * 生成库存统计报表
     * @return 库存统计数据
     */
    InventoryStatisticsDTO generateInventoryReport();

    /**
     * 生成交易统计报表
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 交易统计数据
     */
    TransactionStatisticsDTO generateTransactionReport(String startDate, String endDate);

    /**
     * 导出库存报表到Excel
     * @param response HTTP响应
     */
    void exportInventoryReportToExcel(HttpServletResponse response);

    /**
     * 导出交易报表到Excel
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param response HTTP响应
     */
    void exportTransactionReportToExcel(String startDate, String endDate, HttpServletResponse response);

    /**
     * 生成预警报表
     * @return 预警统计数据
     */
    Map<String, Object> generateAlertReport();
}
