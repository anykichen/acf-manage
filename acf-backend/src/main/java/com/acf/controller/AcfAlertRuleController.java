package com.acf.controller;

import com.acf.entity.AcfAlertRule;
import com.acf.service.AcfAlertRuleService;
import com.acf.vo.PageVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 预警规则Controller
 *
 * @author ACF Team
 */
@Tag(name = "预警规则管理", description = "预警规则管理接口")
@RestController
@RequestMapping("/alert-rule")
@RequiredArgsConstructor
@Validated
public class AcfAlertRuleController {

    private final AcfAlertRuleService alertRuleService;

    @Operation(summary = "查询预警规则列表")
    @GetMapping("/list")
    public PageVO<AcfAlertRule> list(
            @RequestParam(required = false) String ruleName,
            @RequestParam(required = false) String ruleType,
            @RequestParam(required = false) Integer isActive,
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size) {

        LambdaQueryWrapper<AcfAlertRule> wrapper = new LambdaQueryWrapper<>();

        if (ruleName != null && !ruleName.isEmpty()) {
            wrapper.like(AcfAlertRule::getRuleName, ruleName);
        }

        if (ruleType != null && !ruleType.isEmpty()) {
            wrapper.eq(AcfAlertRule::getRuleType, ruleType);
        }

        if (isActive != null) {
            wrapper.eq(AcfAlertRule::getIsActive, isActive);
        }

        wrapper.orderByDesc(AcfAlertRule::getCreateTime);

        Page<AcfAlertRule> page = alertRuleService.page(new Page<>(current, size), wrapper);
        return new PageVO<>(page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Operation(summary = "根据规则类型查询")
    @GetMapping("/type/{ruleType}")
    public List<AcfAlertRule> getByRuleType(@PathVariable String ruleType) {
        return alertRuleService.getByRuleType(ruleType);
    }

    @Operation(summary = "根据ID查询预警规则")
    @GetMapping("/{id}")
    public AcfAlertRule getById(@PathVariable Long id) {
        return alertRuleService.getById(id);
    }

    @Operation(summary = "新增预警规则")
    @PostMapping
    public void add(@Validated @RequestBody AcfAlertRule rule) {
        alertRuleService.save(rule);
    }

    @Operation(summary = "更新预警规则")
    @PutMapping("/{id}")
    public void update(@PathVariable Long id, @Validated @RequestBody AcfAlertRule rule) {
        rule.setId(id);
        alertRuleService.updateById(rule);
    }

    @Operation(summary = "删除预警规则")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        alertRuleService.removeById(id);
    }

    @Operation(summary = "批量删除预警规则")
    @DeleteMapping("/batch")
    public void batchDelete(@RequestBody List<Long> ids) {
        alertRuleService.batchDelete(ids);
    }

    @Operation(summary = "激活规则")
    @PutMapping("/{id}/activate")
    public void activateRule(@PathVariable Long id) {
        alertRuleService.activateRule(id);
    }

    @Operation(summary = "停用规则")
    @PutMapping("/{id}/deactivate")
    public void deactivateRule(@PathVariable Long id) {
        alertRuleService.deactivateRule(id);
    }
}
