package com.xusheng94.leyu.admin.config.myBatisPlus;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.xusheng94.leyu.admin.util.CurrentUser;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        // 无论是否传值，都覆盖
        this.setFieldValByName("createdAt", now, metaObject);
        this.setFieldValByName("updatedAt", now, metaObject);
        this.setFieldValByName("deleted", false, metaObject);
        // 只有在没有传值的情况下才填充(当前字段为空才填, 当前字段已有值就不覆盖, 传入值是 null 也不会填)
        // 这种策略允许在插入时手动指定 creatorId、deptId 等字段的值（例如通过代码设置或数据库默认值），同时在未指定时自动填充当前用户信息。
        // 前端传值没有作用, 因为该字段上有@JsonProperty(access = JsonProperty.Access.READ_ONLY)
        this.fillStrategy(metaObject, "creatorId", CurrentUser.getUserId());
        this.fillStrategy(metaObject, "deptId", CurrentUser.getDeptId());

        if (metaObject.hasGetter("id") && metaObject.getValue("id") != null) {
            log.warn(
                    "Insert entity with preset ID detected, id={}, class={}",
                    metaObject.getValue("id"),
                    metaObject.getOriginalObject().getClass().getSimpleName());
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        // 无论是否传值，都覆盖
        this.setFieldValByName("updatedAt", now, metaObject);
    }
}
