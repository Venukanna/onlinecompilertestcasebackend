package com.codedocker.compilerdocker.service;


import com.codedocker.compilerdocker.model.CodeRequest;
import com.codedocker.compilerdocker.model.CodeResponse;
import com.codedocker.compilerdocker.util.DockerExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CodeExecutionService {

    @Autowired
    private DockerExecutor dockerExecutor;

    public CodeResponse executeCode(CodeRequest codeRequest) {
        long startTime = System.currentTimeMillis();

        try {
            String output = dockerExecutor.executeInDocker(
                    codeRequest.getLanguage(),
                    codeRequest.getCode(),
                    codeRequest.getInput()
            );

            long executionTime = System.currentTimeMillis() - startTime;
            return new CodeResponse(output, "", executionTime);
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            return new CodeResponse("", e.getMessage(), executionTime);
        }
    }
}