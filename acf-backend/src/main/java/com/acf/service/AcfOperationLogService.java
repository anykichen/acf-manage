package com.acf.service;

import com.acf.entity.AcfOperationLog;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 操作日志Service接口
 */
public interface AcfOperationLogService extends IService<AcfOperationLog> {

    /**
     * 记录操作日志
     */
    void logOperation(String operationType, String lotNumber, String materialCode,
                       String operator, String operationDetail, String operationResult, String ipAddress);

    /**
     * 记录操作日志（使用当前用户）
     */
    void logOperation(String operationType, String lotNumber, String materialCode,
                       String operationDetail, String operationResult, String ipAddress);

    /**
     * 分页查询操作日志
     */
    IPage<AcfOperationLog> queryLogPage(int pageNum, int pageSize, String lotNumber,
                                         String operationType, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 查询最近的操作日志
     */
    List<AcfOperationLog> queryRecentLogs(int limit);

    /**
     * 根据LOT号查询操作日志
     */
    List<AcfOperationLog> queryLogsByLotNumber(String lotNumber);

    /**
     * 统计操作日志数量
     */
    List<Map<String, Object>> countByOperationType(LocalDateTime startTime, LocalDateTime endTime);
}
