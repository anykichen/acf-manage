package com.acf.service;

import com.acf.dto.FIFORecommendDTO;
import com.acf.entity.AcfLot;
import com.acf.enums.LotStatus;
import com.acf.mapper.AcfLotMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 库存服务测试
 */
@SpringBootTest
public class InventoryServiceTest {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private AcfLotMapper acfLotMapper;

    private AcfLot testLot;

    @BeforeEach
    public void setUp() {
        // 清理测试数据
        acfLotMapper.delete(new LambdaQueryWrapper<AcfLot>().eq(AcfLot::getLotNumber, "TEST-LOT-001"));

        // 创建测试数据
        testLot = new AcfLot();
        testLot.setLotNumber("TEST-LOT-001");
        testLot.setMaterialCode("ACF-TEST");
        testLot.setCurrentQuantity(100);
        testLot.setStatus(LotStatus.IN_STOCK);
        testLot.setInboundTime(LocalDateTime.now());
        testLot.setExpireTime(LocalDateTime.now().plusDays(180));
        testLot.setUsageCount(0);
        testLot.setMaxUsageCount(3);
        testLot.setStorageLocation("A-01-01");
        acfLotMapper.insert(testLot);
    }

    @Test
    public void testFIFORecommend() {
        // 测试FIFO推荐
        List<FIFORecommendDTO> recommendations = inventoryService.fifoRecommend("ACF-TEST", 50);
        
        assertNotNull(recommendations);
        assertFalse(recommendations.isEmpty());
        assertEquals("TEST-LOT-001", recommendations.get(0).getLotNumber());
    }

    @Test
    public void testInbound() {
        // 测试入库
        inventoryService.inbound("TEST-LOT-001", "A-02-02", "Test User");

        AcfLot updatedLot = acfLotMapper.selectOne(
            new LambdaQueryWrapper<AcfLot>().eq(AcfLot::getLotNumber, "TEST-LOT-001")
        );

        assertEquals("A-02-02", updatedLot.getStorageLocation());
        assertEquals(LotStatus.IN_STOCK, updatedLot.getStatus());
    }

    @Test
    public void testOutbound() {
        // 先入库
        testLot.setStorageLocation("A-01-01");
        testLot.setStatus(LotStatus.IN_STOCK);
        acfLotMapper.updateById(testLot);

        // 测试发料
        inventoryService.outbound("TEST-LOT-001", 50, "Test User");

        AcfLot updatedLot = acfLotMapper.selectOne(
            new LambdaQueryWrapper<AcfLot>().eq(AcfLot::getLotNumber, "TEST-LOT-001")
        );

        assertEquals(50, updatedLot.getCurrentQuantity());
        assertEquals(1, updatedLot.getUsageCount());
        assertEquals(LotStatus.IN_USE, updatedLot.getStatus());
        assertNotNull(updatedLot.getWarmupStartTime());
    }

    @Test
    public void testOutboundInsufficientQuantity() {
        // 测试库存不足
        Exception exception = assertThrows(Exception.class, () -> {
            inventoryService.outbound("TEST-LOT-001", 200, "Test User");
        });

        assertTrue(exception.getMessage().contains("库存不足"));
    }

    @Test
    public void testOutboundMaxUsageExceeded() {
        // 设置使用次数已达上限
        testLot.setUsageCount(3);
        acfLotMapper.updateById(testLot);

        // 测试超过最大使用次数
        Exception exception = assertThrows(Exception.class, () -> {
            inventoryService.outbound("TEST-LOT-001", 10, "Test User");
        });

        assertTrue(exception.getMessage().contains("已达到最大使用次数"));
    }

    @Test
    public void testReturn() {
        // 先发料
        testLot.setCurrentQuantity(80);
        testLot.setUsageCount(1);
        testLot.setStatus(LotStatus.IN_USE);
        testLot.setWarmupStartTime(LocalDateTime.now().minusHours(2));
        acfLotMapper.updateById(testLot);

        // 测试退库
        inventoryService.returnStock("TEST-LOT-001", 20, "Test User");

        AcfLot updatedLot = acfLotMapper.selectOne(
            new LambdaQueryWrapper<AcfLot>().eq(AcfLot::getLotNumber, "TEST-LOT-001")
        );

        assertEquals(100, updatedLot.getCurrentQuantity());
        assertEquals(2, updatedLot.getUsageCount());
        assertEquals(LotStatus.IN_STOCK, updatedLot.getStatus());
        assertNotNull(updatedLot.getWarmupEndTime());
    }

    @Test
    public void testScrap() {
        // 测试报废
        inventoryService.scrap("TEST-LOT-001", "测试报废", "Test User");

        AcfLot updatedLot = acfLotMapper.selectOne(
            new LambdaQueryWrapper<AcfLot>().eq(AcfLot::getLotNumber, "TEST-LOT-001")
        );

        assertEquals(LotStatus.SCRAPPED, updatedLot.getStatus());
    }
}
