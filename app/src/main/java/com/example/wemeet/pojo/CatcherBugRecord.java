package com.example.wemeet.pojo;

import com.example.wemeet.pojo.user.User;

import java.io.Serializable;
import java.util.Objects;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 捉虫用户和被捉虫子的关系表
 * @author xieziwei99
 * 2020-02-21
 */
@Data
@Accessors(chain = true)
public class CatcherBugRecord implements Serializable {

    private CatcherBugKey id;

    private User catcher;

    private BugProperty caughtBug;

    private String userAnswer;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CatcherBugRecord record = (CatcherBugRecord) o;

        return Objects.equals(id, record.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
