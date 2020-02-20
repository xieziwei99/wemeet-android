package com.example.wemeet.pojo;


import com.example.wemeet.pojo.user.User;

import java.io.Serializable;

import lombok.Data;
import lombok.experimental.Accessors;


/**
 * @author xieziwei99
 * 2019-07-15
 * 留言版 - 只支持一个留言？
 */
@Data
@Accessors(chain = true)
//@JsonIgnoreProperties(ignoreUnknown = true)   // 使得其可以接受包含其他多余字段的json串
public class Message implements Serializable {

    private Long messageID;

    private User user;

    private ContentDesc contentDesc;
}
