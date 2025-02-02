package org.isomorphism.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @Auther isomorphism
 */
public class QrCodeUtils {

    public static BufferedImage generateQRCodeImage(String data) {
        return QrCodeUtils.generateQRCodeImage(data, 300, 300);
    }

    // 生成二维码
    public static BufferedImage generateQRCodeImage(String data, int width, int height) {
        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8"); // 设置字符编码
            hints.put(EncodeHintType.ERROR_CORRECTION, com.google.zxing.qrcode.decoder.ErrorCorrectionLevel.H); // 错误纠正级别
            hints.put(EncodeHintType.MARGIN, 1); // 二维码边距

            MultiFormatWriter writer = new MultiFormatWriter();
            BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, width, height, hints);

            // 创建 BufferedImage 并绘制二维码
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    image.setRGB(x, y, bitMatrix.get(x, y) ? Color.BLACK.getRGB() : Color.WHITE.getRGB());
                }
            }
            System.out.println("图像已写入到内存");
            return image;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

//    public static void main(String[] args) {
//        // String data = ""; // 二维码中的数据
//        String data = "Isomorphism 测试数据"; // 二维码中的数据
//        int width = 300; // 二维码的宽度
//        int height = 300; // 二维码的高度
//        String uuid = UUID.randomUUID().toString();
//        String filePath = "your/local/path" + uuid + ".png"; // 生成的二维码文件的路径
//
//        QrCodeUtils.generateQRCode(data, width, height, filePath);
//    }

}
