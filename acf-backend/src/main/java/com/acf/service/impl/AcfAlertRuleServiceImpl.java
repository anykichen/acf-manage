package com.acf.service.impl;

import com.acf.entity.AcfAlertRule;
import com.acf.mapper.AcfAlertRuleMapper;
import com.acf.service.AcfAlertRuleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 预警规则Service实现类
 *
 * @author ACF Team
 */
@Service
public class AcfAlertRuleServiceImpl extends ServiceImpl<AcfAlertRuleMapper, AcfAlertRule>
        implements AcfAlertRuleService {

    @Override
    public List<AcfAlertRule> getByRuleType(String ruleType) {
        return list(new LambdaQueryWrapper<AcfAlertRule>()
                .eq(AcfAlertRule::getRuleType, ruleType)
                .eq(AcfAlertRule::getIsActive, 1)
                .eq(AcfAlertRule::getDeleted, 0)
                .orderByAsc(AcfAlertRule::getId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean activateRule(Long id) {
        AcfAlertRule rule = getById(id);
        if (rule == null) {
            return false;
        }
        rule.setIsActive(1);
        rule.setUpdateTime(LocalDateTime.now());
        return updateById(rule);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deactivateRule(Long id) {
        AcfAlertRule rule = getById(id);
        if (rule == null) {
            return false;
        }
        rule.setIsActive(0);
        rule.setUpdateTime(LocalDateTime.now());
        return updateById(rule);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDelete(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return true;
        }
        return update().set("deleted", 1)
                .set("update_time", LocalDateTime.now())
                .in("id", ids)
                .update();
    }
}
