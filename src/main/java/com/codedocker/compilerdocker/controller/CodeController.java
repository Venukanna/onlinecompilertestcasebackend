package com.codedocker.compilerdocker.controller;


import com.codedocker.compilerdocker.model.CodeRequest;
import com.codedocker.compilerdocker.model.CodeResponse;
import com.codedocker.compilerdocker.service.CodeExecutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/execute")
public class CodeController {

    @Autowired
    private CodeExecutionService codeExecutionService;

    @PostMapping
    public CodeResponse executeCode(@RequestBody CodeRequest codeRequest) {
        return codeExecutionService.executeCode(codeRequest);
    }

    @GetMapping("/languages")
    public String[] getSupportedLanguages() {
        return new String[]{"java", "python", "c", "cpp", "javascript", "html"};
    }
}