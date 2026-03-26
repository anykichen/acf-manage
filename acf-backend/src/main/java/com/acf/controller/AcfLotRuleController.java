package com.acf.controller;

import com.acf.common.Result;
import com.acf.common.ResultCode;
import com.acf.entity.AcfLotRule;
import com.acf.service.AcfLotRuleService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * LOT号规则Controller
 */
@Tag(name = "LOT号规则管理", description = "LOT号规则相关接口")
@RestController
@RequestMapping("/api/lot-rule")
public class AcfLotRuleController {

    @Autowired
    private AcfLotRuleService lotRuleService;

    @Operation(summary = "分页查询规则")
    @GetMapping("/page")
    public Result<Page<AcfLotRule>> queryRulePage(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "规则名称") @RequestParam(required = false) String ruleName,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status) {

        Page<AcfLotRule> page = lotRuleService.queryRulePage(pageNum, pageSize, ruleName, status);
        return Result.success(page);
    }

    @Operation(summary = "查询所有规则")
    @GetMapping("/list")
    public Result<List<AcfLotRule>> queryAllRules() {
        List<AcfLotRule> rules = lotRuleService.list();
        return Result.success(rules);
    }

    @Operation(summary = "查询启用的规则")
    @GetMapping("/active")
    public Result<List<AcfLotRule>> queryActiveRules() {
        List<AcfLotRule> rules = lotRuleService.getActiveRules();
        return Result.success(rules);
    }

    @Operation(summary = "获取默认规则")
    @GetMapping("/default")
    public Result<AcfLotRule> getDefaultRule() {
        AcfLotRule rule = lotRuleService.getDefaultRule();
        return Result.success(rule);
    }

    @Operation(summary = "根据ID查询规则")
    @GetMapping("/{id}")
    public Result<AcfLotRule> queryRuleById(
            @Parameter(description = "规则ID") @PathVariable Long id) {
        AcfLotRule rule = lotRuleService.getById(id);
        if (rule == null) {
            return Result.error(ResultCode.NOT_FOUND, "规则不存在");
        }
        return Result.success(rule);
    }

    @Operation(summary = "新增规则")
    @PostMapping
    public Result<Void> createRule(@RequestBody AcfLotRule rule) {
        // 检查规则名称是否重复
        AcfLotRule existRule = lotRuleService.queryByRuleName(rule.getRuleName());
        if (existRule != null) {
            return Result.error(ResultCode.FAILED, "规则名称已存在");
        }

        boolean success = lotRuleService.save(rule);
        if (success) {
            return Result.success();
        } else {
            return Result.error(ResultCode.FAILED, "新增失败");
        }
    }

    @Operation(summary = "更新规则")
    @PutMapping("/{id}")
    public Result<Void> updateRule(
            @Parameter(description = "规则ID") @PathVariable Long id,
            @RequestBody AcfLotRule rule) {
        rule.setId(id);
        boolean success = lotRuleService.updateById(rule);
        if (success) {
            return Result.success();
        } else {
            return Result.error(ResultCode.FAILED, "更新失败");
        }
    }

    @Operation(summary = "删除规则")
    @DeleteMapping("/{id}")
    public Result<Void> deleteRule(@Parameter(description = "规则ID") @PathVariable Long id) {
        boolean success = lotRuleService.removeById(id);
        if (success) {
            return Result.success();
        } else {
            return Result.error(ResultCode.FAILED, "删除失败");
        }
    }

    @Operation(summary = "批量删除规则")
    @DeleteMapping("/batch")
    public Result<Void> batchDeleteRules(@Parameter(description = "规则ID列表") @RequestBody List<Long> ids) {
        boolean success = lotRuleService.removeByIds(ids);
        if (success) {
            return Result.success();
        } else {
            return Result.error(ResultCode.FAILED, "批量删除失败");
        }
    }

    @Operation(summary = "设置默认规则")
    @PutMapping("/{id}/set-default")
    public Result<Void> setDefaultRule(@Parameter(description = "规则ID") @PathVariable Long id) {
        boolean success = lotRuleService.setDefaultRule(id);
        if (success) {
            return Result.success();
        } else {
            return Result.error(ResultCode.FAILED, "设置默认规则失败");
        }
    }

    @Operation(summary = "生成LOT号")
    @GetMapping("/generate")
    public Result<String> generateLotNumber(
            @Parameter(description = "料号") @RequestParam String materialCode) {
        String lotNumber = lotRuleService.generateLotNumber(materialCode);
        return Result.success(lotNumber);
    }
}
