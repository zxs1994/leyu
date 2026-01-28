package devtools;

import lombok.Data;

import java.util.Map;
import java.util.Set;

public class GeneratorConfig {
    /**
     * 不生成控制器的表
     */
    public static final Set<String> noControllerTables = Set.of(
            "sys__user_role",
            "sys__role_permission"
    );

    /**
     * 使用自增ID的表
     */
    public static final Set<String> autoIdTables = Set.of(
            "sys__permission",
            "sys__user_role",
            "sys__role_permission"
    );

    /**
     * 只读字段
     */
    public static final Set<String> readOnlyFields = Set.of(
            "id",
            "source",
            "token_version"
    );

    /**
     * 忽略字段
     */
    public static final Set<String> ignoreFields = Set.of(
            "created_at",
            "updated_at"
    );

    /**
     * 查询字段配置（queryConfig）
     *
     * 用于描述「查询对象字段」与「数据库查询条件」之间的映射关系，
     * 通常配合 FreeMarker / 代码生成器，在 Service 层自动生成 QueryWrapper 查询逻辑。
     *
     * <p>
     * 配置结构说明：
     * </p>
     *
     * <pre>
     * key   : 查询对象中的字段名（Query DTO 字段名）
     * value : Map<String, Object>，用于描述该字段的查询规则
     * </pre>
     *
     * <p>
     * value 中支持的配置项：
     * </p>
     *
     * <ul>
     *   <li><b>column</b>：数据库字段名（String）</li>
     *   <li><b>operator</b>：查询操作符（String），支持以下值：
     *     <ul>
     *       <li>eq       —— 等于（=）</li>
     *       <li>ne       —— 不等于（&lt;&gt;）</li>
     *       <li>like     —— 模糊匹配（LIKE %value%）</li>
     *       <li>in       —— 集合匹配（IN (...)）</li>
     *       <li>between  —— 区间查询（需要 Query 中提供 Start / End 字段）</li>
     *       <li>gt       —— 大于（&gt;）</li>
     *       <li>ge       —— 大于等于（&gt;=）</li>
     *       <li>lt       —— 小于（&lt;）</li>
     *       <li>le       —— 小于等于（&lt;=）</li>
     *     </ul>
     *   </li>
     *   <li><b>likeMode</b>：模糊匹配模式（String，可选）
     *     <ul>
     *       <li>both  —— %value%（默认）</li>
     *       <li>left  —— %value</li>
     *       <li>right —— value%</li>
     *     </ul>
     *   </li>
     *   <li><b>ignoreEmpty</b>：是否忽略空值（Boolean，可选）
     *     <ul>
     *       <li>true  —— null / 空字符串时不拼接查询条件</li>
     *       <li>false —— 即使为空也参与查询</li>
     *     </ul>
     *   </li>
     * </ul>
     *
     * <p>
     * 使用示例：
     * </p>
     *
     * <pre>
     * "name" -> LIKE 查询（忽略空字符串）
     * "status" -> 精确匹配
     * "createdAt" -> 区间查询（createdAtStart / createdAtEnd）
     * </pre>
     *
     * <p>
     * 说明：
     * <ul>
     *   <li>该配置主要服务于代码生成与动态查询，不建议在运行时频繁修改</li>
     *   <li>如需更复杂逻辑（OR / 子查询），建议在 Service 中手写</li>
     * </ul>
     * </p>
     */
    public static final Map<String, Map<String, Object>> queryConfig = Map.of(
            "name", Map.of(
                    "column", "name",
                    "operator", "like",
                    "likeMode", "both",
                    "ignoreEmpty", true
            ),
            "status", Map.of(
                    "column", "status",
                    "operator", "eq"
            ),
            "createdAt", Map.of(
                    "column", "created_at",
                    "operator", "between",
                    "fieldType", "OffsetDateTime"
            )
    );


}
