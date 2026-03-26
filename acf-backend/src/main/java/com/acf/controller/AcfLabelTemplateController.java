package com.acf.controller;

import com.acf.entity.AcfLabelTemplate;
import com.acf.service.AcfLabelTemplateService;
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
 * 标签模板Controller
 *
 * @author ACF Team
 */
@Tag(name = "标签模板管理", description = "标签模板管理接口")
@RestController
@RequestMapping("/label-template")
@RequiredArgsConstructor
@Validated
public class AcfLabelTemplateController {

    private final AcfLabelTemplateService labelTemplateService;

    @Operation(summary = "查询标签模板列表")
    @GetMapping("/list")
    public PageVO<AcfLabelTemplate> list(
            @RequestParam(required = false) String templateName,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size) {

        LambdaQueryWrapper<AcfLabelTemplate> wrapper = new LambdaQueryWrapper<>();

        if (templateName != null && !templateName.isEmpty()) {
            wrapper.like(AcfLabelTemplate::getTemplateName, templateName);
        }

        if (status != null) {
            wrapper.eq(AcfLabelTemplate::getStatus, status);
        }

        wrapper.orderByDesc(AcfLabelTemplate::getCreateTime);

        Page<AcfLabelTemplate> page = labelTemplateService.page(new Page<>(current, size), wrapper);
        return new PageVO<>(page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize());
    }

    @Operation(summary = "根据ID查询标签模板")
    @GetMapping("/{id}")
    public AcfLabelTemplate getById(@PathVariable Long id) {
        return labelTemplateService.getById(id);
    }

    @Operation(summary = "获取默认标签模板")
    @GetMapping("/default")
    public AcfLabelTemplate getDefaultTemplate() {
        return labelTemplateService.getDefaultTemplate();
    }

    @Operation(summary = "新增标签模板")
    @PostMapping
    public void add(@Validated @RequestBody AcfLabelTemplate template) {
        labelTemplateService.save(template);
    }

    @Operation(summary = "更新标签模板")
    @PutMapping("/{id}")
    public void update(@PathVariable Long id, @Validated @RequestBody AcfLabelTemplate template) {
        template.setId(id);
        labelTemplateService.updateById(template);
    }

    @Operation(summary = "删除标签模板")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        labelTemplateService.removeById(id);
    }

    @Operation(summary = "批量删除标签模板")
    @DeleteMapping("/batch")
    public void batchDelete(@RequestBody List<Long> ids) {
        labelTemplateService.batchDelete(ids);
    }

    @Operation(summary = "设置默认模板")
    @PutMapping("/{id}/set-default")
    public void setDefaultTemplate(@PathVariable Long id) {
        labelTemplateService.setDefaultTemplate(id);
    }
}
