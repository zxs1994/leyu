package devtools;

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
}
