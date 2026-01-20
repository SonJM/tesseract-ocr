package com.bravokorea.ocr.util;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

/**
 * OCR 인식률 향상을 위한 이미지 전처리 유틸리티
 */
public class ImageUtils {

    private static final int SCALE_FACTOR = 3; // 3배 확대

    /**
     * 이미지를 확대하고 흑백으로 변환합니다.
     * PuTTY 스크린샷과 같은 저해상도 이미지의 인식률을 높이는 데 필수적입니다.
     */
    public static BufferedImage preprocess(BufferedImage original) {
        int newWidth = original.getWidth() * SCALE_FACTOR;
        int newHeight = original.getHeight() * SCALE_FACTOR;

        // 이미지 확대 (SCALE_SMOOTH 알고리즘 사용)
        BufferedImage resized = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g = resized.createGraphics();
        
        try {
            g.drawImage(original.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH), 0, 0, null);
        } finally {
            g.dispose();
        }

        return resized;
    }
}
