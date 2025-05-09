//package com.codedocker.compilerdocker.model;
//
//public class CodeRequest {
//    private String code;
//    private String language;
//    private String input;
//
//    // Constructors, getters, and setters
//    public CodeRequest() {}
//
//    public CodeRequest(String code, String language, String input) {
//        this.code = code;
//        this.language = language;
//        this.input = input;
//    }
//
//    public String getCode() {
//        return code;
//    }
//
//    public void setCode(String code) {
//        this.code = code;
//    }
//
//    public String getLanguage() {
//        return language;
//    }
//
//    public void setLanguage(String language) {
//        this.language = language;
//    }
//
//    public String getInput() {
//        return input;
//    }
//
//    public void setInput(String input) {
//        this.input = input;
//    }
//}

package com.codedocker.compilerdocker.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CodeRequest {

    private String code;


    private String language;


    @JsonProperty(value = "input")
    private String input;

    @JsonProperty("stdin")
    public void setStdin(String stdin) {
        this.input = stdin;
    }

    @JsonProperty("stdin")
    public String getStdin() {
        return input;
    }
}
