package com.acf.mapper;

import com.acf.entity.AcfAlert;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 预警记录Mapper
 *
 * @author ACF Team
 */
@Mapper
public interface AcfAlertMapper extends BaseMapper<AcfAlert> {
}
