package devtools;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.builder.CustomFile;
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

        String tableName = "test_table";

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
                            .logicDeleteColumnName("deleted")
                            .enableFileOverride() // 覆盖生成的文件

                )
                .templateEngine(new FreemarkerTemplateEngine())
                .injectionConfig(injectConfig -> {
                    Map<String, Object> customMap = new HashMap<>();

                    customMap.put("basePackage", basePackage);
                    customMap.put("autoIdTables", GeneratorConfig.autoIdTables);
                    customMap.put("readOnlyFields", GeneratorConfig.readOnlyFields);
                    customMap.put("ignoreFields", GeneratorConfig.ignoreFields);
                    customMap.put("queryConfig", GeneratorConfig.queryConfig);

                    injectConfig.customMap(customMap);

                    injectConfig.customFile(new CustomFile.Builder()
                            .fileName("Dto.java")
                            .templatePath("templates/dto.java.ftl")
                            .packageName("model.dto")
                            .build());

                    injectConfig.customFile(new CustomFile.Builder()
                            .fileName("Vo.java")
                            .templatePath("templates/vo.java.ftl")
                            .packageName("model.vo")
                            .build());

                    injectConfig.customFile(new CustomFile.Builder()
                            .fileName("Query.java")
                            .templatePath("templates/query.java.ftl")
                            .packageName("model.query")
                            .enableFileOverride() // 覆盖生成的文件
                            .build());
                })
                .execute();

        deleteNoControllerFiles(outputDir, basePackage);

    }

    /**
     * 由于生成器不能按条件生成，所以使用生成后再删除的方法
     * @param outputDir 文件夹
     * @param basePackage 基础包
     * @throws IOException IO异常
     */
    private static void deleteNoControllerFiles(
            String outputDir,
            String basePackage
    ) throws IOException {

        String controllerPath = outputDir + "/" + basePackage.replace(".", "/") + "/controller";
        String dtoPath = outputDir + "/" + basePackage.replace(".", "/") + "/model/dto";
        String voPath = outputDir + "/" + basePackage.replace(".", "/") + "/model/vo";
        String queryPath = outputDir + "/" + basePackage.replace(".", "/") + "/model/query";


        for (String table : GeneratorConfig.noControllerTables) {
            String entityName = NamingStrategy.capitalFirst(NamingStrategy.underlineToCamel(table));

            // 删除 Controller
            Path controllerFile = Paths.get(controllerPath + "/" + entityName + "Controller.java");
            if (Files.exists(controllerFile)) {
                Files.delete(controllerFile);
                System.out.println("🗑 已删除 Controller: " + controllerFile.getFileName());
            }

            // 删除 DTO
            Path dtoFile = Paths.get(dtoPath + "/" + entityName + "Dto.java");
            if (Files.exists(dtoFile)) {
                Files.delete(dtoFile);
                System.out.println("🗑 已删除 DTO: " + dtoFile.getFileName());
            }

            // 删除 VO
            Path voFile = Paths.get(voPath + "/" + entityName + "Vo.java");
            if (Files.exists(voFile)) {
                Files.delete(voFile);
                System.out.println("🗑 已删除 VO: " + voFile.getFileName());
            }

            // 删除 VO
            Path queryFile = Paths.get(queryPath + "/" + entityName + "Query.java");
            if (Files.exists(queryFile)) {
                Files.delete(queryFile);
                System.out.println("🗑 已删除 Query: " + queryFile.getFileName());
            }
        }
    }
}


