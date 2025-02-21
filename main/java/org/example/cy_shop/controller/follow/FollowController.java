package org.example.cy_shop.controller.follow;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.cy_shop.dto.request.follow.FollowRequest;
import org.example.cy_shop.service.IFollowService;
import org.example.cy_shop.utils.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "00. Follow")
@RequestMapping(value = Const.PREFIX_VERSION )
public class FollowController {
    @Autowired
    private IFollowService followService;
    @PostMapping("/follow")
    public ResponseEntity<?> follow(@RequestBody FollowRequest followRequest) {
        return followService.follow(followRequest);
    }
}
