package com.example.wemeet.pojo;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 疫情点
 * @author xieziwei99
 * 2020-03-03
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class VirusPoint extends BugContent implements Serializable {
    private String description;
}
