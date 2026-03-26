package com.acf.controller;

import com.acf.common.Result;
import com.acf.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 看板数据控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "看板管理", description = "看板数据相关接口")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    /**
     * 获取大屏看板数据
     */
    @Operation(summary = "获取大屏看板数据")
    @GetMapping("/data")
    public Result<Map<String, Object>> getDashboardData() {
        log.info("获取大屏看板数据");
        Map<String, Object> data = dashboardService.getDashboardData();
        return Result.success(data);
    }

    /**
     * 获取业务状态看板数据
     */
    @Operation(summary = "获取业务状态看板数据")
    @GetMapping("/business-status")
    public Result<Map<String, Object>> getBusinessStatusData() {
        log.info("获取业务状态看板数据");
        Map<String, Object> data = dashboardService.getBusinessStatusData();
        return Result.success(data);
    }

    /**
     * 获取库存分布数据（饼图）
     */
    @Operation(summary = "获取库存分布数据")
    @GetMapping("/inventory-distribution")
    public Result<Map<String, Object>> getInventoryDistribution() {
        log.info("获取库存分布数据");
        Map<String, Object> data = dashboardService.getInventoryDistribution();
        return Result.success(data);
    }

    /**
     * 获取交易趋势数据（折线图）
     */
    @Operation(summary = "获取交易趋势数据")
    @GetMapping("/transaction-trend")
    public Result<Map<String, Object>> getTransactionTrend(@RequestParam(defaultValue = "7") int days) {
        log.info("获取交易趋势数据，天数：{}", days);
        Map<String, Object> data = dashboardService.getTransactionTrend(days);
        return Result.success(data);
    }

    /**
     * 获取预警统计数据（柱状图）
     */
    @Operation(summary = "获取预警统计数据")
    @GetMapping("/alert-statistics")
    public Result<Map<String, Object>> getAlertStatistics() {
        log.info("获取预警统计数据");
        Map<String, Object> data = dashboardService.getAlertStatistics();
        return Result.success(data);
    }

    /**
     * 获取料号使用量对比数据
     */
    @Operation(summary = "获取料号使用量对比数据")
    @GetMapping("/material-usage")
    public Result<Map<String, Object>> getMaterialUsageComparison() {
        log.info("获取料号使用量对比数据");
        Map<String, Object> data = dashboardService.getMaterialUsageComparison();
        return Result.success(data);
    }
}
