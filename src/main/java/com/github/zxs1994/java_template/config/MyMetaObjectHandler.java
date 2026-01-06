package com.example.template.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.example.template.util.TimeProvider;

import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        OffsetDateTime now = TimeProvider.now();
        // 无论是否传值，都覆盖
        this.setFieldValByName("createdAt", now, metaObject);
        this.setFieldValByName("updatedAt", now, metaObject);
        this.setFieldValByName("deleted", false, metaObject);

        // 强制覆盖 id 为 null
        if (metaObject.hasGetter("id")) {
            metaObject.setValue("id", null); // ⚡ 强制覆盖
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        OffsetDateTime now = TimeProvider.now();
        // 无论是否传值，都覆盖
        this.setFieldValByName("updatedAt", now, metaObject);
    }
}
