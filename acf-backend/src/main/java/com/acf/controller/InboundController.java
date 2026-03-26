package com.acf.controller;

import com.acf.common.Result;
import com.acf.common.ResultCode;
import com.acf.service.InboundService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 来料管理Controller
 */
@Tag(name = "来料管理", description = "来料扫码识别、LOT号生成、标签打印相关接口")
@RestController
@RequestMapping("/api/inbound")
public class InboundController {

    @Autowired
    private InboundService inboundService;

    @Operation(summary = "扫码识别来料")
    @PostMapping("/scan")
    public Result<Map<String, Object>> scanBarcode(
            @Parameter(description = "条码") @RequestParam String barcode) {

        Map<String, Object> result = inboundService.scanBarcode(barcode);
        if ((Boolean) result.get("success")) {
            return Result.success(result);
        } else {
            return Result.error(ResultCode.FAILED, (String) result.get("message"));
        }
    }

    @Operation(summary = "确认来料并生成LOT号")
    @PostMapping("/confirm")
    public Result<String> confirmInbound(
            @Parameter(description = "料号") @RequestParam String materialCode,
            @Parameter(description = "数量") @RequestParam Integer quantity,
            @Parameter(description = "操作人") @RequestParam(required = false, defaultValue = "system") String operator) {

        try {
            String lotNumber = inboundService.confirmInbound(materialCode, quantity, operator);
            return Result.success(lotNumber, "LOT号生成成功");
        } catch (Exception e) {
            return Result.error(ResultCode.FAILED, e.getMessage());
        }
    }

    @Operation(summary = "生成标签数据")
    @GetMapping("/label/{lotNumber}")
    public Result<Map<String, Object>> generateLabelData(
            @Parameter(description = "LOT号") @PathVariable String lotNumber) {

        Map<String, Object> result = inboundService.generateLabelData(lotNumber);
        if ((Boolean) result.get("success")) {
            return Result.success(result);
        } else {
            return Result.error(ResultCode.FAILED, (String) result.get("message"));
        }
    }

    @Operation(summary = "分页查询来料记录")
    @GetMapping("/page")
    public Result<Page<Map<String, Object>>> queryInboundPage(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "料号") @RequestParam(required = false) String materialCode,
            @Parameter(description = "开始时间") @RequestParam(required = false)
                @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false)
                @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {

        Page<Map<String, Object>> page = inboundService.queryInboundPage(pageNum, pageSize, 
                materialCode, startTime, endTime);
        return Result.success(page);
    }
}
