package com.acf.service;

/**
 * 二维码生成Service接口
 */
public interface QrCodeService {

    /**
     * 生成二维码Base64字符串
     * @param content 二维码内容
     * @param width 宽度
     * @param height 高度
     * @return Base64字符串
     */
    String generateQrCodeBase64(String content, int width, int height) throws Exception;

    /**
     * 生成二维码并保存到文件
     * @param content 二维码内容
     * @param filePath 文件路径
     * @param width 宽度
     * @param height 高度
     */
    void generateQrCodeToFile(String content, String filePath, int width, int height) throws Exception;

    /**
     * 解析二维码
     * @param filePath 文件路径
     * @return 二维码内容
     */
    String decodeQrCode(String filePath) throws Exception;
}
