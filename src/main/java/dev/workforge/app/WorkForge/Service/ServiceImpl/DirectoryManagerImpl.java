package dev.workforge.app.WorkForge.Service.ServiceImpl;

import dev.workforge.app.WorkForge.Service.Other.DirectoryManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class DirectoryManagerImpl implements DirectoryManager {

    @Value("${path.to.attachments}")
    private String basePath;

    public Path createProjectDirectory(String projectName, String taskId, String fileName) throws IOException {

       Path projectPath = Paths.get(basePath, projectName).normalize();
       Path taskPath = projectPath.resolve(taskId).normalize();

       if (!projectPath.startsWith(basePath) || !taskPath.startsWith(basePath)) {
           throw new SecurityException("Directory traversal attempt detected");
       }

        if (Files.exists(projectPath)) {
            System.out.println("Project directory already exists: " + projectPath);
        } else {
            Files.createDirectories(projectPath);
            System.out.println("Project directory created: " + projectPath);
        }

        if (Files.exists(taskPath)) {
            System.out.println("Task directory already exists: " + taskPath);
        } else {
            Files.createDirectories(taskPath);
            System.out.println("Task directory created: " + taskPath);
        }

        return Paths.get(basePath, projectName, taskId, fileName).normalize();
    }

}
