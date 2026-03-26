package com.acf.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * LOT号规则实体类
 */
@Data
@TableName("acf_lot_rule")
public class AcfLotRule {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 规则描述
     */
    private String ruleDescription;

    /**
     * 二维码格式（支持变量：{lotNumber}、{materialCode}、{year}、{month}、{day}、{seq}）
     */
    private String qrCodeFormat;

    /**
     * 文本描述格式
     */
    private String textDescriptionFormat;

    /**
     * 序列号起始值
     */
    private Integer sequenceStart;

    /**
     * 序列号长度（补零位数）
     */
    private Integer sequenceLength;

    /**
     * 是否默认规则
     */
    private Integer isDefault;

    /**
     * 状态（0-停用 1-启用）
     */
    private Integer status;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新人
     */
    private String updatedBy;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 是否删除（0-未删除 1-已删除）
     */
    @TableLogic
    private Integer deleted;
}
