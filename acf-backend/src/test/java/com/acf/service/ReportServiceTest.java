package com.acf.service;

import com.acf.dto.InventoryStatisticsDTO;
import com.acf.dto.TransactionStatisticsDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 报表服务测试
 */
@SpringBootTest
public class ReportServiceTest {

    @Autowired
    private ReportService reportService;

    @Test
    public void testGenerateInventoryReport() {
        // 测试生成库存报表
        InventoryStatisticsDTO report = reportService.generateInventoryReport();
        
        assertNotNull(report);
        assertTrue(report.getTotalBatchCount() >= 0);
        assertTrue(report.getTotalQuantity() >= 0);
        assertTrue(report.getExpiredBatchCount() >= 0);
        assertTrue(report.getWarningBatchCount() >= 0);
        assertTrue(report.getTodayInboundCount() >= 0);
    }

    @Test
    public void testGenerateTransactionReport() {
        // 测试生成交易报表
        String today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        TransactionStatisticsDTO report = reportService.generateTransactionReport(today, today);
        
        assertNotNull(report);
        assertTrue(report.getTotalTransactions() >= 0);
        assertTrue(report.getTotalQuantity() >= 0);
        assertTrue(report.getFailedTransactions() >= 0);
    }

    @Test
    public void testGenerateAlertReport() {
        // 测试生成预警报表
        var report = reportService.generateAlertReport();
        
        assertNotNull(report);
    }
}
