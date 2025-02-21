package org.example.cy_shop.controller.user;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.cy_shop.dto.ApiResponse;
import org.example.cy_shop.dto.request.address.AddressAccountRequest;
import org.example.cy_shop.dto.response.address.AddressAccountResponse;
import org.example.cy_shop.service.IAddressAccountService;
import org.example.cy_shop.utils.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "07.AddressAccount")
@RestController
@RequestMapping(value = Const.PREFIX_VERSION + "/address")
public class AddressAccountController {
    @Autowired
    private IAddressAccountService addressAccountService;
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<String>> createAddress(@RequestBody AddressAccountRequest addressAccountRequest) {
        ApiResponse<String> response = addressAccountService.createAddress(addressAccountRequest);
        if (response.getCode() != 200) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse<String>> updateAddress(@PathVariable Long id, @RequestBody AddressAccountRequest addressAccountRequest) {
        ApiResponse<String> response = addressAccountService.updateAddress(id, addressAccountRequest);
        if (response.getCode() != 200) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<AddressAccountResponse>>> getAllAddress(@RequestParam(defaultValue = "0") int page,
                                                                                   @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        ApiResponse<List<AddressAccountResponse>> response = addressAccountService.getAllAddress(pageable);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AddressAccountResponse>> getAddress(@PathVariable Long id) {
        ApiResponse<AddressAccountResponse> response = addressAccountService.getAddressById(id);

        if (response.getCode() == HttpStatus.NOT_FOUND.value()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        if (response.getData() == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse<String>> deleteAddress(@PathVariable Long id) {
        ApiResponse<String> response = addressAccountService.deleteAddress(id);
        if (response.getCode() != 200) {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
