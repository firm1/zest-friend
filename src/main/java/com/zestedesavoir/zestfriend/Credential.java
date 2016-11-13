package com.zestedesavoir.zestfriend;

public class Credential {
    private String accessToken;
    private String tokenType;
    private int expiresIn;
    private String refreshToken;
    private String scope;

    public Credential(String accessToken, String tokenType, int expiresIn, String refreshToken, String scope) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.refreshToken = refreshToken;
        this.scope = scope;
    }

    public String getAccessToken() {
        return "Bearer "+accessToken;
    }

    @Override
    public String toString() {
        return "Credential{" +
                "accessToken='" + accessToken + '\'' +
                ", tokenType='" + tokenType + '\'' +
                ", expiresIn=" + expiresIn +
                ", refreshToken='" + refreshToken + '\'' +
                ", scope='" + scope + '\'' +
                '}';
    }
}
