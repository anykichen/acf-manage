package com.acf.mapper;

import com.acf.entity.AcfLotRule;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * LOT号规则Mapper接口
 */
@Mapper
public interface AcfLotRuleMapper extends BaseMapper<AcfLotRule> {

    /**
     * 查询默认规则
     */
    @Select("SELECT * FROM acf_lot_rule WHERE is_default = 1 AND status = 1 AND deleted = 0 LIMIT 1")
    AcfLotRule selectDefaultRule();

    /**
     * 查询所有启用的规则
     */
    @Select("SELECT * FROM acf_lot_rule WHERE status = 1 AND deleted = 0 ORDER BY is_default DESC, create_time DESC")
    List<AcfLotRule> selectActiveRules();

    /**
     * 根据规则名称查询
     */
    @Select("SELECT * FROM acf_lot_rule WHERE rule_name = #{ruleName} AND deleted = 0 LIMIT 1")
    AcfLotRule selectByRuleName(String ruleName);
}
