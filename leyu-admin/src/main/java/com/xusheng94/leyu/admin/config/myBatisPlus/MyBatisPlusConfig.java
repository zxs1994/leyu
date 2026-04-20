package com.xusheng94.leyu.admin.config.myBatisPlus;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DataPermissionInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@MapperScan("${project.base-package}.mapper")
@RequiredArgsConstructor
public class MyBatisPlusConfig {

    private final MyTenantHandler tenantHandler;
    private final MyDataPermissionHandler dataPermissionHandler;

    /**
     * MyBatis-Plus 拦截器
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 1️⃣ 多租户插件
        interceptor.addInnerInterceptor(new TenantLineInnerInterceptor(tenantHandler));
        // 2️⃣ 数据权限插件
        interceptor.addInnerInterceptor(new DataPermissionInterceptor(dataPermissionHandler));
        // 3️⃣ 分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}