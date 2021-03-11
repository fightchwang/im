package com.design.im.model;

import lombok.Data;

import java.util.List;

@Data
public class PullMsgVo<T> {
    private List<T> msg;
    private int page;
    private int pageSize;
    //total number of records
    private long total;
}
