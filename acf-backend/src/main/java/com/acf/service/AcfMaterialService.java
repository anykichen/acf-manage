package com.acf.service;

import com.acf.entity.AcfMaterial;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 料号Service
 *
 * @author ACF Team
 */
public interface AcfMaterialService extends IService<AcfMaterial> {

    /**
     * 根据料号查询
     */
    AcfMaterial getByMaterialCode(String materialCode);

    /**
     * 根据料号查询(别名)
     */
    AcfMaterial queryByMaterialCode(String materialCode);

    /**
     * 批量删除
     */
    boolean batchDelete(List<Long> ids);

    /**
     * 更新状态
     */
    boolean updateStatus(Long id, Integer status);
}
