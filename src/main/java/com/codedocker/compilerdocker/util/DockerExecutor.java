//package com.codedocker.compilerdocker.util;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//
//import java.io.*;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.*;
//import java.nio.file.attribute.BasicFileAttributes;
//import java.util.*;
//import java.util.concurrent.*;
//
//@Component
//public class DockerExecutor {
//    private static final Logger logger = LoggerFactory.getLogger(DockerExecutor.class);
//
//    private static final String DOCKER_IMAGES_PREFIX = "compiler-";
//    private static final String TEMP_DIR;
//
//    static {
//        // OS-aware temp dir
//        if (System.getProperty("os.name").toLowerCase().contains("win")) {
//            TEMP_DIR = System.getProperty("java.io.tmpdir") + "compiler\\";
//        } else {
//            TEMP_DIR = "/tmp/compiler/";
//        }
//        try {
//            Path tempPath = Paths.get(TEMP_DIR);
//            if (!Files.exists(tempPath)) {
//                Files.createDirectories(tempPath);
//                setDirectoryPermissions(tempPath);
//            }
//        } catch (IOException e) {
//            throw new RuntimeInitializationException("Failed to initialize temp directory", e);
//        }
//    }
//
//    private final ExecutorService executor = Executors.newCachedThreadPool();
//
//    public String executeInDocker(String language, String code, String input) throws CompilerExecutionException, IOException {
//        Path hostPath = createWorkspace();
//
//        try {
//            String filename = getFilename(language);
//            String dockerImage = DOCKER_IMAGES_PREFIX + language.toLowerCase();
//
//            writeCodeFile(hostPath, filename, code);
//            boolean hasInput = writeInputFileIfNeeded(hostPath, input);
//
//            // Prepare paths
//            String dockerVolumePath = hostPath.toAbsolutePath().toString(); // For Docker -v
//            String processBuilderDir = hostPath.toAbsolutePath().toString(); // For ProcessBuilder.directory()
//
//            if (System.getProperty("os.name").toLowerCase().contains("win")) {
//                // For Docker -v argument
//                dockerVolumePath = dockerVolumePath.replace("\\", "/");
//                if (dockerVolumePath.matches("^[A-Za-z]:/.*")) {
//                    // Convert C:/Users/... to /c/Users/...
//                    dockerVolumePath = "/" + dockerVolumePath.substring(0, 1).toLowerCase() + dockerVolumePath.substring(2);
//                }
//                // For ProcessBuilder.directory(), keep as native Windows path
//            }
//
//            ProcessBuilder pb = buildDockerProcess(dockerImage, dockerVolumePath, filename, hasInput);
//            pb.directory(new File(processBuilderDir)); // Always native path for Java
//
//            Process process = pb.start();
//
//            try (BufferedReader outputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
//                 BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
//
//                boolean completed = process.waitFor(10, TimeUnit.SECONDS);
//                if (!completed) {
//                    process.destroyForcibly();
//                    throw new CompilerExecutionException("Execution timed out after 10 seconds");
//                }
//
//                String output = readStream(outputReader);
//                String error = readStream(errorReader);
//
//                if (process.exitValue() != 0) {
//                    throw new CompilerExecutionException(error.isEmpty() ?
//                            "Process failed with exit code " + process.exitValue() :
//                            error);
//                }
//
//                return output;
//            }
//        } catch (IOException | InterruptedException e) {
//            throw new CompilerExecutionException("Execution failed: " + e.getMessage());
//        } finally {
//            cleanupWorkspace(hostPath);
//        }
//    }
//
//    private static void setDirectoryPermissions(Path path) throws IOException {
//        if (!Files.isReadable(path) || !Files.isWritable(path)) {
//            path.toFile().setReadable(true, true);
//            path.toFile().setWritable(true, true);
//        }
//    }
//
//    private Path createWorkspace() throws IOException {
//        String uuid = UUID.randomUUID().toString();
//        Path hostPath = Paths.get(TEMP_DIR, uuid);
//        Files.createDirectories(hostPath);
//        setDirectoryPermissions(hostPath);
//        return hostPath;
//    }
//
//    private void writeCodeFile(Path hostPath, String filename, String code) throws IOException {
//        Path codeFilePath = hostPath.resolve(filename);
//        Files.write(codeFilePath, code.getBytes(StandardCharsets.UTF_8));
//        setFilePermissions(codeFilePath);
//    }
//
//    private boolean writeInputFileIfNeeded(Path hostPath, String input) throws IOException {
//        if (input != null && !input.trim().isEmpty()) {
//            Path inputFilePath = hostPath.resolve("input.txt");
//            Files.write(inputFilePath, input.getBytes(StandardCharsets.UTF_8));
//            setFilePermissions(inputFilePath);
//            return true;
//        }
//        return false;
//    }
//
//    private static void setFilePermissions(Path path) throws IOException {
//        path.toFile().setReadable(true, true);
//        path.toFile().setWritable(true, true); // Always allow writable for cleanup!
//    }
//
//    private ProcessBuilder buildDockerProcess(String dockerImage, String dockerVolumePath,
//                                              String filename, boolean hasInput) {
//        List<String> cmd = new ArrayList<>(Arrays.asList(
//                "docker", "run", "--rm",
//                "--network=none",
//                "--memory=100m",
//                "--cpus=0.5",
//                "--pids-limit=100",
//                "--user=1000:1000",
//                "-v", dockerVolumePath + ":/usr/src/app/:ro",
//                "-w", "/usr/src/app",
//                dockerImage,
//                filename // Only pass the filename, not a shell command!
//        ));
//
//        ProcessBuilder pb = new ProcessBuilder(cmd);
//        pb.redirectErrorStream(false);
//        return pb;
//    }
//
//    private String getFilename(String language) {
//        switch (language.toLowerCase()) {
//            case "java": return "Main.java";
//            case "python": return "script.py";
//            case "c": return "program.c";
//            case "cpp": return "program.cpp";
//            case "javascript": return "script.js";
//            case "html": return "index.html";
//            default: throw new IllegalArgumentException("Unsupported language: " + language);
//        }
//    }
//
//    // Only pass the filename; your entrypoint script in Docker handles execution!
//    private String getExecutionCommand(String dockerImage, String filename, boolean hasInput) {
//        return filename;
//    }
//
//    private String readStream(BufferedReader reader) throws IOException {
//        StringBuilder builder = new StringBuilder();
//        char[] buffer = new char[8192];
//        int bytesRead;
//        int totalBytes = 0;
//
//        while ((bytesRead = reader.read(buffer)) != -1) {
//            if (totalBytes + bytesRead > (1024 * 1024)) {
//                throw new IOException("Output exceeds maximum allowed size");
//            }
//            builder.append(buffer, 0, bytesRead);
//            totalBytes += bytesRead;
//        }
//        return builder.toString().trim();
//    }
//
//    // Robust cleanup for Windows: set writable and retry deletion if needed
//    private void cleanupWorkspace(Path hostPath) {
//        try {
//            Files.walkFileTree(hostPath, new SimpleFileVisitor<Path>() {
//                @Override
//                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
//                    file.toFile().setWritable(true, true);
//                    int attempts = 0;
//                    while (true) {
//                        try {
//                            Files.delete(file);
//                            break;
//                        } catch (IOException e) {
//                            if (attempts++ < 5) {
//                                try { Thread.sleep(100); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
//                            } else {
//                                throw e;
//                            }
//                        }
//                    }
//                    return FileVisitResult.CONTINUE;
//                }
//
//                @Override
//                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
//                    int attempts = 0;
//                    while (true) {
//                        try {
//                            Files.delete(dir);
//                            break;
//                        } catch (IOException e) {
//                            if (attempts++ < 5) {
//                                try { Thread.sleep(100); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
//                            } else {
//                                throw e;
//                            }
//                        }
//                    }
//                    return FileVisitResult.CONTINUE;
//                }
//            });
//        } catch (IOException e) {
//            logger.error("Failed to clean up workspace: {}", hostPath, e);
//        }
//    }
//
//    public static class CompilerExecutionException extends Exception {
//        public CompilerExecutionException(String message) {
//            super(message);
//        }
//    }
//
//    public static class RuntimeInitializationException extends RuntimeException {
//        public RuntimeInitializationException(String message, Throwable cause) {
//            super(message, cause);
//        }
//    }
//}


