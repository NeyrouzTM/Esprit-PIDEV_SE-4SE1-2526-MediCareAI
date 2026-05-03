package tn.esprit.tn.medicare_ai.dto;



import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AuthResponse {

    private String token;
    private String role;
    private String email;
    private UserInfo user;

    public AuthResponse(String token, String email, String role) {
        this.token = token;
        this.email = email;
        this.role = role;
    }

    public AuthResponse(String token, String role, String email, UserInfo user) {
        this.token = token;
        this.role = role;
        this.email = email;
        this.user = user;
    }
}