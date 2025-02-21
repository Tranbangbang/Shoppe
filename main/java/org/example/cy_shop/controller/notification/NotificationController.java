package org.example.cy_shop.controller.notification;


import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.response.notification.NotificationResponse;
import org.example.cy_shop.enums.EnumNotificationStatus;
import org.example.cy_shop.service.INotificationService;
import org.example.cy_shop.utils.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "07. NOTIFICATION")
@RestController
@RequestMapping(value = Const.PREFIX_VERSION + "/notification")
public class NotificationController {
    @Autowired
    private INotificationService notificationService;
    @GetMapping("/get-all")
    public ApiResponse<List<NotificationResponse>> getNotification() {
        return notificationService.getNotification();
    }


    @GetMapping("/get-notification-user")
    public ApiResponse<List<NotificationResponse>> getNotificationByUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return notificationService.getNotificationByUser(pageable);
    }

    @GetMapping("/get-notification-shop")
    public ApiResponse<List<NotificationResponse>> getNotificationByShop(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return notificationService.getNotificationByShop(pageable);
    }

    @PostMapping("/{id}/mark-seen")
    public ResponseEntity<ApiResponse<Void>> markNotificationAsSeen(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.markNotificationAsSeen(id));
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getNotificationsByStatus(
            @RequestParam EnumNotificationStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
            ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(notificationService.getNotificationsByStatus(status, pageable));
    }

}
