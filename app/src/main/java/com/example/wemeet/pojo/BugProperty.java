package com.example.wemeet.pojo;


import com.example.wemeet.pojo.user.User;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author xieziwei99
 * 2019-07-12
 * 虫子的基本属性
 */
@Data
@Accessors(chain = true)
//@JsonIgnoreProperties(ignoreUnknown = true)   // 使得其可以接受包含其他多余字段的json串
public class BugProperty implements Serializable {
    private Long bugID;

    private User planter;

    private Double startLongitude;

    private Double startLatitude;

    private boolean movable;

    private Double destLongitude;

    private Double destLatitude;

    /**
     * int代表小时
     */
    private Integer survivalTime;

    private Timestamp startTime;

    private Integer lifeCount;

    private Integer restLifeCount;

    private List<String> tags;

    private Integer temperature;

    private BugContent bugContent;

    private Message message;

//    @ToString.Exclude
//    private List<User> catchers;
    @ToString.Exclude
    private Set<CatcherBugRecord> caughtRecords;

    // 当bugID相等时，两个bug相等
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BugProperty that = (BugProperty) o;

        return bugID.equals(that.bugID);
    }

    @Override
    public int hashCode() {
        return bugID.hashCode();
    }
}
