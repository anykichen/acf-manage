package com.acf.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应码枚举
 *
 * @author ACF Team
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    /**
     * 成功
     */
    SUCCESS(200, "操作成功"),

    /**
     * 失败
     */
    ERROR(500, "操作失败"),

    /**
     * 参数错误
     */
    PARAM_ERROR(400, "参数错误"),

    /**
     * 未授权
     */
    UNAUTHORIZED(401, "未授权，请先登录"),

    /**
     * 禁止访问
     */
    FORBIDDEN(403, "禁止访问"),

    /**
     * 资源不存在
     */
    NOT_FOUND(404, "资源不存在"),

    /**
     * 数据已存在
     */
    DATA_EXISTS(1001, "数据已存在"),

    /**
     * 数据不存在
     */
    DATA_NOT_EXISTS(1002, "数据不存在"),

    /**
     * LOT号已存在
     */
    LOT_EXISTS(2001, "LOT号已存在"),

    /**
     * LOT号不存在
     */
    LOT_NOT_EXISTS(2002, "LOT号不存在"),

    /**
     * LOT号已过期
     */
    LOT_EXPIRED(2003, "LOT号已过期"),

    /**
     * 使用次数超限
     */
    USAGE_EXCEEDED(2004, "使用次数超限"),

    /**
     * 库存不足
     */
    STOCK_INSUFFICIENT(2005, "库存不足"),

    /**
     * 料号已存在
     */
    MATERIAL_EXISTS(3001, "料号已存在"),

    /**
     * 料号不存在
     */
    MATERIAL_NOT_EXISTS(3002, "料号不存在"),

    /**
     * 标签模板不存在
     */
    TEMPLATE_NOT_EXISTS(4001, "标签模板不存在"),

    /**
     * 用户名或密码错误
     */
    LOGIN_ERROR(5001, "用户名或密码错误"),

    /**
     * 用户不存在
     */
    USER_NOT_EXISTS(5002, "用户不存在"),

    /**
     * 用户已禁用
     */
    USER_DISABLED(5003, "用户已禁用"),

    /**
     * Token无效或已过期
     */
    TOKEN_INVALID(5004, "Token无效或已过期");

    /**
     * 响应码
     */
    private final Integer code;

    /**
     * 响应消息
     */
    private final String message;
}
