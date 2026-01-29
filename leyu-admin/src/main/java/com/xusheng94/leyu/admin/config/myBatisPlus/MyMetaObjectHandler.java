package com.xusheng94.leyu.admin.config.myBatisPlus;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        OffsetDateTime now = OffsetDateTime.now();
        // 无论是否传值，都覆盖
        this.setFieldValByName("createdAt", now, metaObject);
        this.setFieldValByName("updatedAt", now, metaObject);
        this.setFieldValByName("deleted", false, metaObject);

        if (metaObject.hasGetter("id") && metaObject.getValue("id") != null) {
            log.warn(
                    "Insert entity with preset ID detected, id={}, class={}",
                    metaObject.getValue("id"),
                    metaObject.getOriginalObject().getClass().getSimpleName()
            );
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        OffsetDateTime now = OffsetDateTime.now();
        // 无论是否传值，都覆盖
        this.setFieldValByName("updatedAt", now, metaObject);
    }
}
