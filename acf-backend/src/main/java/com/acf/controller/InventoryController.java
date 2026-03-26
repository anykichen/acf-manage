package com.acf.controller;

import com.acf.common.Result;
import com.acf.common.ResultCode;
import com.acf.service.InventoryService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 库存管理Controller
 */
@Tag(name = "库存管理", description = "入库、发料、退库、报废相关接口")
@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @Operation(summary = "入库（绑定储位）")
    @PostMapping("/inbound")
    public Result<Void> inbound(
            @Parameter(description = "LOT号") @RequestParam String lotNumber,
            @Parameter(description = "储位") @RequestParam String warehouseLocation,
            @Parameter(description = "操作人") @RequestParam(required = false, defaultValue = "system") String operator) {

        try {
            inventoryService.inbound(lotNumber, warehouseLocation, operator);
            return Result.success();
        } catch (Exception e) {
            return Result.error(ResultCode.FAILED, e.getMessage());
        }
    }

    @Operation(summary = "发料")
    @PostMapping("/outbound")
    public Result<Void> outbound(
            @Parameter(description = "LOT号") @RequestParam String lotNumber,
            @Parameter(description = "数量") @RequestParam BigDecimal quantity,
            @Parameter(description = "发料位置") @RequestParam String warehouseLocation,
            @Parameter(description = "操作人") @RequestParam(required = false, defaultValue = "system") String operator) {

        try {
            inventoryService.outbound(lotNumber, quantity, warehouseLocation, operator);
            return Result.success();
        } catch (Exception e) {
            return Result.error(ResultCode.FAILED, e.getMessage());
        }
    }

    @Operation(summary = "退库")
    @PostMapping("/return")
    public Result<Void> returnStock(
            @Parameter(description = "LOT号") @RequestParam String lotNumber,
            @Parameter(description = "数量") @RequestParam BigDecimal quantity,
            @Parameter(description = "退库储位") @RequestParam String warehouseLocation,
            @Parameter(description = "操作人") @RequestParam(required = false, defaultValue = "system") String operator) {

        try {
            inventoryService.returnStock(lotNumber, quantity, warehouseLocation, operator);
            return Result.success();
        } catch (Exception e) {
            return Result.error(ResultCode.FAILED, e.getMessage());
        }
    }

    @Operation(summary = "报废")
    @PostMapping("/scrap")
    public Result<Void> scrap(
            @Parameter(description = "LOT号") @RequestParam String lotNumber,
            @Parameter(description = "报废原因") @RequestParam String remark,
            @Parameter(description = "操作人") @RequestParam(required = false, defaultValue = "system") String operator) {

        try {
            inventoryService.scrap(lotNumber, remark, operator);
            return Result.success();
        } catch (Exception e) {
            return Result.error(ResultCode.FAILED, e.getMessage());
        }
    }

    @Operation(summary = "FIFO推荐")
    @GetMapping("/fifo-recommend")
    public Result<List<Map<String, Object>>> fifoRecommend(
            @Parameter(description = "料号") @RequestParam String materialCode,
            @Parameter(description = "推荐数量") @RequestParam(defaultValue = "5") int limit) {

        try {
            List<Map<String, Object>> result = inventoryService.fifoRecommend(materialCode, limit);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(ResultCode.FAILED, e.getMessage());
        }
    }

    @Operation(summary = "查询库存")
    @GetMapping("/page")
    public Result<Page<Map<String, Object>>> queryInventory(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "料号") @RequestParam(required = false) String materialCode,
            @Parameter(description = "LOT号") @RequestParam(required = false) String lotNumber,
            @Parameter(description = "储位") @RequestParam(required = false) String warehouseLocation,
            @Parameter(description = "状态") @RequestParam(required = false) String status,
            @Parameter(description = "开始时间") @RequestParam(required = false)
                @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false)
                @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {

        Page<Map<String, Object>> page = inventoryService.queryInventory(pageNum, pageSize,
                materialCode, lotNumber, warehouseLocation, status, startTime, endTime);
        return Result.success(page);
    }

    @Operation(summary = "查询库存统计")
    @GetMapping("/statistics")
    public Result<Map<String, Object>> queryInventoryStatistics() {
        Map<String, Object> statistics = inventoryService.queryInventoryStatistics();
        return Result.success(statistics);
    }
}
