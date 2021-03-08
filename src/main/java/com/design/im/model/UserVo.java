package com.design.im.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserVo {
    private String firstname;
    private String surname;
    private String password;
    private String email;
    @ApiModelProperty(hidden = true)
    private boolean onLine;
    private Long userId;
}
