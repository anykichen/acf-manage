package com.acf.controller;

import com.acf.common.BusinessException;
import com.acf.common.Result;
import com.acf.common.ResultCode;
import com.acf.entity.AcfMaterial;
import com.acf.service.AcfMaterialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 料号Controller
 *
 * @author ACF Team
 */
@Tag(name = "料号管理", description = "ACF料号管理接口")
@RestController
@RequestMapping("/material")
@RequiredArgsConstructor
@Validated
public class AcfMaterialController {

    private final AcfMaterialService materialService;

    @Operation(summary = "查询料号列表")
    @GetMapping("/list")
    public Result<List<AcfMaterial>> list(
            @RequestParam(required = false) String materialCode,
            @RequestParam(required = false) String materialName,
            @RequestParam(required = false) String manufacturer,
            @RequestParam(required = false) Integer status) {

        // TODO: 实现分页查询和条件筛选
        List<AcfMaterial> list = materialService.list();
        return Result.success(list);
    }

    @Operation(summary = "根据ID查询料号")
    @GetMapping("/{id}")
    public Result<AcfMaterial> getById(@PathVariable Long id) {
        AcfMaterial material = materialService.getById(id);
        if (material == null) {
            throw new BusinessException(ResultCode.MATERIAL_NOT_EXISTS);
        }
        return Result.success(material);
    }

    @Operation(summary = "根据料号查询")
    @GetMapping("/code/{materialCode}")
    public Result<AcfMaterial> getByCode(@PathVariable String materialCode) {
        AcfMaterial material = materialService.getByMaterialCode(materialCode);
        if (material == null) {
            throw new BusinessException(ResultCode.MATERIAL_NOT_EXISTS);
        }
        return Result.success(material);
    }

    @Operation(summary = "新增料号")
    @PostMapping
    public Result<Void> add(@Validated @RequestBody AcfMaterial material) {
        // 检查料号是否已存在
        AcfMaterial existMaterial = materialService.getByMaterialCode(material.getMaterialCode());
        if (existMaterial != null) {
            throw new BusinessException(ResultCode.MATERIAL_EXISTS);
        }
        materialService.save(material);
        return Result.success("新增成功");
    }

    @Operation(summary = "更新料号")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Validated @RequestBody AcfMaterial material) {
        material.setId(id);
        AcfMaterial existMaterial = materialService.getById(id);
        if (existMaterial == null) {
            throw new BusinessException(ResultCode.MATERIAL_NOT_EXISTS);
        }
        materialService.updateById(material);
        return Result.success("更新成功");
    }

    @Operation(summary = "删除料号")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        AcfMaterial material = materialService.getById(id);
        if (material == null) {
            throw new BusinessException(ResultCode.MATERIAL_NOT_EXISTS);
        }
        materialService.removeById(id);
        return Result.success("删除成功");
    }

    @Operation(summary = "批量删除料号")
    @DeleteMapping("/batch")
    public Result<Void> batchDelete(@RequestBody List<Long> ids) {
        materialService.batchDelete(ids);
        return Result.success("删除成功");
    }

    @Operation(summary = "更新料号状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        materialService.updateStatus(id, status);
        return Result.success("状态更新成功");
    }
}
