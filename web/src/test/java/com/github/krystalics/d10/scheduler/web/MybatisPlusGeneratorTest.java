package com.github.krystalics.d10.scheduler.web;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import org.junit.jupiter.api.Test;

import java.util.Collections;

/**
 * @author linjiabao001
 * @date 2021/10/2
 * @description
 */

public class MybatisPlusGeneratorTest extends SprintBootBaseTest {
    @Test
    public void generate() {
        FastAutoGenerator.create("127.0.0.1:3306", "root", "root")
                .globalConfig(builder -> {
                    builder.author("krysta") // 设置作者
                            .enableSwagger() // 开启 swagger 模式
                            .fileOverride() // 覆盖已生成文件
                            .outputDir("D://"); // 指定输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("com.github.krystalics.d10.scheduler.dao") // 设置父包名
                            .moduleName("dao") // 设置父包模块名
                            .pathInfo(Collections.singletonMap(OutputFile.mapperXml, "D://")); // 设置mapperXml生成路径
                })
                .strategyConfig(builder -> {
                    builder.addInclude("task") // 设置需要生成的表名
                            .addInclude("version")
                            .addInclude("instance")
                            .addInclude("task_rely")
                            .addInclude("instance_rely");
                })
                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();
    }
}
