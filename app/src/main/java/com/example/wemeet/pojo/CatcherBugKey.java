package com.example.wemeet.pojo;

import java.io.Serializable;
import java.util.Objects;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 捉虫用户和被捉虫子的关系表 的组合主键
 * @author xieziwei99
 * 2020-02-21
 */
@Data
@Accessors(chain = true)
public class CatcherBugKey implements Serializable {

    private Long userId;

    private Long bugId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CatcherBugKey key = (CatcherBugKey) o;

        if (!Objects.equals(userId, key.userId)) {
            return false;
        }
        return Objects.equals(bugId, key.bugId);
    }

    @Override
    public int hashCode() {
        int result = userId != null ? userId.hashCode() : 0;
        result = 31 * result + (bugId != null ? bugId.hashCode() : 0);
        return result;
    }
}
