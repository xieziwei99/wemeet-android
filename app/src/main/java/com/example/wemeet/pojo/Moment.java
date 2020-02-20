package com.example.wemeet.pojo;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


/**
 * @author xieziwei99
 * 2019-07-13
 * 朋友圈动态
 */
@Getter
@Setter
@Accessors(chain = true)
//@JsonIgnoreProperties(ignoreUnknown = true)   // 使得其可以接受包含其他多余字段的json串
public class Moment extends BugContent implements Serializable {

    private ContentDesc contentDesc;
}
