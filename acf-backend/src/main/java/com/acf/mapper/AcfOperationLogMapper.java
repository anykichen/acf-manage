package com.acf.mapper;

import com.acf.entity.AcfOperationLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 操作日志Mapper接口
 */
@Mapper
public interface AcfOperationLogMapper extends BaseMapper<AcfOperationLog> {

    /**
     * 分页查询操作日志（按LOT号）
     */
    @Select("<script>" +
            "SELECT * FROM acf_operation_log " +
            "WHERE deleted = 0 " +
            "<if test='lotNumber != null and lotNumber != \"\"'> " +
            "AND lot_number = #{lotNumber} " +
            "</if> " +
            "<if test='operationType != null and operationType != \"\"'> " +
            "AND operation_type = #{operationType} " +
            "</if> " +
            "<if test='startTime != null'> " +
            "AND operation_time &gt;= #{startTime} " +
            "</if> " +
            "<if test='endTime != null'> " +
            "AND operation_time &lt;= #{endTime} " +
            "</if> " +
            "ORDER BY operation_time DESC " +
            "</script>")
    List<AcfOperationLog> selectLogPage(String lotNumber, String operationType,
                                         LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 查询最近的操作日志
     */
    @Select("SELECT * FROM acf_operation_log " +
            "WHERE deleted = 0 " +
            "ORDER BY operation_time DESC " +
            "LIMIT #{limit}")
    List<AcfOperationLog> selectRecentLogs(int limit);

    /**
     * 统计操作日志数量
     */
    @Select("<script>" +
            "SELECT operation_type, COUNT(*) as count FROM acf_operation_log " +
            "WHERE deleted = 0 " +
            "<if test='startTime != null'> " +
            "AND operation_time &gt;= #{startTime} " +
            "</if> " +
            "<if test='endTime != null'> " +
            "AND operation_time &lt;= #{endTime} " +
            "</if> " +
            "GROUP BY operation_type " +
            "</script>")
    List<Map<String, Object>> countByOperationType(LocalDateTime startTime, LocalDateTime endTime);
}
