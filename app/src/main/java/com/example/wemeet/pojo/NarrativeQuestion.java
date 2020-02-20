package com.example.wemeet.pojo;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


/**
 * @author xieziwei99
 * 2019-07-13
 * 叙述题
 */
@Getter
@Setter
@Accessors(chain = true)
//@JsonIgnoreProperties(ignoreUnknown = true)   // 使得其可以接受包含其他多余字段的json串
public class NarrativeQuestion extends BugContent implements Serializable {

    private ContentDesc question;

    private String userAnswer;

    private String correctAnswer;

    private Integer score;
}
