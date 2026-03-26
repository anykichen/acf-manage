package com.acf.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 来料管理Service接口
 */
public interface InboundService {

    /**
     * 扫码识别来料
     * @param barcode 条码
     * @return 识别结果
     */
    java.util.Map<String, Object> scanBarcode(String barcode);

    /**
     * 确认来料并生成LOT号
     * @param materialCode 料号
     * @param quantity 数量
     * @param operator 操作人
     * @return LOT号
     */
    String confirmInbound(String materialCode, Integer quantity, String operator);

    /**
     * 生成标签数据
     * @param lotNumber LOT号
     * @return 标签数据
     */
    java.util.Map<String, Object> generateLabelData(String lotNumber);

    /**
     * 分页查询来料记录
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param materialCode 料号
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 分页结果
     */
    Page<java.util.Map<String, Object>> queryInboundPage(int pageNum, int pageSize, String materialCode,
                                                         java.time.LocalDateTime startTime, java.time.LocalDateTime endTime);
}
