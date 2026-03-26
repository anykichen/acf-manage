package com.acf.mapper;

import com.acf.entity.AcfTransaction;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 出入库记录Mapper（自定义方法）
 *
 * @author ACF Team
 */
@Mapper
public interface AcfTransactionMapperCustom extends BaseMapper<AcfTransaction> {

    /**
     * 插入交易记录
     */
    @Insert("INSERT INTO acf_transaction (transaction_no, lot_number, transaction_type, quantity, " +
            "warehouse_location, operator_id, operator_name, warmup_start_time, warmup_end_time, " +
            "warmup_duration, remark, transaction_time, create_time, deleted) " +
            "VALUES (#{transaction.transactionNo}, #{transaction.lotNumber}, #{transaction.transactionType}, " +
            "#{transaction.quantity}, #{transaction.warehouseLocation}, #{transaction.operatorId}, " +
            "#{transaction.operatorName}, #{transaction.warmupStartTime}, #{transaction.warmupEndTime}, " +
            "#{transaction.warmupDuration}, #{transaction.remark}, #{transaction.transactionTime}, " +
            "#{transaction.createTime}, #{transaction.deleted})")
    int insertTransaction(@Param("transaction") AcfTransaction transaction);

    /**
     * 根据LOT号查询交易记录
     */
    @Select("SELECT * FROM acf_transaction WHERE lot_number = #{lotNumber} AND deleted = 0 ORDER BY transaction_time DESC")
    List<AcfTransaction> getByLotNumber(String lotNumber);

    /**
     * 查询指定LOT号的最后发料时间
     */
    @Select("SELECT transaction_time FROM acf_transaction WHERE lot_number = #{lotNumber} " +
            "AND transaction_type = 'OUTBOUND' AND deleted = 0 ORDER BY transaction_time DESC LIMIT 1")
    LocalDateTime getLastOutboundTime(String lotNumber);
}
