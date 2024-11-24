package com.wanda.utils.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public class SuccessResponse<T> {

    private Boolean status;
    private String message;
    private Explanation error;

    @JsonProperty("data")
    private Object data;

    public SuccessResponse(Boolean status, String message, List<T> data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public SuccessResponse(Boolean status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}
