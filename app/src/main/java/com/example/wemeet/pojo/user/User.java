package com.example.wemeet.pojo.user;

import java.io.Serializable;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 代表用户的类
 *
 * @author xieziwei99
 * 2019-11-27
 */
@Data
@Accessors(chain = true)
//@JsonIgnoreProperties(ignoreUnknown = true)   // 使得其可以接受包含其他多余字段的json串
public class User implements Serializable {

    private Long id;

    private String name;

    private String password;

    private String phoneNumber;

    /**
     * 用于注册和登录
     */
    private String email;

    /**
     * 代表积分，或者货币
     */
    private Double score;

    /**
     * 代表用户等级
     */
    private Integer grade;
}
