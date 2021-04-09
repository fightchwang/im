package com.design.im.model;

import lombok.Data;

@Data
public class MasterInfoAndAnswer extends MasterInfo {
    private Long topicFaqId;
    private String answer;
}
