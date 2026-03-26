package com.acf.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 预警DTO
 *
 * @author ACF Team
 */
@Data
public class AcfAlertDTO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 预警类型
     */
    private String alertType;

    /**
     * LOT号
     */
    private String lotNumber;

    /**
     * 预警消息
     */
    private String alertMessage;

    /**
     * 预警级别
     */
    private String alertLevel;

    /**
     * 是否已处理
     */
    private Integer isHandled;

    /**
     * 处理人ID
     */
    private Long handledBy;

    /**
     * 处理时间
     */
    private LocalDateTime handleTime;

    /**
     * 处理备注
     */
    private String handleRemark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
