package com.acf.mapper;

import com.acf.entity.AcfLot;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;

/**
 * LOT号Mapper
 *
 * @author ACF Team
 */
@Mapper
public interface AcfLotMapper extends BaseMapper<AcfLot> {

    /**
     * 获取FIFO推荐的LOT号
     */
    @Select("SELECT * FROM acf_lot WHERE material_code = #{materialCode} " +
            "AND current_quantity > 0 " +
            "AND lot_status = 'IN_STOCK' " +
            "AND deleted = 0 " +
            "ORDER BY inbound_date ASC " +
            "LIMIT #{limit}")
    List<AcfLot> getFifoLots(@Param("materialCode") String materialCode, @Param("limit") int limit);

    /**
     * 获取库存预警LOT号
     */
    @Select("SELECT l.*, m.max_usage_times " +
            "FROM acf_lot l " +
            "LEFT JOIN acf_material m ON l.material_code = m.material_code " +
            "WHERE l.lot_status = 'IN_STOCK' " +
            "AND l.deleted = 0 " +
            "AND (DATEDIFF(l.expire_date, NOW()) <= 7 OR l.usage_times >= m.max_usage_times) " +
            "ORDER BY DATEDIFF(l.expire_date, NOW()) ASC")
    List<AcfLot> getAlertLots();

    /**
     * 统计料号库存
     */
    @Select("SELECT SUM(current_quantity) FROM acf_lot " +
            "WHERE material_code = #{materialCode} " +
            "AND lot_status = 'IN_STOCK' " +
            "AND deleted = 0")
    BigDecimal getStockQuantity(@Param("materialCode") String materialCode);
}
