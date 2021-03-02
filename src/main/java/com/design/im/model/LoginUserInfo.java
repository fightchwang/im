package com.design.im.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "userId")
public class LoginUserInfo {
    private long userId;
    private String userName;
    private String token;
    private String refreshToken;
}
