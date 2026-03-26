package com.acf.service.impl;

import com.acf.common.BusinessException;
import com.acf.common.ResultCode;
import com.acf.entity.AcfMaterial;
import com.acf.mapper.AcfMaterialMapper;
import com.acf.service.AcfMaterialService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 料号Service实现类
 *
 * @author ACF Team
 */
@Service
public class AcfMaterialServiceImpl extends ServiceImpl<AcfMaterialMapper, AcfMaterial> implements AcfMaterialService {

    @Override
    public AcfMaterial getByMaterialCode(String materialCode) {
        return getOne(new LambdaQueryWrapper<AcfMaterial>()
                .eq(AcfMaterial::getMaterialCode, materialCode)
                .eq(AcfMaterial::getDeleted, 0));
    }

    @Override
    public boolean batchDelete(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return true;
        }
        return update().set("deleted", 1)
                .set("update_time", LocalDateTime.now())
                .in("id", ids)
                .update();
    }

    @Override
    public boolean updateStatus(Long id, Integer status) {
        AcfMaterial material = getById(id);
        if (material == null) {
            throw new BusinessException(ResultCode.MATERIAL_NOT_EXISTS);
        }
        material.setStatus(status);
        material.setUpdateTime(LocalDateTime.now());
        return updateById(material);
    }
}