package com.codedocker.compilerdocker.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.*;

@Component
public class DockerExecutor {
    private static final Logger logger = LoggerFactory.getLogger(DockerExecutor.class);

    private static final String DOCKER_IMAGES_PREFIX = "compiler-";
    private static final String TEMP_DIR;

    static {
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            TEMP_DIR = System.getProperty("java.io.tmpdir") + "compiler\\";
        } else {
            TEMP_DIR = "/tmp/compiler/";
        }
        try {
            Path tempPath = Paths.get(TEMP_DIR);
            if (!Files.exists(tempPath)) {
                Files.createDirectories(tempPath);
                setDirectoryPermissions(tempPath);
            }
        } catch (IOException e) {
            throw new RuntimeInitializationException("Failed to initialize temp directory", e);
        }
    }

    private final ExecutorService executor = Executors.newCachedThreadPool();

    /**
     * Executes code in a Docker container, streaming input via STDIN.
     */
    public String executeInDocker(String language, String code, String input) throws CompilerExecutionException, IOException {
        Path hostPath = createWorkspace();

        try {
            String filename = getFilename(language);
            String dockerImage = DOCKER_IMAGES_PREFIX + language.toLowerCase();

            // Write code file to workspace
            writeCodeFile(hostPath, filename, code);

            // Prepare Docker volume path
            String dockerVolumePath = hostPath.toAbsolutePath().toString();
            String processBuilderDir = hostPath.toAbsolutePath().toString();

            // Windows path fix for Docker
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                dockerVolumePath = dockerVolumePath.replace("\\", "/");
                if (dockerVolumePath.matches("^[A-Za-z]:/.*")) {
                    dockerVolumePath = "/" + dockerVolumePath.substring(0, 1).toLowerCase() + dockerVolumePath.substring(2);
                }
            }

            // Build Docker command with -i for STDIN
            ProcessBuilder pb = buildDockerProcess(dockerImage, dockerVolumePath, filename);
            pb.directory(new File(processBuilderDir));

            Process process = pb.start();

            // Stream input to Docker's STDIN
            if (input != null && !input.trim().isEmpty()) {
                try (OutputStream stdin = process.getOutputStream()) {
                    stdin.write(input.getBytes(StandardCharsets.UTF_8));
                    stdin.flush();
                }
            } else {
                process.getOutputStream().close();
            }

            // Read output and error
            String output, error;
            try (
                    BufferedReader outputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))
            ) {
                boolean completed = process.waitFor(10, TimeUnit.SECONDS);
                if (!completed) {
                    process.destroyForcibly();
                    throw new CompilerExecutionException("Execution timed out after 10 seconds");
                }

                output = readStream(outputReader);
                error = readStream(errorReader);

                if (process.exitValue() != 0) {
                    throw new CompilerExecutionException(error.isEmpty() ?
                            "Process failed with exit code " + process.exitValue() :
                            error);
                }
            }

            return output;
        } catch (IOException | InterruptedException e) {
            throw new CompilerExecutionException("Execution failed: " + e.getMessage());
        } finally {
            cleanupWorkspace(hostPath);
        }
    }

    private static void setDirectoryPermissions(Path path) throws IOException {
        if (!Files.isReadable(path) || !Files.isWritable(path)) {
            path.toFile().setReadable(true, true);
            path.toFile().setWritable(true, true);
        }
    }

    private Path createWorkspace() throws IOException {
        String uuid = UUID.randomUUID().toString();
        Path hostPath = Paths.get(TEMP_DIR, uuid);
        Files.createDirectories(hostPath);
        setDirectoryPermissions(hostPath);
        return hostPath;
    }

    private void writeCodeFile(Path hostPath, String filename, String code) throws IOException {
        Path codeFilePath = hostPath.resolve(filename);
        Files.write(codeFilePath, code.getBytes(StandardCharsets.UTF_8));
        setFilePermissions(codeFilePath);
    }

    private static void setFilePermissions(Path path) throws IOException {
        path.toFile().setReadable(true, true);
        path.toFile().setWritable(true, true);
    }

    /**
     * Builds the Docker command with -i for STDIN.
     */
    private ProcessBuilder buildDockerProcess(String dockerImage, String dockerVolumePath, String filename) {
        List<String> cmd = new ArrayList<>(Arrays.asList(
                "docker", "run", "--rm", "-i", // -i for interactive STDIN
                "--network=none",
                "--memory=100m",
                "--cpus=0.5",
                "--pids-limit=100",
                "--user=1000:1000",
                "-v", dockerVolumePath + ":/usr/src/app/:ro",
                "-w", "/usr/src/app",
                dockerImage,
                filename // Entrypoint script in Docker should handle execution and read from STDIN
        ));

        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(false);
        return pb;
    }

    private String getFilename(String language) {
        switch (language.toLowerCase()) {
            case "java": return "Main.java";
            case "python": return "script.py";
            case "c": return "program.c";
            case "cpp": return "program.cpp";
            case "javascript": return "script.js";
            case "html": return "index.html";
            default: throw new IllegalArgumentException("Unsupported language: " + language);
        }
    }

    private String readStream(BufferedReader reader) throws IOException {
        StringBuilder builder = new StringBuilder();
        char[] buffer = new char[8192];
        int bytesRead;
        int totalBytes = 0;

        while ((bytesRead = reader.read(buffer)) != -1) {
            if (totalBytes + bytesRead > (1024 * 1024)) {
                throw new IOException("Output exceeds maximum allowed size");
            }
            builder.append(buffer, 0, bytesRead);
            totalBytes += bytesRead;
        }
        return builder.toString().trim();
    }

    // Robust cleanup for Windows: set writable and retry deletion if needed
    private void cleanupWorkspace(Path hostPath) {
        try {
            Files.walkFileTree(hostPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    file.toFile().setWritable(true, true);
                    int attempts = 0;
                    while (true) {
                        try {
                            Files.delete(file);
                            break;
                        } catch (IOException e) {
                            if (attempts++ < 5) {
                                try { Thread.sleep(100); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
                            } else {
                                throw e;
                            }
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    int attempts = 0;
                    while (true) {
                        try {
                            Files.delete(dir);
                            break;
                        } catch (IOException e) {
                            if (attempts++ < 5) {
                                try { Thread.sleep(100); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
                            } else {
                                throw e;
                            }
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            logger.error("Failed to clean up workspace: {}", hostPath, e);
        }
    }

    public static class CompilerExecutionException extends Exception {
        public CompilerExecutionException(String message) {
            super(message);
        }
    }

    public static class RuntimeInitializationException extends RuntimeException {
        public RuntimeInitializationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
