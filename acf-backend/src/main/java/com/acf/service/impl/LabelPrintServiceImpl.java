package com.acf.service.impl;

import com.acf.service.LabelPrintService;
import com.acf.service.QrCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 * 标签打印Service实现类
 */
@Service
public class LabelPrintServiceImpl implements LabelPrintService {

    @Autowired
    private QrCodeService qrCodeService;

    // 标签尺寸：5cm × 10cm
    // ZPL打印机：8点/毫米 = 203DPI
    // 5cm = 50mm = 400点
    // 10cm = 100mm = 800点
    private static final int LABEL_WIDTH_DOTS = 400;
    private static final int LABEL_HEIGHT_DOTS = 800;

    @Override
    public String generateZPL(Map<String, Object> labelData) {
        StringBuilder zpl = new StringBuilder();

        // ZPL起始
        zpl.append("^XA\n"); // 开始标签格式

        // 设置标签尺寸
        zpl.append("^PW").append(LABEL_WIDTH_DOTS).append("\n"); // 标签宽度
        zpl.append("^LL").append(LABEL_HEIGHT_DOTS).append("\n"); // 标签长度

        // 设置编码
        zpl.append("^CI28\n"); // UTF-8编码

        // 打印二维码
        String lotNumber = (String) labelData.get("lotNumber");
        zpl.append("^FO").append(LABEL_WIDTH_DOTS / 2).append(",50\n"); // 位置
        zpl.append("^BQN,2,8\n"); // 二维码
        zpl.append("^FDQA,").append(lotNumber).append("^FS\n"); // 二维码内容

        // 打印LOT号文本
        zpl.append("^FO").append(LABEL_WIDTH_DOTS / 2).append(",200\n"); // 位置
        zpl.append("^A0N,50,50\n"); // 字体大小
        zpl.append("^FCCENTER LOT: ").append(lotNumber).append("^FS\n"); // 文本

        // 打印料号
        String materialCode = (String) labelData.get("materialCode");
        zpl.append("^FO").append(LABEL_WIDTH_DOTS / 2).append(",280\n"); // 位置
        zpl.append("^A0N,30,30\n"); // 字体大小
        zpl.append("^FCCENTER Material: ").append(materialCode).append("^FS\n"); // 文本

        // 打印物料名称
        String materialName = (String) labelData.get("materialName");
        zpl.append("^FO").append(LABEL_WIDTH_DOTS / 2).append(",350\n"); // 位置
        zpl.append("^A0N,30,30\n"); // 字体大小
        zpl.append("^FCCENTER ").append(materialName).append("^FS\n"); // 文本

        // 打印入库日期
        String inStockDate = (String) labelData.get("inStockDate");
        zpl.append("^FO50,420\n"); // 位置
        zpl.append("^A0N,25,25\n"); // 字体大小
        zpl.append("^FDIn Date: ").append(inStockDate).append("^FS\n"); // 文本

        // 打印有效期
        String expireDate = (String) labelData.get("expireDate");
        zpl.append("^FO50,470\n"); // 位置
        zpl.append("^A0N,25,25\n"); // 字体大小
        zpl.append("^FDExpire: ").append(expireDate).append("^FS\n"); // 文本

        // 打印保存期限
        Integer shelfLifeMonths = (Integer) labelData.get("shelfLifeMonths");
        zpl.append("^FO50,520\n"); // 位置
        zpl.append("^A0N,25,25\n"); // 字体大小
        zpl.append("^FDSelf Life: ").append(shelfLifeMonths).append(" months^FS\n"); // 文本

        // 打印最大使用次数
        Integer maxUsageTimes = (Integer) labelData.get("maxUsageTimes");
        zpl.append("^FO50,570\n"); // 位置
        zpl.append("^A0N,25,25\n"); // 字体大小
        zpl.append("^FDMax Usage: ").append(maxUsageTimes).append(" times^FS\n"); // 文本

        // 使用记录标题
        zpl.append("^FO50,620\n"); // 位置
        zpl.append("^A0N,20,20\n"); // 字体大小
        zpl.append("^FDUsage Record:^FS\n"); // 文本

        // ZPL结束
        zpl.append("^XZ\n"); // 结束标签格式

        return zpl.toString();
    }

