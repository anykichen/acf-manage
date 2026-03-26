package com.acf.service.impl;

import com.acf.entity.AcfAlert;
import com.acf.entity.AcfLot;
import com.acf.entity.AcfTransaction;
import com.acf.enums.LotStatus;
import com.acf.mapper.AcfAlertMapper;
import com.acf.mapper.AcfLotMapper;
import com.acf.mapper.AcfMaterialMapper;
import com.acf.mapper.AcfTransactionMapper;
import com.acf.service.DashboardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 看板数据服务实现
 */
@Slf4j
@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private AcfLotMapper acfLotMapper;

    @Autowired
    private AcfTransactionMapper acfTransactionMapper;

    @Autowired
    private AcfMaterialMapper acfMaterialMapper;

    @Autowired
    private AcfAlertMapper acfAlertMapper;

    @Override
    public Map<String, Object> getDashboardData() {
        Map<String, Object> data = new HashMap<>();

        // 1. 总库存批次
        long totalBatchCount = acfLotMapper.selectCount(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AcfLot>()
        );
        data.put("totalBatchCount", totalBatchCount);

        // 2. 在库批次
        long inStockCount = acfLotMapper.selectCount(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AcfLot>()
                .eq(AcfLot::getStatus, LotStatus.IN_STOCK)
        );
        data.put("inStockCount", inStockCount);

        // 3. 使用中批次
        long inUseCount = acfLotMapper.selectCount(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AcfLot>()
                .eq(AcfLot::getStatus, LotStatus.IN_USE)
        );
        data.put("inUseCount", inUseCount);

        // 4. 已报废批次
        long scrappedCount = acfLotMapper.selectCount(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AcfLot>()
                .eq(AcfLot::getStatus, LotStatus.SCRAPPED)
        );
        data.put("scrappedCount", scrappedCount);

        // 5. 今日入库批次
        LocalDateTime todayStart = LocalDateTime.now().toLocalDate().atStartOfDay();
        long todayInboundCount = acfLotMapper.selectCount(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AcfLot>()
                .gt(AcfLot::getInboundTime, todayStart)
        );
        data.put("todayInboundCount", todayInboundCount);

        // 6. 今日发料批次
        List<AcfTransaction> todayOutbound = acfTransactionMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AcfTransaction>()
                .eq(AcfTransaction::getTransactionType, com.acf.enums.TransactionType.OUTBOUND)
                .gt(AcfTransaction::getTransactionTime, todayStart)
        );
        data.put("todayOutboundCount", todayOutbound.size());

        // 7. 今日退库批次
        List<AcfTransaction> todayReturn = acfTransactionMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AcfTransaction>()
                .eq(AcfTransaction::getTransactionType, com.acf.enums.TransactionType.RETURN)
                .gt(AcfTransaction::getTransactionTime, todayStart)
        );
        data.put("todayReturnCount", todayReturn.size());

        // 8. 待处理预警
        long pendingAlertCount = acfAlertMapper.selectCount(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AcfAlert>()
                .eq(AcfAlert::getStatus, "PENDING")
        );
        data.put("pendingAlertCount", pendingAlertCount);

        // 9. 总料号数量
        long materialCount = acfMaterialMapper.selectCount(null);
        data.put("materialCount", materialCount);

        // 10. 总交易次数
        long transactionCount = acfTransactionMapper.selectCount(null);
        data.put("transactionCount", transactionCount);

        return data;
    }

    @Override
    public Map<String, Object> getBusinessStatusData() {
        Map<String, Object> data = new HashMap<>();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayStart = now.toLocalDate().atStartOfDay();
        LocalDateTime weekStart = now.minusDays(7).toLocalDate().atStartOfDay();
        LocalDateTime monthStart = now.minusDays(30).toLocalDate().atStartOfDay();

        // 今日入库量
        List<AcfTransaction> todayInbound = acfTransactionMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AcfTransaction>()
                .eq(AcfTransaction::getTransactionType, com.acf.enums.TransactionType.INBOUND)
                .gt(AcfTransaction::getTransactionTime, todayStart)
        );
        int todayInboundQuantity = todayInbound.stream()
            .mapToInt(AcfTransaction::getQuantity)
            .sum();
        data.put("todayInboundQuantity", todayInboundQuantity);

        // 本周入库量
        List<AcfTransaction> weekInbound = acfTransactionMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AcfTransaction>()
                .eq(AcfTransaction::getTransactionType, com.acf.enums.TransactionType.INBOUND)
                .gt(AcfTransaction::getTransactionTime, weekStart)
        );
        int weekInboundQuantity = weekInbound.stream()
            .mapToInt(AcfTransaction::getQuantity)
            .sum();
        data.put("weekInboundQuantity", weekInboundQuantity);

        // 本月入库量
        List<AcfTransaction> monthInbound = acfTransactionMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AcfTransaction>()
                .eq(AcfTransaction::getTransactionType, com.acf.enums.TransactionType.INBOUND)
                .gt(AcfTransaction::getTransactionTime, monthStart)
        );
        int monthInboundQuantity = monthInbound.stream()
            .mapToInt(AcfTransaction::getQuantity)
            .sum();
        data.put("monthInboundQuantity", monthInboundQuantity);

        // 今日出库量
        List<AcfTransaction> todayOutbound = acfTransactionMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AcfTransaction>()
                .eq(AcfTransaction::getTransactionType, com.acf.enums.TransactionType.OUTBOUND)
                .gt(AcfTransaction::getTransactionTime, todayStart)
        );
        int todayOutboundQuantity = todayOutbound.stream()
            .mapToInt(AcfTransaction::getQuantity)
            .sum();
        data.put("todayOutboundQuantity", todayOutboundQuantity);

        // 本周出库量
        List<AcfTransaction> weekOutbound = acfTransactionMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AcfTransaction>()
                .eq(AcfTransaction::getTransactionType, com.acf.enums.TransactionType.OUTBOUND)
                .gt(AcfTransaction::getTransactionTime, weekStart)
        );
        int weekOutboundQuantity = weekOutbound.stream()
            .mapToInt(AcfTransaction::getQuantity)
            .sum();
        data.put("weekOutboundQuantity", weekOutboundQuantity);

        // 本月出库量
        List<AcfTransaction> monthOutbound = acfTransactionMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AcfTransaction>()
                .eq(AcfTransaction::getTransactionType, com.acf.enums.TransactionType.OUTBOUND)
                .gt(AcfTransaction::getTransactionTime, monthStart)
        );
        int monthOutboundQuantity = monthOutbound.stream()
            .mapToInt(AcfTransaction::getQuantity)
            .sum();
        data.put("monthOutboundQuantity", monthOutboundQuantity);

        // 入库出库对比
        data.put("inboundOutboundRatio", 
            todayOutboundQuantity > 0 ? 
            String.format("%.2f", (double) todayInboundQuantity / todayOutboundQuantity) : "0");

        // 业务繁忙指数（基于今日交易次数）
        long todayTransactionCount = acfTransactionMapper.selectCount(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AcfTransaction>()
                .gt(AcfTransaction::getTransactionTime, todayStart)
        );
        String busynessLevel = todayTransactionCount > 50 ? "繁忙" : 
                               todayTransactionCount > 20 ? "正常" : "空闲";
        data.put("busynessLevel", busynessLevel);

        return data;
    }

    @Override
    public Map<String, Object> getInventoryDistribution() {
        Map<String, Object> data = new HashMap<>();

        // 按状态统计库存分布
        List<AcfLot> allLots = acfLotMapper.selectList(null);

        Map<String, Long> statusCount = allLots.stream()
            .collect(Collectors.groupingBy(
                lot -> lot.getStatus().name(),
                Collectors.counting()
            ));

        List<String> categories = new ArrayList<>();
        List<Long> values = new ArrayList<>();

        for (Map.Entry<String, Long> entry : statusCount.entrySet()) {
            categories.add(entry.getKey());
            values.add(entry.getValue());
        }

        data.put("categories", categories);
        data.put("values", values);

        return data;
    }

    @Override
    public Map<String, Object> getTransactionTrend(int days) {
        Map<String, Object> data = new HashMap<>();

        List<String> dates = new ArrayList<>();
        List<Integer> inboundQuantities = new ArrayList<>();
        List<Integer> outboundQuantities = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime dayStart = date.atStartOfDay();
            LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();

            dates.add(date.format(formatter));

            // 入库量
            List<AcfTransaction> inbound = acfTransactionMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AcfTransaction>()
                    .eq(AcfTransaction::getTransactionType, com.acf.enums.TransactionType.INBOUND)
                    .ge(AcfTransaction::getTransactionTime, dayStart)
                    .lt(AcfTransaction::getTransactionTime, dayEnd)
            );
            int inboundQty = inbound.stream().mapToInt(AcfTransaction::getQuantity).sum();
            inboundQuantities.add(inboundQty);

            // 出库量
            List<AcfTransaction> outbound = acfTransactionMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AcfTransaction>()
                    .eq(AcfTransaction::getTransactionType, com.acf.enums.TransactionType.OUTBOUND)
                    .ge(AcfTransaction::getTransactionTime, dayStart)
                    .lt(AcfTransaction::getTransactionTime, dayEnd)
            );
            int outboundQty = outbound.stream().mapToInt(AcfTransaction::getQuantity).sum();
            outboundQuantities.add(outboundQty);
        }

        data.put("dates", dates);
        data.put("inboundQuantities", inboundQuantities);
        data.put("outboundQuantities", outboundQuantities);

        return data;
    }

    @Override
    public Map<String, Object> getAlertStatistics() {
        Map<String, Object> data = new HashMap<>();

        // 按类型统计预警
        List<AcfAlert> allAlerts = acfAlertMapper.selectList(null);

        Map<String, Long> typeCount = allAlerts.stream()
            .collect(Collectors.groupingBy(
                AcfAlert::getAlertType,
                Collectors.counting()
            ));

        List<String> types = new ArrayList<>();
        List<Long> counts = new ArrayList<>();

        for (Map.Entry<String, Long> entry : typeCount.entrySet()) {
            types.add(entry.getKey());
            counts.add(entry.getValue());
        }

        data.put("types", types);
        data.put("counts", counts);

        // 按级别统计预警
        Map<String, Long> levelCount = allAlerts.stream()
            .collect(Collectors.groupingBy(
                AcfAlert::getAlertLevel,
                Collectors.counting()
            ));

        List<String> levels = Arrays.asList("HIGH", "MEDIUM", "LOW");
        List<Long> levelCounts = new ArrayList<>();
        for (String level : levels) {
            levelCounts.add(levelCount.getOrDefault(level, 0L));
        }

        data.put("levels", levels);
        data.put("levelCounts", levelCounts);

        return data;
    }

    @Override
    public Map<String, Object> getMaterialUsageComparison() {
        Map<String, Object> data = new HashMap<>();

        // 查询所有LOT，按料号统计使用次数
        List<AcfLot> lots = acfLotMapper.selectList(null);

        Map<String, Integer> materialUsageMap = new HashMap<>();
        for (AcfLot lot : lots) {
            materialUsageMap.merge(lot.getMaterialCode(), lot.getUsageCount(), Integer::sum);
        }

        // 按使用次数排序，取前10
        List<Map.Entry<String, Integer>> sortedEntries = materialUsageMap.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .limit(10)
            .collect(Collectors.toList());

        List<String> materials = new ArrayList<>();
        List<Integer> usages = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : sortedEntries) {
            materials.add(entry.getKey());
            usages.add(entry.getValue());
        }

        data.put("materials", materials);
        data.put("usages", usages);

        return data;
    }
}
