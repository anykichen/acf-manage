package com.acf.mapper;

import com.acf.entity.AcfTransaction;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 出入库记录Mapper
 *
 * @author ACF Team
 */
@Mapper
public interface AcfTransactionMapper extends BaseMapper<AcfTransaction> {

    /**
     * 根据LOT号查询交易记录
     */
    @Select("SELECT * FROM acf_transaction " +
            "WHERE lot_number = #{lotNumber} " +
            "AND deleted = 0 " +
            "ORDER BY transaction_time DESC")
    List<AcfTransaction> getByLotNumber(String lotNumber);

    /**
     * 查询指定LOT号的最后发料时间
     */
    @Select("SELECT transaction_time FROM acf_transaction " +
            "WHERE lot_number = #{lotNumber} " +
            "AND transaction_type = 'OUTBOUND' " +
            "AND deleted = 0 " +
            "ORDER BY transaction_time DESC " +
            "LIMIT 1")
    LocalDateTime getLastOutboundTime(String lotNumber);
}
