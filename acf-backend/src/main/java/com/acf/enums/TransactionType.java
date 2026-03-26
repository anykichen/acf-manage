package com.acf.enums;

/**
 * 交易类型枚举
 *
 * @author ACF Team
 */
public enum TransactionType {
    /**
     * 入库
     */
    INBOUND("INBOUND", "入库"),

    /**
     * 发料
     */
    OUTBOUND("OUTBOUND", "发料"),

    /**
     * 退库
     */
    RETURN("RETURN", "退库"),

    /**
     * 报废
     */
    SCRAP("SCRAP", "报废");

    private final String code;
    private final String description;

    TransactionType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
