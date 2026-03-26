package com.acf.service;

import com.acf.entity.AcfAlert;
import com.acf.mapper.AcfAlertMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 预警服务测试
 */
@SpringBootTest
public class AlertServiceTest {

    @Autowired
    private AlertService alertService;

    @Autowired
    private AcfAlertMapper acfAlertMapper;

    @Test
    public void testCheckExpireAlert() {
        // 测试过期预警检查
        List alerts = alertService.checkExpireAlert();
        
        assertNotNull(alerts);
        assertTrue(alerts.size() >= 0);
    }

    @Test
    public void testCheckUsageAlert() {
        // 测试使用次数预警检查
        List alerts = alertService.checkUsageAlert();
        
        assertNotNull(alerts);
        assertTrue(alerts.size() >= 0);
    }

    @Test
    public void testGetPendingAlerts() {
        // 测试获取待处理预警
        List alerts = alertService.getPendingAlerts();
        
        assertNotNull(alerts);
        assertTrue(alerts.size() >= 0);
    }

    @Test
    public void testMarkAlertAsResolved() {
        // 创建测试预警
        AcfAlert alert = new AcfAlert();
        alert.setLotNumber("TEST-LOT-001");
        alert.setMaterialCode("ACF-001");
        alert.setAlertType("EXPIRE_SOON");
        alert.setAlertLevel("HIGH");
        alert.setAlertMessage("测试预警");
        alert.setStatus("PENDING");
        acfAlertMapper.insert(alert);

        // 标记为已处理
        alertService.markAlertAsResolved(alert.getId());

        // 验证
        AcfAlert updated = acfAlertMapper.selectById(alert.getId());
        assertEquals("RESOLVED", updated.getStatus());
        assertNotNull(updated.getResolvedTime());

        // 清理
        acfAlertMapper.deleteById(alert.getId());
    }
}
