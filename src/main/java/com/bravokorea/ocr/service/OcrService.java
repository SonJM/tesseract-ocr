package com.bravokorea.ocr.service;

import com.bravokorea.ocr.util.ImageUtils;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class OcrService {

    private static final Logger log = LoggerFactory.getLogger(OcrService.class);
    private final Tesseract tesseract;

    public OcrService() {
        this.tesseract = new Tesseract();
        
        try {
            // 원본 데이터 파일 확인
            Path originalDataPath = new File("tessdata").getAbsoluteFile().toPath();
            File originalFile = originalDataPath.resolve("eng.traineddata").toFile();

            if (!originalFile.exists()) {
                throw new RuntimeException("원본 TessData 파일을 찾을 수 없습니다: " + originalFile.getAbsolutePath());
            }

            // 임시 폴더 준비
            // Windows Tesseract 버그 회피: 한글 경로 인식 불가 문제를 해결하기 위해 Temp로 복사
            String tempDir = System.getProperty("java.io.tmpdir");
            Path targetDir = Path.of(tempDir, "tessdata_cache");
            
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }

            // 파일 복사
            Path targetFile = targetDir.resolve("eng.traineddata");
            log.info("한글 경로 이슈 우회: 데이터 파일을 임시 폴더로 복사합니다.\n -> From: {}\n -> To: {}", originalFile, targetFile);
            
            Files.copy(originalFile.toPath(), targetFile, StandardCopyOption.REPLACE_EXISTING);

            // 복사된 경로(영문)로 설정
            String finalDataPath = targetDir.toString();
            this.tesseract.setDatapath(finalDataPath);
            this.tesseract.setLanguage("eng");
            this.tesseract.setOcrEngineMode(1); // LSTM Only
            
            log.info("Tesseract 초기화 성공 (Data Path: {})", finalDataPath);

        } catch (IOException e) {
            throw new RuntimeException("TessData 임시 파일 복사 실패", e);
        }
    }

    public String extractText(Path imagePath) {
        File imageFile = imagePath.toFile();
        if (!imageFile.exists()) {
            throw new IllegalArgumentException("파일을 찾을 수 없습니다: " + imagePath);
        }

        try {
            log.info("이미지 로딩 중: {}", imagePath);
            BufferedImage originalImage = ImageIO.read(imageFile);
            
            log.info("이미지 전처리 수행 중 (Upscaling & Grayscale)...");
            BufferedImage processedImage = ImageUtils.preprocess(originalImage);

            log.info("텍스트 추출 시작...");
	        return tesseract.doOCR(processedImage);

        } catch (IOException e) {
            log.error("이미지 파일 읽기 실패", e);
            throw new RuntimeException("이미지 처리 중 오류 발생", e);
        } catch (TesseractException e) {
            log.error("OCR 수행 실패", e);
            throw new RuntimeException("OCR 엔진 오류", e);
        }
    }
}
