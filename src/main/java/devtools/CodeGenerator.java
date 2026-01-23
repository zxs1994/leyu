package devtools;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class CodeGenerator {

    public static void main(String[] args) throws IOException {

        String url = LoadYaml.getDevProperty("spring", "datasource", "url");
        String username = LoadYaml.getDevProperty("spring", "datasource", "username");
        String password = LoadYaml.getDevProperty("spring", "datasource", "password");
        String basePackage = LoadYaml.getBasePackage();
        String outputDir = System.getProperty("user.dir") + "/src/main/java";

//        System.out.println("数据库 URL: " + url);
//        System.out.println("数据库 用户名: " + username);
//        System.out.println("数据库 密码: " + password);
//        System.out.println("项目基础包: " + basePackage);

        String tableName = "sys__user_role";

        FastAutoGenerator.create(url, username, password)
                .globalConfig(builder -> builder
                        .author("xusheng")
                        .outputDir(outputDir)
                        .commentDate("yyyy-MM-dd HH:mm:ss")
                        .disableOpenDir() // 禁止自动打开输出目录
                )
                .packageConfig(builder ->
                    builder.parent(basePackage) // 父包名
//                            .entity("entity")
//                            .mapper("mapper")
//                            .service("service")
//                            .controller("controller")
//                            .serviceImpl("service.impl")
//                            .xml("mapper.xml")
                )
                .strategyConfig(builder -> builder
                        .addInclude(tableName)

                        .entityBuilder()
                            .enableTableFieldAnnotation() // ✅ 强烈推荐
                            .addIgnoreColumns("created_at", "updated_at")
                            .logicDeleteColumnName("deleted")
                            .enableFileOverride() // 覆盖生成的文件

                )
                .templateEngine(new FreemarkerTemplateEngine())
                .injectionConfig(builder -> {
                    Map<String, Object> customMap = new HashMap<>();

                    customMap.put("basePackage", basePackage);
                    customMap.put("readOnlyFields", GeneratorConfig.readOnlyFields);
                    customMap.put("autoIdTables", GeneratorConfig.autoIdTables);

                    builder.customMap(customMap);
                })
                .execute();

        deleteNoControllerFiles(outputDir, basePackage);

    }

    private static void deleteNoControllerFiles(
            String outputDir,
            String basePackage
    ) throws IOException {

        String controllerPath = outputDir
                + "/"
                + basePackage.replace(".", "/")
                + "/controller";

        for (String table : GeneratorConfig.noControllerTables) {
            String entityName =
                    NamingStrategy.capitalFirst(
                            NamingStrategy.underlineToCamel(table)
                    );
            String controllerFile = controllerPath + "/" + entityName + "Controller.java";

            Path path = Paths.get(controllerFile);
            if (Files.exists(path)) {
                Files.delete(path);
                System.out.println("🗑 已删除 Controller: " + path.getFileName());
            }
        }
    }
}


