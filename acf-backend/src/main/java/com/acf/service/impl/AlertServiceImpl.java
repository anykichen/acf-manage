package com.acf.service.impl;

import com.acf.dto.AcfAlertDTO;
import com.acf.entity.AcfAlert;
import com.acf.entity.AcfLot;
import com.acf.enums.LotStatus;
import com.acf.enums.TransactionType;
import com.acf.mapper.AcfAlertMapper;
import com.acf.mapper.AcfLotMapper;
import com.acf.service.AlertService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * 预警服务实现
 */
@Slf4j
@Service
public class AlertServiceImpl implements AlertService {

    @Autowired
    private AcfAlertMapper acfAlertMapper;

    @Autowired
    private AcfLotMapper acfLotMapper;

    /**
     * 定时任务：每小时检查一次预警
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void scheduledCheck() {
        log.info("开始执行预警检查任务...");
        try {
            // 检查过期预警
            List<AcfAlertDTO> expireAlerts = checkExpireAlert();
            log.info("过期预警数量: {}", expireAlerts.size());

            // 检查使用次数预警
            List<AcfAlertDTO> usageAlerts = checkUsageAlert();
            log.info("使用次数预警数量: {}", usageAlerts.size());

            // 发送通知
            expireAlerts.forEach(this::sendAlertNotification);
            usageAlerts.forEach(this::sendAlertNotification);

            log.info("预警检查任务完成");
        } catch (Exception e) {
            log.error("预警检查任务执行失败", e);
        }
    }

    @Override
    public List<AcfAlertDTO> checkExpireAlert() {
        List<AcfAlertDTO> alerts = new ArrayList<>();

        // 查询所有在库的LOT
        List<AcfLot> lots = acfLotMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AcfLot>()
                .eq(AcfLot::getStatus, LotStatus.IN_STOCK)
                .gt(AcfLot::getExpireTime, LocalDateTime.now())
        );

        LocalDateTime now = LocalDateTime.now();

        for (AcfLot lot : lots) {
            // 计算剩余天数
            long daysRemaining = ChronoUnit.DAYS.between(now, lot.getExpireTime());

            // 如果剩余7天内，则触发预警
            if (daysRemaining <= 7) {
                AcfAlert alert = new AcfAlert();
                alert.setLotNumber(lot.getLotNumber());
                alert.setMaterialCode(lot.getMaterialCode());
                alert.setAlertType("EXPIRE_SOON");
                alert.setAlertLevel(daysRemaining <= 3 ? "HIGH" : "MEDIUM");
                alert.setAlertMessage(String.format("LOT %s 将在 %d 天后过期", 
                    lot.getLotNumber(), daysRemaining));
                alert.setAlertTime(LocalDateTime.now());
                alert.setStatus("PENDING");

                acfAlertMapper.insert(alert);

                AcfAlertDTO dto = new AcfAlertDTO();
                BeanUtils.copyProperties(alert, dto);
                alerts.add(dto);
            }
        }

        return alerts;
    }

    @Override
    public List<AcfAlertDTO> checkUsageAlert() {
        List<AcfAlertDTO> alerts = new ArrayList<>();

        // 查询所有在库的LOT
        List<AcfLot> lots = acfLotMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AcfLot>()
                .eq(AcfLot::getStatus, LotStatus.IN_STOCK)
                .isNotNull(AcfLot::getMaxUsageCount)
        );

        for (AcfLot lot : lots) {
            // 如果使用次数已达到最大使用次数的80%，则触发预警
            if (lot.getUsageCount() > 0 && lot.getMaxUsageCount() > 0) {
                double usageRatio = (double) lot.getUsageCount() / lot.getMaxUsageCount();
                if (usageRatio >= 0.8) {
                    AcfAlert alert = new AcfAlert();
                    alert.setLotNumber(lot.getLotNumber());
                    alert.setMaterialCode(lot.getMaterialCode());
                    alert.setAlertType("USAGE_HIGH");
                    alert.setAlertLevel(usageRatio >= 0.9 ? "HIGH" : "MEDIUM");
                    alert.setAlertMessage(String.format("LOT %s 使用次数 %d/%d，即将达到上限", 
                        lot.getLotNumber(), lot.getUsageCount(), lot.getMaxUsageCount()));
                    alert.setAlertTime(LocalDateTime.now());
                    alert.setStatus("PENDING");

                    acfAlertMapper.insert(alert);

                    AcfAlertDTO dto = new AcfAlertDTO();
                    BeanUtils.copyProperties(alert, dto);
                    alerts.add(dto);
                }
            }
        }

        return alerts;
    }

    @Override
    public void sendAlertNotification(AcfAlertDTO alert) {
        // TODO: 实现预警通知（邮件、短信、系统消息等）
        log.info("发送预警通知: {}", alert.getAlertMessage());
        // 暂时只记录日志
    }

    @Override
    public List<AcfAlertDTO> getPendingAlerts() {
        List<AcfAlert> alerts = acfAlertMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AcfAlert>()
                .eq(AcfAlert::getStatus, "PENDING")
                .orderByDesc(AcfAlert::getAlertTime)
        );

        List<AcfAlertDTO> dtos = new ArrayList<>();
        for (AcfAlert alert : alerts) {
            AcfAlertDTO dto = new AcfAlertDTO();
            BeanUtils.copyProperties(alert, dto);
            dtos.add(dto);
        }

        return dtos;
    }

    @Override
    public void markAlertAsResolved(Long alertId) {
        acfAlertMapper.updateById(new AcfAlert() {{
            setId(alertId);
            setStatus("RESOLVED");
            setResolvedTime(LocalDateTime.now());
        }});
    }
}
