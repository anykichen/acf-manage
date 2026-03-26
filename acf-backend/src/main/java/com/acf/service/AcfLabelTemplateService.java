package com.acf.service;

import com.acf.entity.AcfLabelTemplate;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 标签模板Service
 *
 * @author ACF Team
 */
public interface AcfLabelTemplateService extends IService<AcfLabelTemplate> {

    /**
     * 获取默认模板
     */
    AcfLabelTemplate getDefaultTemplate();

    /**
     * 设置默认模板
     */
    void setDefaultTemplate(Long id);

    /**
     * 批量删除
     */
    boolean batchDelete(List<Long> ids);
}
