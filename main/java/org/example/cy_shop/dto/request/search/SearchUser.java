package org.example.cy_shop.dto.request.search;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchUser {
    private String username;
    private String keysearch;
    private String role;
}
