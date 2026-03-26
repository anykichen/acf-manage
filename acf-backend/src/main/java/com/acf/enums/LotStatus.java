package com.acf.enums;

/**
 * LOT状态枚举
 *
 * @author ACF Team
 */
public enum LotStatus {
    /**
     * 在库
     */
    IN_STOCK("IN_STOCK", "在库"),

    /**
     * 使用中
     */
    IN_USE("IN_USE", "使用中"),

    /**
     * 报废
     */
    SCRAPPED("SCRAPPED", "报废");

    private final String code;
    private final String description;

    LotStatus(String code, String description) {
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
