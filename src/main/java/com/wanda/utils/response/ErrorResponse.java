package com.wanda.utils.response;


import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ErrorResponse {
    private Boolean status;
    private String message;
    private Explanation error;
    private List<Object> data = new ArrayList<>();

    public ErrorResponse(Boolean status,  String explanation) {
        this.status = status;
        this.message = "Something went wrong";
        this.error = new Explanation(explanation);
    }


}


@Data
class Explanation{
    private String explanation;

    public Explanation(String explanation){
        this.explanation = explanation;
    }


}