package br.com.fiap.api_rest.dto.Response;

public class AuthResponse {
    private String type;
    private String token;

    public AuthResponse() { }
    public AuthResponse(String type, String token) {
        this.type = type;
        this.token = token;
    }

    public static AuthResponse bearer(String token) {
        return new AuthResponse("Bearer", token);
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
