package com.example.wemeet.pojo;

import com.example.wemeet.pojo.user.User;

import java.io.Serializable;
import java.util.Objects;

import lombok.Data;

/**
 * @author xieziwei99
 * 2019-09-14
 * 接收前端传来的数据（存入一个bug的完整信息，包括BugProperty和BugContent）
 */
@Data
//@JsonIgnoreProperties(ignoreUnknown = true)   // 使得其可以接受包含其他多余字段的json串
public class Bug implements Serializable {

    private BugProperty bugProperty;
    private Moment moment;
    private ChoiceQuestion choiceQuestion;
    private NarrativeQuestion narrativeQuestion;
    private VirusPoint virusPoint;
    private User planter;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bug bug = (Bug) o;
        return Objects.equals(bugProperty, bug.bugProperty);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bugProperty);
    }
}
