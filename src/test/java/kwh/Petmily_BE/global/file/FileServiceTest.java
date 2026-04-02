package kwh.Petmily_BE.global.file;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class FileServiceTest {

    @Autowired
    private FileService fileService;

    @TempDir
    Path tempDir;

    @Test
    void storeAndDeleteFile() throws IOException, NoSuchFieldException, IllegalAccessException {
        // FileService의 private uploadDir 필드를 임시 디렉토리로 설정
        Field uploadDirField = FileService.class.getDeclaredField("uploadDir");
        uploadDirField.setAccessible(true);
        uploadDirField.set(fileService, tempDir.toString() + "/");

        MockMultipartFile mockFile = new MockMultipartFile("file", "test.txt", "text/plain", "hello".getBytes());

        String url = fileService.storeFile(mockFile);
        assertThat(url).startsWith("/uploads/");

        String fileName = url.replaceFirst("/uploads/", "");
        Path stored = tempDir.resolve(fileName);
        assertThat(Files.exists(stored)).isTrue();

        // delete
        fileService.deleteFile(url);
        assertThat(Files.exists(stored)).isFalse();
    }
}
