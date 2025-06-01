package com.codedocker.compilerdocker.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class CodeExecutionService {

    public String executeCode(MultipartFile file, String language) throws IOException {
        File tempDir = Files.createTempDirectory("code").toFile();
        File codeFile = new File(tempDir, file.getOriginalFilename());
        file.transferTo(codeFile);

        String filename = codeFile.getName();
        String baseName = filename.contains(".") ? filename.substring(0, filename.lastIndexOf('.')) : filename;

        List<String> compileCommand = new ArrayList<>();
        List<String> runCommand = new ArrayList<>();
        String extension = getExtension(filename);
        Process process;

        try {
            switch (language.toLowerCase()) {
                case "java":
                    compileCommand.add("javac");
                    compileCommand.add(filename);
                    executeProcess(compileCommand, tempDir);

                    runCommand.add("java");
                    runCommand.add(baseName);
                    process = executeProcess(runCommand, tempDir);
                    break;

                case "python":
                    runCommand.add("python3");
                    runCommand.add(filename);
                    process = executeProcess(runCommand, tempDir);
                    break;

                case "c":
                    compileCommand.add("gcc");
                    compileCommand.add(filename);
                    compileCommand.add("-o");
                    compileCommand.add("output");
                    executeProcess(compileCommand, tempDir);

                    runCommand.add("./output");
                    process = executeProcess(runCommand, tempDir);
                    break;

                case "cpp":
                    compileCommand.add("g++");
                    compileCommand.add(filename);
                    compileCommand.add("-o");
                    compileCommand.add("output");
                    executeProcess(compileCommand, tempDir);

                    runCommand.add("./output");
                    process = executeProcess(runCommand, tempDir);
                    break;

                case "html":
                case "css":
                case "javascript":
                case "html/css/js":
                    return new String(Files.readAllBytes(codeFile.toPath())); // Return file content directly

                default:
                    return "❌ Unsupported language: " + language;
            }

            return readOutput(process);

        } catch (Exception e) {
            return "❌ Error: " + e.getMessage();
        } finally {
            cleanup(tempDir);
        }
    }

    private Process executeProcess(List<String> command, File directory) throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder(command);
        builder.directory(directory);
        builder.redirectErrorStream(true);
        Process process = builder.start();

        boolean finished = process.waitFor(3, TimeUnit.SECONDS);
        if (!finished) {
            process.destroyForcibly();
            throw new RuntimeException("❌ Process timed out!");
        }
        return process;
    }

    private String readOutput(Process process) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            return output.toString();
        }
    }

    private void cleanup(File dir) {
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            file.delete();
        }
        dir.delete();
    }

    private String getExtension(String filename) {
        int index = filename.lastIndexOf('.');
        return index > 0 ? filename.substring(index + 1) : "";
    }
}







// package com.codedocker.compilerdocker.service;

// import com.codedocker.compilerdocker.model.CodeRequest;
// import com.codedocker.compilerdocker.model.CodeResponse;
// import com.codedocker.compilerdocker.util.DockerExecutor;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

// @Service
// public class CodeExecutionService {

//     private static final Logger logger = LoggerFactory.getLogger(CodeExecutionService.class);

//     @Autowired
//     private DockerExecutor dockerExecutor;

//     public CodeResponse executeCode(CodeRequest codeRequest) {
//         long startTime = System.currentTimeMillis();

//         // Input validation
//         if (codeRequest == null || codeRequest.getCode() == null || codeRequest.getLanguage() == null) {
//             logger.warn("Invalid code execution request: {}", codeRequest);
//             return new CodeResponse("", "Invalid request: code and language are required.", 0);
//         }

//         try {
//             // Log the request for debugging
//             logger.info("Executing code in language: {}", codeRequest.getLanguage());

//             // Use the input (stdin) field, which is now compatible with both 'input' and 'stdin'
//             String output = dockerExecutor.executeInDocker(
//                     codeRequest.getLanguage(),
//                     codeRequest.getCode(),
//                     codeRequest.getInput()
//             );

//             long executionTime = System.currentTimeMillis() - startTime;
//             return new CodeResponse(output, "", executionTime);

//         } catch (Exception e) {
//             long executionTime = System.currentTimeMillis() - startTime;
//             logger.error("Code execution failed: {}", e.getMessage(), e);
//             return new CodeResponse("", "Execution error: " + e.getMessage(), executionTime);
//         }
//     }
// }
