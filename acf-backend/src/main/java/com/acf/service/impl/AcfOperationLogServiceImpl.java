package com.acf.service.impl;

import com.acf.entity.AcfOperationLog;
import com.acf.mapper.AcfOperationLogMapper;
import com.acf.service.AcfOperationLogService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 操作日志Service实现类
 */
@Service
public class AcfOperationLogServiceImpl extends ServiceImpl<AcfOperationLogMapper, AcfOperationLog>
        implements AcfOperationLogService {

    @Override
    public void logOperation(String operationType, String lotNumber, String materialCode,
                             String operator, String operationDetail, String operationResult, String ipAddress) {
        AcfOperationLog log = new AcfOperationLog();
        log.setOperationType(operationType);
        log.setLotNumber(lotNumber);
        log.setMaterialCode(materialCode);
        log.setOperator(operator);
        log.setOperationDetail(operationDetail);
        log.setOperationResult(operationResult);
        log.setIpAddress(ipAddress);
        log.setOperationTime(java.time.LocalDateTime.now());

        this.save(log);
    }

    @Override
    public void logOperation(String operationType, String lotNumber, String materialCode,
                             String operationDetail, String operationResult, String ipAddress) {
        String operator = "system";
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                Object user = request.getAttribute("currentUser");
                if (user != null) {
                    operator = user.toString();
                }
            }
        } catch (Exception e) {
            // 忽略异常，使用默认值
        }

        logOperation(operationType, lotNumber, materialCode, operator, operationDetail, operationResult, ipAddress);
    }

    @Override
    public IPage<AcfOperationLog> queryLogPage(int pageNum, int pageSize, String lotNumber,
                                               String operationType, java.time.LocalDateTime startTime,
                                               java.time.LocalDateTime endTime) {
        Page<AcfOperationLog> page = new Page<>(pageNum, pageSize);
        List<AcfOperationLog> records = baseMapper.selectLogPage(lotNumber, operationType, startTime, endTime);

        int total = records.size();
        int fromIndex = (pageNum - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);

        List<AcfOperationLog> pageRecords = fromIndex < total ? records.subList(fromIndex, toIndex) : List.of();

        page.setRecords(pageRecords);
        page.setTotal(total);
        return page;
    }

    @Override
    public List<AcfOperationLog> queryRecentLogs(int limit) {
        return baseMapper.selectRecentLogs(limit);
    }

    @Override
    public List<AcfOperationLog> queryLogsByLotNumber(String lotNumber) {
        return baseMapper.selectLogPage(lotNumber, null, null, null);
    }

    @Override
    public List<Map<String, Object>> countByOperationType(java.time.LocalDateTime startTime,
                                                           java.time.LocalDateTime endTime) {
        return baseMapper.countByOperationType(startTime, endTime);
    }
}
