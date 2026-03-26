package com.acf.mapper;

import com.acf.entity.AcfTransaction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 出入库记录Mapper扩展方法
 */
@Mapper
public interface AcfTransactionMapperCustomEnhanced {

    /**
     * 查询最后一次发料记录
     */
    @Select("SELECT * FROM acf_transaction " +
            "WHERE lot_number = #{lotNumber} " +
            "AND transaction_type = 'OUTBOUND' " +
            "ORDER BY transaction_time DESC " +
            "LIMIT 1")
    AcfTransaction selectLastOutbound(@Param("lotNumber") String lotNumber);

    /**
     * 查询LOT的所有交易记录
     */
    @Select("SELECT * FROM acf_transaction " +
            "WHERE lot_number = #{lotNumber} " +
            "ORDER BY transaction_time ASC")
    java.util.List<AcfTransaction> selectByLotNumber(@Param("lotNumber") String lotNumber);

    /**
     * 统计LOT的交易次数
     */
    @Select("SELECT transaction_type, COUNT(*) as count " +
            "FROM acf_transaction " +
            "WHERE lot_number = #{lotNumber} " +
            "GROUP BY transaction_type")
    java.util.List<java.util.Map<String, Object>> countByType(@Param("lotNumber") String lotNumber);
}
