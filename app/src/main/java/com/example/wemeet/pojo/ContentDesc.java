package com.example.wemeet.pojo;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author xieziwei99
 * 2019-07-13
 * 基本数据结构，包含文字、图片、视频
 */
@Accessors(chain = true)
@Getter
@Setter
//@JsonIgnoreProperties(ignoreUnknown = true)   // 使得其可以接受包含其他多余字段的json串
public class ContentDesc implements Serializable {

    private String textContent;

    // Expected BEGIN_ARRAY but was STRING at line 1 column 25126
    // Gson 解析问题，springboot处理过来是字符串，而Gson却处理为字符数组

    // 现改用与springboot相同的Jackson，故改回byte[]
    private byte[] pictureContent;

    private String videoContent;    // 存储视频文件所在路径
}
