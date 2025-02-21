package org.example.cy_shop.service;

import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.request.follow.FollowRequest;
import org.springframework.http.ResponseEntity;

public interface IFollowService {
    ResponseEntity<ApiResponse<String>> follow(FollowRequest followRequest);
}
