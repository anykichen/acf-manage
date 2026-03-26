package com.acf.service.impl;

import com.acf.common.BusinessException;
import com.acf.common.ResultCode;
import com.acf.entity.*;
import com.acf.mapper.AcfTransactionMapper;
import com.acf.service.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * 库存管理Service实现类
 */
@Service
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    private AcfLotService lotService;

    @Autowired
    private AcfMaterialService materialService;

    @Autowired
    private AcfOperationLogService operationLogService;

    @Autowired
    private AcfTransactionMapper transactionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void inbound(String lotNumber, String warehouseLocation, String operator) {
        // 查询LOT号
        AcfLot lot = lotService.queryByLotNumber(lotNumber);
        if (lot == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "LOT号不存在");
        }

        // 检查状态
        if (!"IN_STOCK".equals(lot.getStatus())) {
            throw new BusinessException(ResultCode.FAILED, "LOT号状态不允许入库");
        }

        // 绑定储位
        lot.setWarehouseLocation(warehouseLocation);
        lot.setInStockTime(LocalDateTime.now());
        lot.setUpdateTime(LocalDateTime.now());
        lotService.updateById(lot);

        // 记录操作日志
        operationLogService.logOperation("INBOUND", lotNumber, lot.getMaterialCode(),
                operator, "入库，储位: " + warehouseLocation, "成功", null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void outbound(String lotNumber, BigDecimal quantity, String warehouseLocation, String operator) {
        // 查询LOT号
        AcfLot lot = lotService.queryByLotNumber(lotNumber);
        if (lot == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "LOT号不存在");
        }

        // 检查库存
        BigDecimal currentQuantity = lot.getCurrentQuantity() != null ? lot.getCurrentQuantity() : lot.getQuantity();
        if (currentQuantity.compareTo(quantity) < 0) {
            throw new BusinessException(ResultCode.FAILED, "库存不足");
        }

        // 查询料号
        AcfMaterial material = materialService.queryByMaterialCode(lot.getMaterialCode());
        if (material == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "料号不存在");
        }

        // 检查使用次数
        int currentUsageTimes = lot.getUsageTimes() != null ? lot.getUsageTimes() : 0;
        if (currentUsageTimes >= material.getMaxUsageTimes()) {
            throw new BusinessException(ResultCode.FAILED, "使用次数已达上限（" + material.getMaxUsageTimes() + "次）");
        }

        // 扣减库存
        BigDecimal newQuantity = currentQuantity.subtract(quantity);
        lot.setCurrentQuantity(newQuantity);
        lot.setUsageTimes(currentUsageTimes + 1);
        lot.setStatus("IN_USE");
        lot.setUpdateTime(LocalDateTime.now());
        lotService.updateById(lot);

        // 记录交易
        String transactionNo = generateTransactionNo("OUTBOUND");
        AcfTransaction transaction = new AcfTransaction();
        transaction.setTransactionNo(transactionNo);
        transaction.setLotNumber(lotNumber);
        transaction.setMaterialCode(lot.getMaterialCode());
        transaction.setTransactionType("OUTBOUND");
        transaction.setQuantity(quantity);
        transaction.setWarehouseLocation(warehouseLocation);
        transaction.setOperator(operator);
        transaction.setWarmupStartTime(LocalDateTime.now());
        transaction.setTransactionTime(LocalDateTime.now());
        transaction.setCreateTime(LocalDateTime.now());

        transactionMapper.insertTransaction(transaction);

        // 记录操作日志
        operationLogService.logOperation("OUTBOUND", lotNumber, lot.getMaterialCode(),
                operator, "发料，数量: " + quantity + "，储位: " + warehouseLocation, "成功", null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void returnStock(String lotNumber, BigDecimal quantity, String warehouseLocation, String operator) {
        // 查询LOT号
        AcfLot lot = lotService.queryByLotNumber(lotNumber);
        if (lot == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "LOT号不存在");
        }

        // 查询最近一次发料记录
        AcfTransaction lastOutbound = transactionMapper.selectLastOutbound(lotNumber);
        if (lastOutbound == null) {
            throw new BusinessException(ResultCode.FAILED, "未找到发料记录");
        }

        // 计算回温时间
        LocalDateTime warmupStart = lastOutbound.getWarmupStartTime();
        LocalDateTime warmupEnd = LocalDateTime.now();
        long warmupMinutes = Duration.between(warmupStart, warmupEnd).toMinutes();

        // 恢复库存
        BigDecimal currentQuantity = lot.getCurrentQuantity() != null ? lot.getCurrentQuantity() : BigDecimal.ZERO;
        BigDecimal newQuantity = currentQuantity.add(quantity);
        lot.setCurrentQuantity(newQuantity);
        lot.setWarehouseLocation(warehouseLocation);
        lot.setStatus("IN_STOCK");
        lot.setUpdateTime(LocalDateTime.now());
        lotService.updateById(lot);

        // 记录交易
        String transactionNo = generateTransactionNo("RETURN");
        AcfTransaction transaction = new AcfTransaction();
        transaction.setTransactionNo(transactionNo);
        transaction.setLotNumber(lotNumber);
        transaction.setMaterialCode(lot.getMaterialCode());
        transaction.setTransactionType("RETURN");
        transaction.setQuantity(quantity);
        transaction.setWarehouseLocation(warehouseLocation);
        transaction.setOperator(operator);
        transaction.setWarmupStartTime(warmupStart);
        transaction.setWarmupEndTime(warmupEnd);
        transaction.setWarmupDurationMinutes((int) warmupMinutes);
        transaction.setTransactionTime(LocalDateTime.now());
        transaction.setCreateTime(LocalDateTime.now());

        transactionMapper.insertTransaction(transaction);

        // 记录操作日志
        String warmupInfo = String.format("回温时长: %d分%d秒", 
                warmupMinutes / 60, warmupMinutes % 60);
        
        operationLogService.logOperation("RETURN", lotNumber, lot.getMaterialCode(),
                operator, "退库，数量: " + quantity + "，储位: " + warehouseLocation + "，" + warmupInfo, 
                "成功", null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void scrap(String lotNumber, String remark, String operator) {
        // 查询LOT号
        AcfLot lot = lotService.queryByLotNumber(lotNumber);
        if (lot == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "LOT号不存在");
        }

        // 更新状态
        lot.setStatus("SCRAPPED");
        lot.setRemark(remark);
        lot.setUpdateTime(LocalDateTime.now());
        lotService.updateById(lot);

        // 记录操作日志
        operationLogService.logOperation("SCRAP", lotNumber, lot.getMaterialCode(),
                operator, "报废，原因: " + remark, "成功", null);
    }

    @Override
    public List<Map<String, Object>> fifoRecommend(String materialCode, int limit) {
        // 查询料号
        AcfMaterial material = materialService.queryByMaterialCode(materialCode);
        if (material == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "料号不存在");
        }

        // FIFO推荐：入库时间升序 + 未过期 + 在库状态 + 库存>0
        LambdaQueryWrapper<AcfLot> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AcfLot::getMaterialCode, materialCode)
               .eq(AcfLot::getStatus, "IN_STOCK")
               .gt(AcfLot::getCurrentQuantity, 0)
               .gt(AcfLot::getExpireTime, LocalDateTime.now())
               .orderByAsc(AcfLot::getInStockTime)
               .last("LIMIT " + limit);

        List<AcfLot> lots = lotService.list(wrapper);

        // 转换为Map
        List<Map<String, Object>> result = new ArrayList<>();
        for (AcfLot lot : lots) {
            Map<String, Object> map = new HashMap<>();
            map.put("lotNumber", lot.getLotNumber());
            map.put("materialCode", lot.getMaterialCode());
            map.put("quantity", lot.getCurrentQuantity());
            map.put("inStockTime", lot.getInStockTime());
            map.put("expireTime", lot.getExpireTime());
            map.put("usageTimes", lot.getUsageTimes());
            map.put("warehouseLocation", lot.getWarehouseLocation());
            
            // 计算剩余有效天数
            long daysUntilExpire = ChronoUnit.DAYS.between(LocalDateTime.now(), lot.getExpireTime());
            map.put("daysUntilExpire", daysUntilExpire);
            
            result.add(map);
        }

        return result;
    }

    @Override
    public Page<Map<String, Object>> queryInventory(int pageNum, int pageSize, String materialCode,
                                                   String lotNumber, String warehouseLocation, String status,
                                                   LocalDateTime startTime, LocalDateTime endTime) {
        Page<AcfLot> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<AcfLot> wrapper = new LambdaQueryWrapper<>();

        if (materialCode != null && !materialCode.isEmpty()) {
            wrapper.like(AcfLot::getMaterialCode, materialCode);
        }
        if (lotNumber != null && !lotNumber.isEmpty()) {
            wrapper.like(AcfLot::getLotNumber, lotNumber);
        }
        if (warehouseLocation != null && !warehouseLocation.isEmpty()) {
            wrapper.like(AcfLot::getWarehouseLocation, warehouseLocation);
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(AcfLot::getStatus, status);
        }
        if (startTime != null) {
            wrapper.ge(AcfLot::getInStockTime, startTime);
        }
        if (endTime != null) {
            wrapper.le(AcfLot::getInStockTime, endTime);
        }

        wrapper.orderByDesc(AcfLot::getInStockTime);

        Page<AcfLot> lotPage = lotService.page(page, wrapper);

        // 转换为Map
        List<Map<String, Object>> records = new ArrayList<>();
        for (AcfLot lot : lotPage.getRecords()) {
            Map<String, Object> map = new HashMap<>();
            map.put("lotNumber", lot.getLotNumber());
            map.put("materialCode", lot.getMaterialCode());
            map.put("quantity", lot.getCurrentQuantity());
            map.put("status", lot.getStatus());
            map.put("usageTimes", lot.getUsageTimes());
            map.put("inStockTime", lot.getInStockTime());
            map.put("expireTime", lot.getExpireTime());
            map.put("warehouseLocation", lot.getWarehouseLocation());
            map.put("createTime", lot.getCreateTime());
            
            // 计算在库天数
            long daysInStock = ChronoUnit.DAYS.between(lot.getInStockTime(), LocalDateTime.now());
            map.put("daysInStock", daysInStock);
            
            // 计算剩余有效天数
            long daysUntilExpire = ChronoUnit.DAYS.between(LocalDateTime.now(), lot.getExpireTime());
            map.put("daysUntilExpire", daysUntilExpire);
            
            // 判断是否过期或预警
            boolean isExpired = lot.getExpireTime().isBefore(LocalDateTime.now());
            map.put("isExpired", isExpired);
            
            records.add(map);
        }

        Page<Map<String, Object>> resultPage = new Page<>(pageNum, pageSize);
        resultPage.setRecords(records);
        resultPage.setTotal(lotPage.getTotal());
        
        return resultPage;
    }

    @Override
    public Map<String, Object> queryInventoryStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        // 总库存数量
        LambdaQueryWrapper<AcfLot> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AcfLot::getStatus, "IN_STOCK");
        List<AcfLot> inStockLots = lotService.list(wrapper);
        
        BigDecimal totalQuantity = inStockLots.stream()
                .map(lot -> lot.getCurrentQuantity() != null ? lot.getCurrentQuantity() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        statistics.put("totalLots", inStockLots.size());
        statistics.put("totalQuantity", totalQuantity);

        // 过期数量
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AcfLot::getStatus, "IN_STOCK")
               .lt(AcfLot::getExpireTime, LocalDateTime.now());
        List<AcfLot> expiredLots = lotService.list(wrapper);
        statistics.put("expiredLots", expiredLots.size());

        // 预警数量（7天内过期）
        LocalDateTime alertDate = LocalDateTime.now().plusDays(7);
        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AcfLot::getStatus, "IN_STOCK")
               .lt(AcfLot::getExpireTime, alertDate)
               .ge(AcfLot::getExpireTime, LocalDateTime.now());
        List<AcfLot> alertLots = lotService.list(wrapper);
        statistics.put("alertLots", alertLots.size());

        // 使用次数接近上限
        AcfLot lotWithMaxUsage = new AcfLot();
        // TODO: 查询使用次数接近上限的LOT

        // 今日入库
        LocalDateTime todayStart = LocalDateTime.now().toLocalDate().atStartOfDay();
        wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(AcfLot::getInStockTime, todayStart);
        List<AcfLot> todayInbound = lotService.list(wrapper);
        statistics.put("todayInboundLots", todayInbound.size());

        return statistics;
    }

    /**
     * 生成交易单号
     */
    private String generateTransactionNo(String type) {
        String timestamp = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return type + "_" + timestamp + "_" + (int)(Math.random() * 10000);
    }
}
