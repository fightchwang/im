package com.design.im.model;

import lombok.Data;

import java.util.List;

@Data
public class IMMessage {
    private Long toUserId;
    private Long topicId;
    private boolean isGroupMessage;
    private String msgContent;
    private Long time;
    private List<Long> atUserIds;
}