    @Override
    public String generateEPL(Map<String, Object> labelData) {
        // EPL指令生成（简化版）
        StringBuilder epl = new StringBuilder();

        // EPL起始
        epl.append("\n"); // 起始符

        // 打印二维码
        String lotNumber = (String) labelData.get("lotNumber");
        epl.append("Q280,26\n"); // 二维码密度
        epl.append("B100,50,Q,3,8,7,\"").append(lotNumber).append("\"\n"); // 二维码

        // 打印文本
        epl.append("A100,250,0,4,1,1,N,\"LOT: ").append(lotNumber).append("\"\n");

        String materialCode = (String) labelData.get("materialCode");
        epl.append("A100,300,0,3,1,1,N,\"Material: ").append(materialCode).append("\"\n");

        String materialName = (String) labelData.get("materialName");
        epl.append("A100,350,0,3,1,1,N,\"").append(materialName).append("\"\n");

        String inStockDate = (String) labelData.get("inStockDate");
        epl.append("A50,420,0,2,1,1,N,\"In Date: ").append(inStockDate).append("\"\n");

        String expireDate = (String) labelData.get("expireDate");
        epl.append("A50,470,0,2,1,1,N,\"Expire: ").append(expireDate).append("\"\n");

        // 打印结束
        epl.append("P1\n"); // 打印1份

        return epl.toString();
    }

    @Override
    public Map<String, Object> printLabel(String zpl, String printerIp, Integer printerPort) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 实际生产环境中，这里应该通过Socket连接打印机发送ZPL指令
            // 这里仅做模拟返回

            result.put("success", true);
            result.put("message", "打印指令已发送");
            result.put("zpl", zpl);

            if (printerIp != null) {
                result.put("printerIp", printerIp);
                if (printerPort != null) {
                    result.put("printerPort", printerPort);
                }
            }

            // TODO: 实际打印逻辑
            /*
            try (Socket socket = new Socket(printerIp, printerPort != null ? printerPort : 9100);
                 OutputStream out = socket.getOutputStream()) {
                out.write(zpl.getBytes("UTF-8"));
                out.flush();
            }
            */

            return result;

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "打印失败: " + e.getMessage());
            return result;
        }
    }

    @Override
    public String previewLabel(Map<String, Object> labelData) throws Exception {
        // 创建标签图像
        int width = 280;  // 5cm × 56px = 280px
        int height = 560; // 10cm × 56px = 560px

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // 设置抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 白色背景
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);

        // 黑色文字
        g2d.setColor(Color.BLACK);

        // 打印边框
        g2d.drawRect(2, 2, width - 4, height - 4);

        // 打印二维码
        String qrCode = (String) labelData.get("qrCode");
        if (qrCode != null && qrCode.startsWith("data:image/png;base64,")) {
            // TODO: 解析Base64图片并绘制
        }

        // 打印LOT号
        String lotNumber = (String) labelData.get("lotNumber");
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth("LOT: " + lotNumber);
        g2d.drawString("LOT: " + lotNumber, (width - textWidth) / 2, 200);

        // 打印料号
        String materialCode = (String) labelData.get("materialCode");
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        fm = g2d.getFontMetrics();
        textWidth = fm.stringWidth("Material: " + materialCode);
        g2d.drawString("Material: " + materialCode, (width - textWidth) / 2, 240);

        // 打印物料名称
        String materialName = (String) labelData.get("materialName");
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        fm = g2d.getFontMetrics();
        textWidth = fm.stringWidth(materialName);
        g2d.drawString(materialName, (width - textWidth) / 2, 270);

        // 打印入库日期
        String inStockDate = (String) labelData.get("inStockDate");
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        g2d.drawString("In Date: " + inStockDate, 20, 320);

        // 打印有效期
        String expireDate = (String) labelData.get("expireDate");
        g2d.drawString("Expire: " + expireDate, 20, 350);

        // 保存为Base64
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", outputStream);
        byte[] imageBytes = outputStream.toByteArray();

        g2d.dispose();

        return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);
    }
}
