package com.acf.controller;

import com.acf.common.Result;
import com.acf.common.ResultCode;
import com.acf.entity.AcfOperationLog;
import com.acf.service.AcfOperationLogService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 操作日志Controller
 */
@Tag(name = "操作日志管理", description = "操作日志相关接口")
@RestController
@RequestMapping("/api/operation-log")
public class AcfOperationLogController {

    @Autowired
    private AcfOperationLogService operationLogService;

    @Operation(summary = "分页查询操作日志")
    @GetMapping("/page")
    public Result<IPage<AcfOperationLog>> queryLogPage(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "LOT号") @RequestParam(required = false) String lotNumber,
            @Parameter(description = "操作类型") @RequestParam(required = false) String operationType,
            @Parameter(description = "开始时间") @RequestParam(required = false)
                @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false)
                @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {

        IPage<AcfOperationLog> page = operationLogService.queryLogPage(pageNum, pageSize, lotNumber,
                operationType, startTime, endTime);
        return Result.success(page);
    }

    @Operation(summary = "查询最近的操作日志")
    @GetMapping("/recent")
    public Result<List<AcfOperationLog>> queryRecentLogs(
            @Parameter(description = "查询条数") @RequestParam(defaultValue = "10") int limit) {
        List<AcfOperationLog> logs = operationLogService.queryRecentLogs(limit);
        return Result.success(logs);
    }

    @Operation(summary = "根据LOT号查询操作日志")
    @GetMapping("/lot/{lotNumber}")
    public Result<List<AcfOperationLog>> queryLogsByLotNumber(
            @Parameter(description = "LOT号") @PathVariable String lotNumber) {
        List<AcfOperationLog> logs = operationLogService.queryLogsByLotNumber(lotNumber);
        return Result.success(logs);
    }

    @Operation(summary = "统计操作日志数量")
    @GetMapping("/count")
    public Result<List<Map<String, Object>>> countByOperationType(
            @Parameter(description = "开始时间") @RequestParam(required = false)
                @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @Parameter(description = "结束时间") @RequestParam(required = false)
                @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {

        List<Map<String, Object>> counts = operationLogService.countByOperationType(startTime, endTime);
        return Result.success(counts);
    }

    @Operation(summary = "根据ID查询日志详情")
    @GetMapping("/{id}")
    public Result<AcfOperationLog> queryLogById(
            @Parameter(description = "日志ID") @PathVariable Long id) {
        AcfOperationLog log = operationLogService.getById(id);
        if (log == null) {
            return Result.error(ResultCode.NOT_FOUND, "操作日志不存在");
        }
        return Result.success(log);
    }

    @Operation(summary = "删除操作日志")
    @DeleteMapping("/{id}")
    public Result<Void> deleteLog(@Parameter(description = "日志ID") @PathVariable Long id) {
        boolean success = operationLogService.removeById(id);
        if (success) {
            return Result.success();
        } else {
            return Result.error(ResultCode.FAILED, "删除失败");
        }
    }

    @Operation(summary = "批量删除操作日志")
    @DeleteMapping("/batch")
    public Result<Void> batchDeleteLogs(@Parameter(description = "日志ID列表") @RequestBody List<Long> ids) {
        boolean success = operationLogService.removeByIds(ids);
        if (success) {
            return Result.success();
        } else {
            return Result.error(ResultCode.FAILED, "批量删除失败");
        }
    }
}
