package com.design.im.model;

import lombok.Data;

import java.util.List;

@Data
public class CreateTopicVo {
    private String topic;
    private List<QuestionAndAnswer> questions;

    @Data
    public static class QuestionAndAnswer{
        private String question;
        private String answer;
    }
}
