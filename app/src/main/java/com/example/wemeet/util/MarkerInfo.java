package com.example.wemeet.util;

import com.example.wemeet.pojo.Bug;
import com.example.wemeet.pojo.VirusPoint;

import lombok.Data;
import lombok.experimental.Accessors;

// 用作地图上每个marker存储的对象
@Data
@Accessors(chain = true)
public class MarkerInfo {
    private Bug bug;
    private boolean caught;
    private String userAnswer;
    private VirusPoint virusPoint;
}
