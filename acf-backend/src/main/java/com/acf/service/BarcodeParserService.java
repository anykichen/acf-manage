package com.acf.service;

import java.util.Map;

/**
 * 条码解析Service接口
 */
public interface BarcodeParserService {

    /**
     * 解析条码
     * @param barcode 条码
     * @param rule 解析规则（正则表达式）
     * @return 解析结果Map（key为捕获组名称）
     */
    Map<String, String> parseBarcode(String barcode, String rule);

    /**
     * 根据条码匹配料号
     * @param barcode 条码
     * @return 料号
     */
    String matchMaterialByBarcode(String barcode);

    /**
     * 验证条码格式
     * @param barcode 条码
     * @param rule 解析规则
     * @return 是否匹配
     */
    boolean validateBarcode(String barcode, String rule);
}
