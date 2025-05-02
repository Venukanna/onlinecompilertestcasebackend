package com.codedocker.compilerdocker.model;

public class CodeResponse {
    private String output;
    private String error;
    private long executionTime;

    // Constructors, getters, and setters
    public CodeResponse() {}

    public CodeResponse(String output, String error, long executionTime) {
        this.output = output;
        this.error = error;
        this.executionTime = executionTime;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }
}