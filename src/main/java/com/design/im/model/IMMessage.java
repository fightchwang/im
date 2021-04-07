package com.design.im.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * interface of app -> server
 */
@Data
public class IMMessage {
    private Long toUserId;
    @ApiModelProperty(value = "don't set it, api will override it")
    private Long fromUserId;
    @ApiModelProperty(value = "if it's group msg, set it to topicId, or else set it to 0 if chat to single person")
    private Long topicId;
    @ApiModelProperty(value = "if it's group msg, set it to true, or else set it to false")
    private Boolean groupMessage;
    private String msgContent;
    @ApiModelProperty(value = "don't set it, api will override it")
    private Long time;
    private List<Long> atUserIds;

}
