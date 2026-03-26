package com.acf.controller;

import com.acf.common.Result;
import com.acf.dto.AcfAlertDTO;
import com.acf.service.AlertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 预警管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/alert")
@Tag(name = "预警管理", description = "预警管理相关接口")
public class AlertController {

    @Autowired
    private AlertService alertService;

    /**
     * 手动检查过期预警
     */
    @Operation(summary = "检查过期预警")
    @PostMapping("/check-expire")
    public Result<List<AcfAlertDTO>> checkExpireAlert() {
        log.info("手动检查过期预警");
        List<AcfAlertDTO> alerts = alertService.checkExpireAlert();
        return Result.success(alerts);
    }

    /**
     * 手动检查使用次数预警
     */
    @Operation(summary = "检查使用次数预警")
    @PostMapping("/check-usage")
    public Result<List<AcfAlertDTO>> checkUsageAlert() {
        log.info("手动检查使用次数预警");
        List<AcfAlertDTO> alerts = alertService.checkUsageAlert();
        return Result.success(alerts);
    }

    /**
     * 获取待处理的预警
     */
    @Operation(summary = "获取待处理的预警")
    @GetMapping("/pending")
    public Result<List<AcfAlertDTO>> getPendingAlerts() {
        log.info("获取待处理的预警");
        List<AcfAlertDTO> alerts = alertService.getPendingAlerts();
        return Result.success(alerts);
    }

    /**
     * 标记预警为已处理
     */
    @Operation(summary = "标记预警为已处理")
    @PutMapping("/{alertId}/resolve")
    public Result<Void> markAlertAsResolved(@PathVariable Long alertId) {
        log.info("标记预警为已处理: {}", alertId);
        alertService.markAlertAsResolved(alertId);
        return Result.success();
    }

    /**
     * 手动触发预警检查
     */
    @Operation(summary = "手动触发预警检查")
    @PostMapping("/trigger")
    public Result<Void> triggerAlertCheck() {
        log.info("手动触发预警检查");
        // 触发所有预警检查
        alertService.checkExpireAlert();
        alertService.checkUsageAlert();
        return Result.success();
    }
}
