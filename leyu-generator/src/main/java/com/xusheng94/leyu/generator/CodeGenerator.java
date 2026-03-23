package com.xusheng94.leyu.generator;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.builder.CustomFile;
import com.baomidou.mybatisplus.generator.config.rules.IColumnType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

public class CodeGenerator {

    public static void main(String[] args) throws IOException {
        // 从模块读配置
        String moduleName = "leyu-admin";

        // 要生成代码的表
        String tableName = "sys__user";

        LoadYaml.setBusinessModule(moduleName);

        String url = LoadYaml.getDevProperty("spring", "datasource", "url");
        String username = LoadYaml.getDevProperty("spring", "datasource", "username");
        String password = LoadYaml.getDevProperty("spring", "datasource", "password");
        String parentPackage = LoadYaml.getParentPackage();
        String basePackage = LoadYaml.getBasePackage();

        // System.out.println("url: " + url);
        // System.out.println("username: " + username);
        // System.out.println("password: " + password);

        // 多模块支持：自动定位项目根目录，输出代码到指定模块
        String parentDir = LoadYaml.findParentDirectory();
        String outputDir = parentDir + "/" + moduleName + "/src/main/java";

        System.out.println("Parent 目录: " + parentDir);
        System.out.println("输出目录: " + outputDir);
        System.out.println("基础包名: " + basePackage);

        FastAutoGenerator.create(url, username, password)
                .dataSourceConfig(builder -> builder
                        .typeConvertHandler((globalConfig, typeRegistry, metaInfo) -> {
                            int typeCode = metaInfo.getJdbcType().TYPE_CODE;
                            if (typeCode == Types.TIMESTAMP || typeCode == Types.TIMESTAMP_WITH_TIMEZONE) {
                                return new IColumnType() {
                                    @Override
                                    public String getType() {
                                        return "OffsetDateTime";
                                    }

                                    @Override
                                    public String getPkg() {
                                        return "java.time.OffsetDateTime";
                                    }
                                };
                            }
                            // JSON 判断
                            // 这里用 metaInfo.getColumnType() 返回数据库类型名字符串（比如 "json", "longtext" 等）
                            String columnType = metaInfo.getTypeName().toLowerCase();
                            if (columnType.contains("json")) {
                                return new IColumnType() {
                                    @Override
                                    public String getType() {
                                        return "Map<String,Object>";
                                    }

                                    @Override
                                    public String getPkg() {
                                        return "java.util.Map";
                                    }
                                };
                            }
                            return typeRegistry.getColumnType(metaInfo);
                        }))
                .globalConfig(builder -> builder
                        .author("xusheng")
                        .outputDir(outputDir)
                        .commentDate("yyyy-MM-dd HH:mm:ss")
                        .disableOpenDir() // 禁止自动打开输出目录
                )
                .packageConfig(builder -> builder.parent(basePackage) // 父包名
                // .entity("entity")
                // .mapper("mapper")
                // .service("service")
                // .controller("controller")
                // .serviceImpl("service.impl")
                // .xml("mapper.xml")
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
                    customMap.put("parentPackage", parentPackage);
                    customMap.put("autoIdTables", GeneratorConfig.autoIdTables);
                    customMap.put("readOnlyFields", GeneratorConfig.readOnlyFields);
                    customMap.put("jsonIgnoreFields", GeneratorConfig.jsonIgnoreFields);
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
                            // .enableFileOverride() // 覆盖生成的文件
                            .build());
                })
                .execute();

        deleteNoControllerFiles(outputDir, basePackage);

    }

    /**
     * 由于生成器不能按条件生成，所以使用生成后再删除的方法
     * 
     * @param outputDir   文件夹
     * @param basePackage 基础包
     * @throws IOException IO异常
     */
    private static void deleteNoControllerFiles(
            String outputDir,
            String basePackage) throws IOException {

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
