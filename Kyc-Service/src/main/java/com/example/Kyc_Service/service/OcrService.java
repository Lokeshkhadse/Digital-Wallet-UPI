//
//package com.example.Kyc_Service.service;
//
//import com.example.Kyc_Service.exception.KycException;
//import lombok.extern.slf4j.Slf4j;
//import net.sourceforge.tess4j.ITesseract;
//import net.sourceforge.tess4j.Tesseract;
//import org.springframework.stereotype.Service;
//
//import javax.imageio.ImageIO;
//import java.awt.image.BufferedImage;
//import java.io.ByteArrayInputStream;
//import java.util.Base64;
//
//@Service
//@Slf4j
//public class OcrService {
//
//    public String extractTextFromBase64(String base64) {
//
//        try {
//
//            byte[] imageBytes =
//                    Base64.getDecoder().decode(base64);
//
//            BufferedImage image =
//                    ImageIO.read(
//                            new ByteArrayInputStream(imageBytes)
//                    );
//
//            ITesseract tesseract =
//                    new Tesseract();
//
//            tesseract.setDatapath(
//                    "C:\\Program Files\\Tesseract-OCR\\tessdata"
//            );
//
//            tesseract.setLanguage("eng");
//
//            tesseract.setPageSegMode(6);
//
//            tesseract.setOcrEngineMode(1);
//
//            String extractedText =
//                    tesseract.doOCR(image);
//
//            log.info(
//                    "OCR Extracted Text : {}",
//                    extractedText
//            );
//
//            return extractedText;
//
//        } catch (Exception ex) {
//
//            throw new KycException(
//                    "OCR Processing Failed : "
//                            + ex.getMessage()
//            );
//        }
//    }
//}

package com.example.Kyc_Service.service;

import com.example.Kyc_Service.exception.KycException;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.Base64;

@Service
@Slf4j
public class OcrService {

    public String extractTextFromBase64(String base64) {

        try {

            byte[] imageBytes =
                    Base64.getDecoder().decode(base64);

            BufferedImage image =
                    ImageIO.read(
                            new ByteArrayInputStream(imageBytes)
                    );

            image = preprocessImage(image);

            ITesseract tesseract =
                    new Tesseract();

            tesseract.setDatapath(
                    "C:\\Program Files\\Tesseract-OCR\\tessdata"
            );

            tesseract.setLanguage("eng");

            tesseract.setPageSegMode(6);

            tesseract.setOcrEngineMode(1);

            tesseract.setVariable(
                    "preserve_interword_spaces",
                    "1"
            );

            tesseract.setVariable(
                    "user_defined_dpi",
                    "300"
            );

            String extractedText =
                    tesseract.doOCR(image);

            log.info("==================================");
            log.info("OCR TEXT");
            log.info(extractedText);
            log.info("==================================");

            return extractedText;

        } catch (Exception ex) {

            throw new KycException(
                    "OCR Processing Failed : "
                            + ex.getMessage()
            );
        }
    }

    private BufferedImage preprocessImage(
            BufferedImage original) {

        int width =
                original.getWidth() * 2;

        int height =
                original.getHeight() * 2;

        BufferedImage scaled =
                new BufferedImage(
                        width,
                        height,
                        BufferedImage.TYPE_BYTE_GRAY
                );

        Graphics2D g =
                scaled.createGraphics();

        g.drawImage(
                original,
                0,
                0,
                width,
                height,
                null
        );

        g.dispose();

        return scaled;
    }
}