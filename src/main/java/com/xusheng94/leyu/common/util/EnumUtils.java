package com.xusheng94.leyu.common.util;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import com.xusheng94.leyu.common.BaseEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnumUtils {

    public static Map<String, List<Map<String, Object>>> loadAllEnums(String packageName) {
        Map<String, List<Map<String, Object>>> result = new HashMap<>();
        try {
            // 扫描包下所有类
            ClassPathScanningCandidateComponentProvider provider =
                    new ClassPathScanningCandidateComponentProvider(false);
            provider.addIncludeFilter(new AssignableTypeFilter(BaseEnum.class));

            for (BeanDefinition bd : provider.findCandidateComponents(packageName)) {
                Class<?> clazz = Class.forName(bd.getBeanClassName());

                if (clazz.isEnum() && BaseEnum.class.isAssignableFrom(clazz)) {
                    Object[] constants = clazz.getEnumConstants();
                    List<Map<String, Object>> list = new ArrayList<>();

                    for (Object e : constants) {
                        BaseEnum<?> base = (BaseEnum<?>) e;
                        Map<String, Object> map = new HashMap<>();
                        map.put("code", base.getCode());
                        map.put("desc", base.getDesc());

                        String color = base.getColor();
                        if (color != null) {
                            map.put("color", color);
                        }

                        list.add(map);
                    }

                    // key 采用类名小写首字母：OrderStatus -> orderStatus
                    String key = clazz.getSimpleName();
                    key = Character.toLowerCase(key.charAt(0)) + key.substring(1);

                    result.put(key, list);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("加载枚举失败", e);
        }
        return result;
    }
}
