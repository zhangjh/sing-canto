package me.zhangjh.sing.canto;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.sql.Types;
import java.util.Collections;

/**
 * @author njhxzhangjihong@126.com
 * @date 14:54 2024/4/30
 * @Description
 */
public class DaoGenerator {

    public static void main(String[] args) {
        DataSourceConfig.Builder dataSourceBuilder = new DataSourceConfig.Builder("jdbc:mysql://127.0.0.1:3306/sing_canto",
                "root", "root_123456");
        FastAutoGenerator.create(dataSourceBuilder)
                .globalConfig(builder -> {
                    builder.author("baomidou") // 设置作者
                            .enableSwagger() // 开启 swagger 模式
                            .fileOverride() // 覆盖已生成文件
                            .outputDir("me.zhangjh.sing.canto.dao.mapper"); // 指定输出目录
                })
                .dataSourceConfig(builder -> builder.typeConvertHandler((globalConfig, typeRegistry, metaInfo) -> {
                    int typeCode = metaInfo.getJdbcType().TYPE_CODE;
                    if (typeCode == Types.SMALLINT) {
                        // 自定义类型转换
                        return DbColumnType.INTEGER;
                    }
                    return typeRegistry.getColumnType(metaInfo);

                }))
                .packageConfig(builder -> {
                    builder.parent("me.zhangjh.sing.canto") // 设置父包名
                            .moduleName("sing-canto") // 设置父包模块名
                            .pathInfo(Collections.singletonMap(OutputFile.xml,
                                    "me.zhangjh.sing.canto.dao.mapper")); // 设置mapperXml生成路径
                })
                // 设置需要生成的表名
                .strategyConfig(builder ->
                        builder
//                         .addInclude("tbl_inference_resource")
//                        .addInclude("tbl_inference_task")
//                        .addInclude("tbl_material_model_mapping")
//                        .addInclude("tbl_model")
//                        .addInclude("tbl_train_material")
                        .addInclude("tbl_practiced"))
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }
}
