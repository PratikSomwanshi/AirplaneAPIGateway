package com.wanda.utils.response;

import lombok.Data;

@Data
public class TokenResponse {
    private String access_token;

    public TokenResponse(String access_token) {
        this.access_token = access_token;
    }
}
