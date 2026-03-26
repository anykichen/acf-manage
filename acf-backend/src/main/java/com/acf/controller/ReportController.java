package com.acf.controller;

import com.acf.common.Result;
import com.acf.dto.InventoryStatisticsDTO;
import com.acf.dto.TransactionStatisticsDTO;
import com.acf.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 报表管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/report")
@Tag(name = "报表管理", description = "报表管理相关接口")
public class ReportController {

    @Autowired
    private ReportService reportService;

    /**
     * 获取库存统计报表
     */
    @Operation(summary = "获取库存统计报表")
    @GetMapping("/inventory")
    public Result<InventoryStatisticsDTO> getInventoryReport() {
        log.info("获取库存统计报表");
        InventoryStatisticsDTO dto = reportService.generateInventoryReport();
        return Result.success(dto);
    }

    /**
     * 获取交易统计报表
     */
    @Operation(summary = "获取交易统计报表")
    @GetMapping("/transaction")
    public Result<TransactionStatisticsDTO> getTransactionReport(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        log.info("获取交易统计报表: {} - {}", startDate, endDate);
        TransactionStatisticsDTO dto = reportService.generateTransactionReport(startDate, endDate);
        return Result.success(dto);
    }

    /**
     * 导出库存报表
     */
    @Operation(summary = "导出库存报表")
    @GetMapping("/inventory/export")
    public void exportInventoryReport(HttpServletResponse response) {
        log.info("导出库存报表");
        reportService.exportInventoryReportToExcel(response);
    }

    /**
     * 导出交易报表
     */
    @Operation(summary = "导出交易报表")
    @GetMapping("/transaction/export")
    public void exportTransactionReport(
            @RequestParam String startDate,
            @RequestParam String endDate,
            HttpServletResponse response) {
        log.info("导出交易报表: {} - {}", startDate, endDate);
        reportService.exportTransactionReportToExcel(startDate, endDate, response);
    }

    /**
     * 获取预警报表
     */
    @Operation(summary = "获取预警报表")
    @GetMapping("/alert")
    public Result<Map<String, Object>> getAlertReport() {
        log.info("获取预警报表");
        Map<String, Object> report = reportService.generateAlertReport();
        return Result.success(report);
    }
}
