package com.acf.service;

import com.acf.entity.AcfAlertRule;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 预警规则Service
 *
 * @author ACF Team
 */
public interface AcfAlertRuleService extends IService<AcfAlertRule> {

    /**
     * 根据规则类型查询
     */
    List<AcfAlertRule> getByRuleType(String ruleType);

    /**
     * 激活规则
     */
    boolean activateRule(Long id);

    /**
     * 停用规则
     */
    boolean deactivateRule(Long id);

    /**
     * 批量删除
     */
    boolean batchDelete(List<Long> ids);
}
