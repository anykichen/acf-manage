package com.acf.service.impl;

import com.acf.entity.AcfLotRule;
import com.acf.mapper.AcfLotRuleMapper;
import com.acf.service.AcfLotRuleService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * LOT号规则Service实现类
 */
@Service
public class AcfLotRuleServiceImpl extends ServiceImpl<AcfLotRuleMapper, AcfLotRule>
        implements AcfLotRuleService {

    /**
     * 序列号计数器（生产环境应使用Redis）
     */
    private static final AtomicInteger SEQUENCE_COUNTER = new AtomicInteger(0);

    @Override
    public Page<AcfLotRule> queryRulePage(int pageNum, int pageSize, String ruleName, Integer status) {
        Page<AcfLotRule> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<AcfLotRule> wrapper = new LambdaQueryWrapper<>();

        if (ruleName != null && !ruleName.isEmpty()) {
            wrapper.like(AcfLotRule::getRuleName, ruleName);
        }
        if (status != null) {
            wrapper.eq(AcfLotRule::getStatus, status);
        }

        wrapper.orderByDesc(AcfLotRule::getIsDefault)
               .orderByDesc(AcfLotRule::getCreateTime);

        return this.page(page, wrapper);
    }

    @Override
    public AcfLotRule getDefaultRule() {
        AcfLotRule defaultRule = baseMapper.selectDefaultRule();
        if (defaultRule == null) {
            // 如果没有默认规则，返回第一个启用的规则
            List<AcfLotRule> activeRules = getActiveRules();
            if (!activeRules.isEmpty()) {
                return activeRules.get(0);
            }
        }
        return defaultRule;
    }

    @Override
    public List<AcfLotRule> getActiveRules() {
        return baseMapper.selectActiveRules();
    }

    @Override
    public String generateLotNumber(String materialCode) {
        AcfLotRule rule = getDefaultRule();
        if (rule == null) {
            throw new RuntimeException("未配置LOT号生成规则");
        }

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String dateStr = now.format(dateFormatter);

        // 生成序列号
        int seq = SEQUENCE_COUNTER.incrementAndGet();
        String seqStr = String.format("%0" + rule.getSequenceLength() + "d", seq);

        // 替换变量
        String lotNumber = rule.getTextDescriptionFormat()
                .replace("{lotNumber}", "")
                .replace("{materialCode}", materialCode)
                .replace("{year}", String.valueOf(now.getYear()))
                .replace("{month}", String.format("%02d", now.getMonthValue()))
                .replace("{day}", String.format("%02d", now.getDayOfMonth()))
                .replace("{date}", dateStr)
                .replace("{seq}", seqStr);

        return lotNumber;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setDefaultRule(Long ruleId) {
        // 先取消所有默认设置
        LambdaQueryWrapper<AcfLotRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AcfLotRule::getIsDefault, 1);

        List<AcfLotRule> allDefaultRules = this.list(wrapper);
        for (AcfLotRule rule : allDefaultRules) {
            rule.setIsDefault(0);
            rule.setUpdateTime(LocalDateTime.now());
        }
        this.updateBatchById(allDefaultRules);

        // 设置新的默认规则
        AcfLotRule newDefault = this.getById(ruleId);
        if (newDefault != null) {
            newDefault.setIsDefault(1);
            newDefault.setUpdateTime(LocalDateTime.now());
            return this.updateById(newDefault);
        }

        return false;
    }

    @Override
    public AcfLotRule queryByRuleName(String ruleName) {
        return baseMapper.selectByRuleName(ruleName);
    }
}
