package org.example.cy_shop.enums;

import lombok.Getter;

@Getter
public enum EnumTokenType {
    AccessToken(0),
    RefreshToken(1),
    ForgotPasswordToken(2);

    private final int value;

    EnumTokenType(int value) {
        this.value = value;
    }
}
