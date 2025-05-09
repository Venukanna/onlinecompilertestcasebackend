//package com.codedocker.compilerdocker.service;
//
//
//import com.codedocker.compilerdocker.model.CodeRequest;
//import com.codedocker.compilerdocker.model.CodeResponse;
//import com.codedocker.compilerdocker.util.DockerExecutor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//@Service
//public class CodeExecutionService {
//
//    @Autowired
//    private DockerExecutor dockerExecutor;
//
//    public CodeResponse executeCode(CodeRequest codeRequest) {
//        long startTime = System.currentTimeMillis();
//
//        try {
//            String output = dockerExecutor.executeInDocker(
//                    codeRequest.getLanguage(),
//                    codeRequest.getCode(),
//                    codeRequest.getInput()
//            );
//
//            long executionTime = System.currentTimeMillis() - startTime;
//            return new CodeResponse(output, "", executionTime);
//        } catch (Exception e) {
//            long executionTime = System.currentTimeMillis() - startTime;
//            return new CodeResponse("", e.getMessage(), executionTime);
//        }
//    }
//}

package com.codedocker.compilerdocker.service;

import com.codedocker.compilerdocker.model.CodeRequest;
import com.codedocker.compilerdocker.model.CodeResponse;
import com.codedocker.compilerdocker.util.DockerExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CodeExecutionService {

    private static final Logger logger = LoggerFactory.getLogger(CodeExecutionService.class);

    @Autowired
    private DockerExecutor dockerExecutor;

    public CodeResponse executeCode(CodeRequest codeRequest) {
        long startTime = System.currentTimeMillis();

        // Input validation
        if (codeRequest == null || codeRequest.getCode() == null || codeRequest.getLanguage() == null) {
            logger.warn("Invalid code execution request: {}", codeRequest);
            return new CodeResponse("", "Invalid request: code and language are required.", 0);
        }

        try {
            // Log the request for debugging
            logger.info("Executing code in language: {}", codeRequest.getLanguage());

            // Use the input (stdin) field, which is now compatible with both 'input' and 'stdin'
            String output = dockerExecutor.executeInDocker(
                    codeRequest.getLanguage(),
                    codeRequest.getCode(),
                    codeRequest.getInput()
            );

            long executionTime = System.currentTimeMillis() - startTime;
            return new CodeResponse(output, "", executionTime);

        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            logger.error("Code execution failed: {}", e.getMessage(), e);
            return new CodeResponse("", "Execution error: " + e.getMessage(), executionTime);
        }
    }
}
