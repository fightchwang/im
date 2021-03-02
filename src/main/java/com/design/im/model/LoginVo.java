package com.design.im.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class LoginVo implements Serializable {

    private static final long serialVersionUID = 6711396581310450023L;

    private String email;

    private String password;


    /**
     * token令牌 过期时间默认15day
     */
    @ApiModelProperty(hidden = true)
    private String token;

    /**
     * 刷新token 过期时间可以设置为jwt的两倍，甚至更长，用于动态刷新token
     */
    @ApiModelProperty(hidden = true)
    private String refreshToken;

    /**
     * token过期时间戳
     */
    @ApiModelProperty(hidden = true)
    private Long tokenPeriodTime;

    /**
     * refresh token过期时间戳
     */
    @ApiModelProperty(hidden = true)
    private Long refreshTokenPeriodTime;

}