package dev.workforge.app.WorkForge.Service.ServiceImpl;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.io.InputStream;
import java.io.OutputStream;

@Service
public class FileServiceImpl {

    private final DirectoryManagerImpl directoryManager;

    public FileServiceImpl(DirectoryManagerImpl directoryManager) {
        this.directoryManager = directoryManager;
    }

    public Path saveFile(MultipartFile file, long taskId, String projectKey) throws IOException {
        Path path = directoryManager.createProjectDirectory(projectKey, String.valueOf(taskId), file.getName());
        uploadFile(file, path);
        return path;
    }

    private void uploadFile(MultipartFile file, Path path) throws IOException {
        try (InputStream inputStream = file.getInputStream();
             OutputStream outputStream = Files.newOutputStream(path, StandardOpenOption.CREATE)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }

    public void deleteFile(String filePath) {
        Path path = Paths.get(filePath);
        try {
            boolean deleted = Files.deleteIfExists(path);
            if (!deleted) {
                System.out.println("File not found: " + filePath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file: " + filePath, e);
        }
    }
}