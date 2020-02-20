package com.example.wemeet.pojo;

import java.io.Serializable;
import java.sql.Timestamp;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author xieziwei99
 * 2019-07-13
 * 虫子所载内容
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
//@JsonIgnoreProperties(ignoreUnknown = true)   // 使得其可以接受包含其他多余字段的json串
public class BugContent implements Serializable {

    private Long bugContentId;

    /**
     * 0代表微信朋友圈的动态，1代表选择题，2代表叙述题，3代表游戏
     */
    private Integer type;

    private Timestamp publishTime;
}
