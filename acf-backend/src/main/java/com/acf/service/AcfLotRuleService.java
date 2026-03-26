package com.acf.service;

import com.acf.entity.AcfLotRule;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * LOT号规则Service接口
 */
public interface AcfLotRuleService extends IService<AcfLotRule> {

    /**
     * 分页查询规则
     */
    Page<AcfLotRule> queryRulePage(int pageNum, int pageSize, String ruleName, Integer status);

    /**
     * 获取默认规则
     */
    AcfLotRule getDefaultRule();

    /**
     * 查询所有启用的规则
     */
    java.util.List<AcfLotRule> getActiveRules();

    /**
     * 生成LOT号
     */
    String generateLotNumber(String materialCode);

    /**
     * 设置默认规则
     */
    boolean setDefaultRule(Long ruleId);

    /**
     * 根据规则名称查询
     */
    AcfLotRule queryByRuleName(String ruleName);
}
