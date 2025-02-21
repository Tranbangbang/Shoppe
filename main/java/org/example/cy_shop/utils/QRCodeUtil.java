package org.example.cy_shop.utils;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
@Component
public class QRCodeUtil {
    public String decodeQRCode(MultipartFile qrCodeImage) throws Exception {
        try {
            BufferedImage bufferedImage = ImageIO.read(qrCodeImage.getInputStream());
            LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

            QRCodeReader reader = new QRCodeReader();
            Result result = reader.decode(bitmap);
            return result.getText();
        } catch (IOException | NotFoundException | FormatException | ChecksumException e) {
            throw new Exception("Error decoding QR code: " + e.getMessage(), e);
        }
    }
}

