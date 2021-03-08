package com.design.im.model;

import lombok.Data;

import java.util.List;

/**
 * interface of app -> server
 */
@Data
public class IMMessage {
    private Long toUserId;
    private Long fromUserId;
    private Long topicId;
    private boolean isGroupMessage;
    private String msgContent;
    private Long time;
    private List<Long> atUserIds;
}
