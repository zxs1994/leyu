package devtools;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.example.template.util.LoadProperties;

import java.io.IOException;
import java.util.Collections;
import java.util.Properties;

public class CodeGenerator {

    public static void main(String[] args) throws IOException {
        Properties devProps = LoadProperties.load("src/main/resources/application-dev.properties");

        String url = devProps.getProperty("spring.datasource.url");
        String username = devProps.getProperty("spring.datasource.username");
        String password = devProps.getProperty("spring.datasource.password");
        String basePackage = LoadProperties.getBasePackage();

        System.out.println("数据库 URL: " + url);
        System.out.println("数据库 用户名: " + username);
        System.out.println("数据库 密码: " + password);
        System.out.println("项目基础包: " + basePackage);

        String tableName = "user";
        FastAutoGenerator.create(url, username, password)
                .globalConfig(builder -> builder
                        .author("xusheng")
                        .outputDir(System.getProperty("user.dir") + "/src/main/java")
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
                            .entityBuilder()
                            .enableTableFieldAnnotation() // ✅ 强烈推荐
                            .addIgnoreColumns("id", "created_at", "updated_at", "deleted")
                            .enableFileOverride() // 覆盖生成的文件

                        .controllerBuilder()
                )
                .templateEngine(new FreemarkerTemplateEngine())
                .injectionConfig(builder -> builder
                        .customMap(Collections.singletonMap("basePackage", basePackage)) // 传给模板
                )
                .execute();

    }
}

