package com.acf.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志实体类
 */
@Data
@TableName("acf_operation_log")
public class AcfOperationLog {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 操作类型
     */
    private String operationType;

    /**
     * LOT号
     */
    private String lotNumber;

    /**
     * 料号
     */
    private String materialCode;

    /**
     * 操作人
     */
    private String operator;

    /**
     * 操作时间
     */
    private LocalDateTime operationTime;

    /**
     * 操作详情
     */
    private String operationDetail;

    /**
     * 操作结果
     */
    private String operationResult;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
