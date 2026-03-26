package com.acf.service;

import java.util.Map;

/**
 * 标签打印Service接口
 */
public interface LabelPrintService {

    /**
     * 生成ZPL指令
     * @param labelData 标签数据
     * @return ZPL指令
     */
    String generateZPL(Map<String, Object> labelData);

    /**
     * 生成EPL指令
     * @param labelData 标签数据
     * @return EPL指令
     */
    String generateEPL(Map<String, Object> labelData);

    /**
     * 打印标签（模拟）
     * @param zpl ZPL指令
     * @param printerIp 打印机IP（可选）
     * @param printerPort 打印机端口（可选）
     * @return 打印结果
     */
    Map<String, Object> printLabel(String zpl, String printerIp, Integer printerPort);

    /**
     * 预览标签（返回Base64图片）
     * @param labelData 标签数据
     * @return Base64图片
     */
    String previewLabel(Map<String, Object> labelData) throws Exception;
}
