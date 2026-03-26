package com.acf.service.impl;

import com.acf.entity.AcfLabelTemplate;
import com.acf.mapper.AcfLabelTemplateMapper;
import com.acf.service.AcfLabelTemplateService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 标签模板Service实现类
 *
 * @author ACF Team
 */
@Service
public class AcfLabelTemplateServiceImpl extends ServiceImpl<AcfLabelTemplateMapper, AcfLabelTemplate>
        implements AcfLabelTemplateService {

    @Override
    public AcfLabelTemplate getDefaultTemplate() {
        return getOne(new LambdaQueryWrapper<AcfLabelTemplate>()
                .eq(AcfLabelTemplate::getIsDefault, 1)
                .eq(AcfLabelTemplate::getDeleted, 0)
                .last("LIMIT 1"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setDefaultTemplate(Long id) {
        // 先清除所有默认标记
        update(new LambdaQueryWrapper<AcfLabelTemplate>()
                .eq(AcfLabelTemplate::getDeleted, 0),
                new AcfLabelTemplate().setIsDefault(0));

        // 设置新的默认模板
        AcfLabelTemplate template = getById(id);
        if (template != null) {
            template.setIsDefault(1);
            template.setUpdateTime(LocalDateTime.now());
            updateById(template);
        }
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
