package com.acf.service.impl;

import com.acf.dto.InventoryStatisticsDTO;
import com.acf.dto.TransactionStatisticsDTO;
import com.acf.entity.AcfLot;
import com.acf.entity.AcfTransaction;
import com.acf.enums.LotStatus;
import com.acf.enums.TransactionType;
import com.acf.mapper.AcfLotMapper;
import com.acf.mapper.AcfTransactionMapper;
import com.acf.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 报表服务实现
 */
@Slf4j
@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private AcfLotMapper acfLotMapper;

    @Autowired
    private AcfTransactionMapper acfTransactionMapper;

    @Override
    public InventoryStatisticsDTO generateInventoryReport() {
        InventoryStatisticsDTO dto = new InventoryStatisticsDTO();

        // 查询所有在库的LOT
        List<AcfLot> lots = acfLotMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AcfLot>()
                .eq(AcfLot::getStatus, LotStatus.IN_STOCK)
        );

        LocalDateTime now = LocalDateTime.now();

        // 统计总库存批次
        dto.setTotalBatchCount((long) lots.size());

        // 统计总库存数量
        int totalQuantity = lots.stream().mapToInt(AcfLot::getCurrentQuantity).sum();
        dto.setTotalQuantity(totalQuantity);

        // 统计过期批次
        long expiredCount = lots.stream()
            .filter(lot -> lot.getExpireTime().isBefore(now))
            .count();
        dto.setExpiredBatchCount(expiredCount);

        // 统计预警批次（7天内过期）
        long warningCount = lots.stream()
            .filter(lot -> {
                long daysRemaining = ChronoUnit.DAYS.between(now, lot.getExpireTime());
                return daysRemaining > 0 && daysRemaining <= 7;
            })
            .count();
        dto.setWarningBatchCount(warningCount);

        // 统计今日入库批次
        LocalDateTime todayStart = now.toLocalDate().atStartOfDay();
        long todayInboundCount = lots.stream()
            .filter(lot -> lot.getInboundTime().isAfter(todayStart))
            .count();
        dto.setTodayInboundCount(todayInboundCount);

        // 统计各料号库存
        Map<String, Integer> materialQuantityMap = new HashMap<>();
        for (AcfLot lot : lots) {
            materialQuantityMap.merge(lot.getMaterialCode(), lot.getCurrentQuantity(), Integer::sum);
        }
        dto.setMaterialQuantityMap(materialQuantityMap);

        return dto;
    }

    @Override
    public TransactionStatisticsDTO generateTransactionReport(String startDate, String endDate) {
        TransactionStatisticsDTO dto = new TransactionStatisticsDTO();

        // 查询交易记录
        List<AcfTransaction> transactions = acfTransactionMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AcfTransaction>()
                .between(AcfTransaction::getTransactionTime, startDate, endDate)
                .eq(AcfTransaction::getResult, "SUCCESS")
        );

        // 统计总交易次数
        dto.setTotalTransactions((long) transactions.size());

        // 统计各类型交易次数
        Map<String, Long> typeCountMap = new HashMap<>();
        Map<Integer, Integer> typeQuantityMap = new HashMap<>();

        for (AcfTransaction transaction : transactions) {
            String type = transaction.getTransactionType().name();
            typeCountMap.merge(type, 1L, Long::sum);
            typeQuantityMap.merge(transaction.getQuantity(), transaction.getQuantity(), Integer::sum);
        }

        dto.setTypeCountMap(typeCountMap);
        dto.setTypeQuantityMap(typeQuantityMap);

        // 统计成功交易数量
        int totalQuantity = transactions.stream()
            .mapToInt(AcfTransaction::getQuantity)
            .sum();
        dto.setTotalQuantity(totalQuantity);

        // 统计失败交易
        long failedCount = acfTransactionMapper.selectCount(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AcfTransaction>()
                .between(AcfTransaction::getTransactionTime, startDate, endDate)
                .eq(AcfTransaction::getResult, "FAILED")
        );
        dto.setFailedTransactions(failedCount);

        return dto;
    }

    @Override
    public void exportInventoryReportToExcel(HttpServletResponse response) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("库存报表");

            // 创建标题行
            Row headerRow = sheet.createRow(0);
            String[] headers = {"LOT号", "料号", "数量", "入库时间", "过期时间", "使用次数", "最大使用次数", "储位"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(createHeaderStyle(workbook));
            }

            // 查询数据
            List<AcfLot> lots = acfLotMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AcfLot>()
                    .eq(AcfLot::getStatus, LotStatus.IN_STOCK)
            );

            // 填充数据
            int rowNum = 1;
            for (AcfLot lot : lots) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(lot.getLotNumber());
                row.createCell(1).setCellValue(lot.getMaterialCode());
                row.createCell(2).setCellValue(lot.getCurrentQuantity());
                row.createCell(3).setCellValue(lot.getInboundTime().toString());
                row.createCell(4).setCellValue(lot.getExpireTime().toString());
                row.createCell(5).setCellValue(lot.getUsageCount());
                row.createCell(6).setCellValue(lot.getMaxUsageCount());
                row.createCell(7).setCellValue(lot.getStorageLocation());
            }

            // 自动调整列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // 输出到响应
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("库存报表_" + System.currentTimeMillis() + ".xlsx", StandardCharsets.UTF_8);
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);

            workbook.write(response.getOutputStream());

        } catch (IOException e) {
            log.error("导出库存报表失败", e);
            throw new RuntimeException("导出库存报表失败", e);
        }
    }

    @Override
    public void exportTransactionReportToExcel(String startDate, String endDate, HttpServletResponse response) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("交易报表");

            // 创建标题行
            Row headerRow = sheet.createRow(0);
            String[] headers = {"交易单号", "LOT号", "交易类型", "数量", "变动前数量", "变动后数量", "交易时间", "操作人", "结果"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(createHeaderStyle(workbook));
            }

            // 查询数据
            List<AcfTransaction> transactions = acfTransactionMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AcfTransaction>()
                    .between(AcfTransaction::getTransactionTime, startDate, endDate)
                    .orderByDesc(AcfTransaction::getTransactionTime)
            );

            // 填充数据
            int rowNum = 1;
            for (AcfTransaction transaction : transactions) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(transaction.getTransactionNumber());
                row.createCell(1).setCellValue(transaction.getLotNumber());
                row.createCell(2).setCellValue(transaction.getTransactionType().name());
                row.createCell(3).setCellValue(transaction.getQuantity());
                row.createCell(4).setCellValue(transaction.getBeforeQuantity());
                row.createCell(5).setCellValue(transaction.getAfterQuantity());
                row.createCell(6).setCellValue(transaction.getTransactionTime().toString());
                row.createCell(7).setCellValue(transaction.getOperator());
                row.createCell(8).setCellValue(transaction.getResult());
            }

            // 自动调整列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // 输出到响应
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("交易报表_" + startDate + "_" + endDate + ".xlsx", StandardCharsets.UTF_8);
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);

            workbook.write(response.getOutputStream());

        } catch (IOException e) {
            log.error("导出交易报表失败", e);
            throw new RuntimeException("导出交易报表失败", e);
        }
    }

    @Override
    public Map<String, Object> generateAlertReport() {
        Map<String, Object> report = new HashMap<>();

        // 统计待处理预警
        // TODO: 实现预警统计

        return report;
    }

    /**
     * 创建标题样式
     */
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }
}
