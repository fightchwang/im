package com.design.im.model;

import lombok.Data;

@Data
public class CommonResponse<T> {
    private T data;
    private String msg;
    private int code;
}
