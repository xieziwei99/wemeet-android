package com.example.wemeet.pojo;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


/**
 * @author xieziwei99
 * 2019-07-13
 * 选择题，包含判断题
 */
@Getter
@Setter
@Accessors(chain = true)
//@JsonIgnoreProperties(ignoreUnknown = true)   // 使得其可以接受包含其他多余字段的json串
public class ChoiceQuestion extends BugContent implements Serializable {
    private ContentDesc question;
    private String choiceA;
    private String choiceB;
    private String choiceC;
    private String choiceD;
    private String correctAnswer;
    private Integer score;
}
