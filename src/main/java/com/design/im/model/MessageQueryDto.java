package com.design.im.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MessageQueryDto {
    private Long fromUserId;
    private Long toUserId;
    private Long topicId;
    private Integer page;
    private Integer pageSize;
    @ApiModelProperty(hidden = true)
    private Integer offset;
}
