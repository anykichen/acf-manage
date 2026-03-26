package com.acf.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ACF料号实体类
 *
 * @author ACF Team
 */
@Data
@TableName("acf_material")
public class AcfMaterial implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 料号
     */
    private String materialCode;

    /**
     * 料号名称
     */
    private String materialName;

    /**
     * 物料描述
     */
    private String materialDesc;

    /**
     * 单位
     */
    private String unit;

    /**
     * 条码解析规则（正则表达式）
     */
    private String barcodeRule;

    /**
     * 厂商
     */
    private String manufacturer;

    /**
     * 型号
     */
    private String model;

    /**
     * 保存期限（月）
     */
    private Integer shelfLifeMonths;

    /**
     * 最大使用次数
     */
    private Integer maxUsageTimes;

    /**
     * 状态（1-启用, 0-停用）
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 删除标记（0-正常, 1-删除）
     */
    private Integer deleted;
}
