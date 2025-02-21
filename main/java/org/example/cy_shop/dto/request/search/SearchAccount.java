package org.example.cy_shop.dto.request.search;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchAccount {
    private String fullname;
    private String username;
    private String role;
    private Boolean isActive;
}
