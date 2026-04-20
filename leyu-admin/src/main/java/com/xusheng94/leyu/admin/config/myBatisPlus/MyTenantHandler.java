package com.xusheng94.leyu.admin.config.myBatisPlus;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.xusheng94.leyu.admin.cache.TableMetadataCache;
import com.xusheng94.leyu.admin.config.security.LoginUser;
import com.xusheng94.leyu.admin.util.CurrentUser;
import lombok.RequiredArgsConstructor;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.Expression;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class MyTenantHandler implements TenantLineHandler {

    /**
     * 多租户插件忽略的表
     */
    private static final Set<String> IGNORED_TABLES = Set.of(
            "sys__user",
            "sys__role",
            "sys__permission",
            "sys__user_role",
            "sys__role_permission");

    private final TableMetadataCache tableMetadataCache;

    /**
     * 返回租户 ID（tenant_id）
     */
    @Override
    public Expression getTenantId() {
        Long tenantId = Optional.ofNullable(CurrentUser.getLoginUser())
                .map(LoginUser::getTenantId)
                .orElse(null);
        return tenantId != null ? new LongValue(tenantId) : null;
    }

    /**
     * 租户字段名
     */
    @Override
    public String getTenantIdColumn() {
        return "tenant_id";
    }

    /**
     * 忽略系统表，不加 tenant_id 条件
     */
    @Override
    public boolean ignoreTable(String tableName) {

        if (tableName == null) {
            return true;
        }

        String normalizedTableName = tableName.toLowerCase(Locale.ROOT);

        if (IGNORED_TABLES.contains(normalizedTableName)) {
            return true;
        }

        // 如果当前用户是平台用户，也忽略
        if (CurrentUser.isPlatformUser()) {
            return true; // 不加 tenant_id
        }

        TableMetadataCache.TableMetadata metadata = tableMetadataCache.get(normalizedTableName);
        return metadata == null || !metadata.isHasTenantId();
    }
}
