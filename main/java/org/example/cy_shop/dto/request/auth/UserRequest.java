package org.example.cy_shop.dto.request.auth;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequest {
    private Long id;
    private String username;
    private String name;

//    private LocationUserRequest locationUsers;

    private String email;
    private String password;

    Set<String> role;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void simpleValidate(){
        if(this.username != null)
            this.username = this.username.trim();

        if(this.name != null)
            this.name = this.name;

        if(this.email != null)
            this.email = this.email.trim();

        if(this.password != null)
            this.password = this.password.trim();
    }
}
