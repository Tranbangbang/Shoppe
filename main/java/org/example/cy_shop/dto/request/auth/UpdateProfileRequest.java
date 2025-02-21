package org.example.cy_shop.dto.request.auth;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProfileRequest {
    private Long id;
    private String username;
    private String email;
    private LocalDate dob;
    private int gender;
    private String location;
    private MultipartFile file;
}
