package kwh.Petmily_BE.global.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileService {
    private static final Logger logger = LoggerFactory.getLogger(FileService.class);

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String storeFile(MultipartFile file) {
        try {
            // 폴더가 없으면 생성
            File folder = new File(uploadDir);
            if (!folder.exists()) {
                boolean created = folder.mkdirs();
                if (!created) logger.warn("Could not create upload directory: {}", uploadDir);
            }

            // 파일명 중복 방지를 위해 UUID 생성
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path targetLocation = Paths.get(uploadDir).resolve(fileName);

            // 파일 저장
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/" + fileName;
        } catch (IOException e) {
            logger.error("파일 저장 실패", e);
            throw new RuntimeException("파일 저장 실패", e);
        }
    }

    // 업로드된 파일(이미지)을 삭제합니다. imageUrl은 "/uploads/{filename}" 형태를 기대합니다.
    public void deleteFile(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) return;

        try {
            String prefix = "/uploads/";
            String fileName = imageUrl.startsWith(prefix) ? imageUrl.substring(prefix.length()) : Paths.get(imageUrl).getFileName().toString();
            Path filePath = Paths.get(uploadDir).resolve(fileName);

            boolean deleted = Files.deleteIfExists(filePath);
            if (!deleted) {
                logger.warn("파일이 존재하지 않거나 삭제되지 않았습니다: {}", filePath.toAbsolutePath());
            }
        } catch (Exception e) {
            // 삭제 실패 시 로그만 남기고 흐름을 막지 않음
            logger.warn("파일 삭제 중 오류가 발생했습니다: {}", imageUrl, e);
        }
    }
}
