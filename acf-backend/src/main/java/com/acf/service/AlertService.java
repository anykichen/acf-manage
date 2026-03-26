package com.acf.service;

import com.acf.dto.AcfAlertDTO;

import java.util.List;

/**
 * 预警服务接口
 */
public interface AlertService {

    /**
     * 检查过期预警
     * @return 预警列表
     */
    List<AcfAlertDTO> checkExpireAlert();

    /**
     * 检查使用次数预警
     * @return 预警列表
     */
    List<AcfAlertDTO> checkUsageAlert();

    /**
     * 发送预警通知
     * @param alert 预警信息
     */
    void sendAlertNotification(AcfAlertDTO alert);

    /**
     * 获取所有待处理的预警
     * @return 预警列表
     */
    List<AcfAlertDTO> getPendingAlerts();

    /**
     * 标记预警为已处理
     * @param alertId 预警ID
     */
    void markAlertAsResolved(Long alertId);
}
