package cz.cvut.felk.kbss.freeplane.server.rest.dto;

public class AuthenticationResponse {

    private String token;
    private String userRole;

    public AuthenticationResponse(String token, String userRole) {
        this.token = token;
        this.userRole = userRole;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    @Override
    public String toString() {
        return "AuthenticationResponse{" +
                "token='" + token + '\'' +
                ", userRole='" + userRole + '\'' +
                '}';
    }
}
