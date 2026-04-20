package com.xusheng94.leyu.admin.config.myBatisPlus;

import com.baomidou.mybatisplus.extension.plugins.handler.MultiDataPermissionHandler;
import com.xusheng94.leyu.admin.cache.TableMetadataCache;
import com.xusheng94.leyu.admin.util.CurrentUser;
import com.xusheng94.leyu.common.enums.DataScopeType;
import lombok.RequiredArgsConstructor;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import org.springframework.stereotype.Component;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class MyMultiDataPermissionHandler implements MultiDataPermissionHandler {

    private static final String CREATOR_ID_COLUMN = "creator_id";
    private static final String DEPT_ID_COLUMN = "dept_id";
    private static final String REQUEST_CACHE_PREFIX = MyMultiDataPermissionHandler.class.getName() + ".";

    /**
     * 数据权限插件忽略的表
     */
    private static final Set<String> IGNORED_TABLES = Set.of(
            "sys__user",
            "sys__role",
            "sys__permission",
            "sys__user_role",
            "sys__role_permission");

    private final JdbcTemplate jdbcTemplate;
    private final TableMetadataCache tableMetadataCache;

    @Override
    public Expression getSqlSegment(Table table, Expression where, String mappedStatementId) {
        if (!CurrentUser.isLogin() || CurrentUser.isPlatformUser()) {
            return null;
        }

        String tableName = table.getName();
        if (tableName == null || IGNORED_TABLES.contains(tableName.toLowerCase(Locale.ROOT))) {
            return null;
        }

        TableMetadataCache.TableMetadata metadata = tableMetadataCache.get(tableName);
        if (metadata == null) {
            return null;
        }

        // 只有表里实际存在 creator_id / dept_id 时才拼数据权限条件。
        boolean hasCreatorId = metadata.isHasCreatorId();
        boolean hasDeptId = metadata.isHasDeptId();
        if (!hasCreatorId && !hasDeptId) {
            return null;
        }

        String sqlSegment = buildSqlSegment(table, hasCreatorId, hasDeptId);
        if (sqlSegment == null || sqlSegment.isBlank()) {
            return null;
        }

        try {
            return CCJSqlParserUtil.parseCondExpression(sqlSegment);
        } catch (JSQLParserException e) {
            throw new RuntimeException("构造数据权限 SQL 失败", e);
        }
    }

    private String buildSqlSegment(Table table, boolean hasCreatorId, boolean hasDeptId) {
        List<String> dataScopes = loadCurrentUserDataScopes();
        if (dataScopes.isEmpty()) {
            return "1 = 0";
        }
        if (dataScopes.contains(DataScopeType.ALL.getCode())) {
            return null;
        }

        Long userId = CurrentUser.getUserId();
        Long deptId = CurrentUser.getDeptId();
        String columnPrefix = buildColumnPrefix(table);

        LinkedHashSet<String> conditions = new LinkedHashSet<>();
        if (hasCreatorId && dataScopes.contains(DataScopeType.SELF.getCode()) && userId != null) {
            conditions.add(columnPrefix + CREATOR_ID_COLUMN + " = " + userId);
        }
        if (hasDeptId && deptId != null && dataScopes.contains(DataScopeType.DEPT.getCode())) {
            conditions.add(columnPrefix + DEPT_ID_COLUMN + " = " + deptId);
        }
        if (hasDeptId && deptId != null && dataScopes.contains(DataScopeType.DEPT_AND_CHILD.getCode())) {
            String deptPath = loadDeptPath(deptId);
            if (deptPath != null && !deptPath.isBlank()) {
                // path = 当前部门 或 path 以“当前部门/”开头，表示当前部门及所有下级部门。
                conditions.add("exists (select 1 from sys__dept ds where ds.id = "
                        + columnPrefix
                        + DEPT_ID_COLUMN + " and (ds.path = '"
                        + escapeSqlLiteral(deptPath)
                        + "' or ds.path like '"
                        + escapeSqlLiteral(deptPath)
                        + "/%'))");
            } else {
                conditions.add(columnPrefix + DEPT_ID_COLUMN + " = " + deptId);
            }
        }

        if (conditions.isEmpty()) {
            return "1 = 0";
        }

        return "(" + String.join(" or ", conditions) + ")";
    }

    private List<String> loadCurrentUserDataScopes() {
        Long userId = CurrentUser.getUserId();
        if (userId == null) {
            return List.of();
        }

        String cacheKey = REQUEST_CACHE_PREFIX + "dataScopes:" + userId;
        // 同一个 HTTP 请求里，当前用户的角色范围不会变化，避免重复查库。
        List<String> cachedScopes = getStringListRequestCache(cacheKey);
        if (cachedScopes != null) {
            return cachedScopes;
        }

        List<String> scopes = jdbcTemplate.query(
                """
                    select distinct r.data_scope
                    from sys__user_role ur
                    join sys__role r on r.id = ur.role_id
                    where ur.user_id = ?
                      and (r.deleted = 0 or r.deleted is null)
                      and r.data_scope is not null
                    """,
                (rs, rowNum) -> rs.getString("data_scope"),
                userId);

        List<String> normalized = new ArrayList<>();
        for (String scope : scopes) {
            if (scope != null && !scope.isBlank()) {
                normalized.add(scope.trim().toUpperCase(Locale.ROOT));
            }
        }

        List<String> result = List.copyOf(normalized);
        putRequestCache(cacheKey, result);
        return result;
    }

    private String loadDeptPath(Long deptId) {
        String cacheKey = REQUEST_CACHE_PREFIX + "deptPath:" + deptId;
        // 同一个 HTTP 请求里，部门 path 只需要查询一次。
        String cachedDeptPath = getStringRequestCache(cacheKey);
        if (cachedDeptPath != null) {
            return cachedDeptPath;
        }

        List<String> paths = jdbcTemplate.query(
                "select path from sys__dept where id = ? limit 1",
                (rs, rowNum) -> rs.getString("path"),
                deptId);

        String deptPath = paths.isEmpty() ? null : paths.getFirst();
        if (deptPath != null) {
            putRequestCache(cacheKey, deptPath);
        }
        return deptPath;
    }

    private String buildColumnPrefix(Table table) {
        if (table.getAlias() != null && table.getAlias().getName() != null) {
            return table.getAlias().getName() + ".";
        }
        return table.getName() + ".";
    }

    private String escapeSqlLiteral(String value) {
        return value.replace("'", "''");
    }

    private List<String> getStringListRequestCache(String key) {
        Object value = getRequestCache(key);
        if (value instanceof List<?> list && list.stream().allMatch(String.class::isInstance)) {
            return list.stream().map(String.class::cast).toList();
        }
        return null;
    }

    private String getStringRequestCache(String key) {
        Object value = getRequestCache(key);
        if (value instanceof String stringValue) {
            return stringValue;
        }
        return null;
    }

    private Object getRequestCache(String key) {
        ServletRequestAttributes requestAttributes = getServletRequestAttributes();
        if (requestAttributes == null) {
            return null;
        }
        return requestAttributes.getAttribute(key, RequestAttributes.SCOPE_REQUEST);
    }

    private void putRequestCache(String key, Object value) {
        ServletRequestAttributes requestAttributes = getServletRequestAttributes();
        if (requestAttributes == null) {
            return;
        }
        requestAttributes.setAttribute(key, value, RequestAttributes.SCOPE_REQUEST);
    }

    private ServletRequestAttributes getServletRequestAttributes() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes servletRequestAttributes) {
            return servletRequestAttributes;
        }
        return null;
    }
}