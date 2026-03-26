package com.acf.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 预警记录实体类
 *
 * @author ACF Team
 */
@Data
@TableName("acf_alert")
public class AcfAlert implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 预警类型（EXPIRE-过期, USAGE-使用次数, STOCK-库存）
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
     * 预警级别（HIGH-高, MEDIUM-中, LOW-低）
     */
    private String alertLevel;

    /**
     * 是否已处理（1-是, 0-否）
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

    /**
     * 删除标记（0-正常, 1-删除）
     */
    private Integer deleted;
}
