package com.bravokorea.ocr;

import com.bravokorea.ocr.service.OcrService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;

public class App {
    private static final Logger log = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        // 대상 파일: 현재 디렉토리의 '조회-쿼리.jpg'
        String fileName = "조회-쿼리.jpg";
        Path imagePath = Paths.get(fileName).toAbsolutePath();

        log.info("=== 브라보코리아 OCR 텍스트 추출기 시작 ===");
        log.info("대상 파일: {}", imagePath);

        try {
            // 1. OCR 서비스 초기화
            // (주의: 실행 디렉토리에 'tessdata/eng.traineddata'가 존재해야 함)
            OcrService ocrService = new OcrService();

            // 2. 텍스트 추출 실행
            String extractedText = ocrService.extractText(imagePath);

            // 3. 결과 출력
            System.out.println("\n--- [추출 결과 시작] ---\n");
            System.out.println(extractedText);
            System.out.println("\n--- [추출 결과 끝] ---\n");
            
            log.info("작업이 성공적으로 완료되었습니다.");

        } catch (Exception e) {
            log.error("프로그램 실행 중 치명적인 오류 발생", e);
            System.err.println("\n[ERROR] 작업을 완료하지 못했습니다.");
            System.err.println("원인: " + e.getMessage());
            System.err.println("팁: 'tessdata' 폴더에 'eng.traineddata' 파일이 있는지 확인해주세요.");
        }
    }
}
