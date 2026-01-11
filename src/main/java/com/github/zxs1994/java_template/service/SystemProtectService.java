package com.github.zxs1994.java_template.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.zxs1994.java_template.common.BizException;
import com.github.zxs1994.java_template.enums.DataSourceType;

import java.io.Serializable;
import java.lang.reflect.Field;

public abstract class SystemProtectService<M extends BaseMapper<T>, T>
        extends ServiceImpl<M, T> {

    protected void checkSystemData(T entity) {
        if (entity == null) return;

        TableInfo tableInfo = TableInfoHelper.getTableInfo(entity.getClass());
        if (tableInfo == null) return;

        TableFieldInfo sourceField = tableInfo.getFieldList().stream()
                .filter(f -> "source".equals(f.getColumn()))
                .findFirst()
                .orElse(null);

        if (sourceField == null) return;

        try {
            Field field = sourceField.getField();
            field.setAccessible(true);
            Object value = field.get(entity);

            if (value == null) return;

            if (DataSourceType.SYSTEM.getCode().equals(value.toString())) {
                throw new BizException(403, "系统内置数据不允许修改或删除");
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("读取 source 字段失败", e);
        }
    }

    /* ========== 修改保护 ========== */

    @Override
    public boolean updateById(T entity) {
        Serializable id = getEntityId(entity);
        if (id != null) {
            T old = getById(id);
            checkSystemData(old);
        }
        return super.updateById(entity);
    }

    @Override
    public boolean saveOrUpdate(T entity) {
        Serializable id = getEntityId(entity);
        if (id != null) {
            T old = getById(id);
            checkSystemData(old);
        }
        return super.saveOrUpdate(entity);
    }

    @Override
    public boolean removeById(Serializable id) {
        T old = getById(id);
        checkSystemData(old);
        return super.removeById(id);
    }

    protected Serializable getEntityId(T entity) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(entity.getClass());
        return tableInfo == null ? null : (Serializable) tableInfo.getPropertyValue(entity, "id");
    }
}