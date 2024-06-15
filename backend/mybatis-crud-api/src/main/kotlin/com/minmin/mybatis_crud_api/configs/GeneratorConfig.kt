package com.minmin.mybatis_crud_api.configs

import java.util.*
import org.mybatis.generator.api.MyBatisGenerator
import org.mybatis.generator.config.*
import org.mybatis.generator.internal.DefaultShellCallback

object GeneratorConfig {

    fun generateMyBatisFiles() {
        val warnings = ArrayList<String>()
        val overwrite = true

        val config = Configuration()

        val context =
                Context(ModelType.FLAT).apply {
                    id = "PostgreSQLTables"
                    targetRuntime = "MyBatis3DynamicSql"

                    // Comment Generator
                    commentGeneratorConfiguration =
                            CommentGeneratorConfiguration().apply {
                                addProperty("suppressDate", "true")
                            }

                    // JDBC Connection Configuration
                    val jdbcConnectionConfiguration =
                            JDBCConnectionConfiguration().apply {
                                driverClass = System.getenv("DB_DRIVER")
                                connectionURL = System.getenv("DB_URL")
                                userId = System.getenv("DB_USER")
                                password = System.getenv("DB_PASSWORD")
                                addProperty("nullCatalogMeansCurrent", "true")
                            }
                    val jdbcField =
                            Context::class.java.getDeclaredField("jdbcConnectionConfiguration")
                    jdbcField.isAccessible = true
                    jdbcField.set(this, jdbcConnectionConfiguration)

                    // Java Type Resolver
                    javaTypeResolverConfiguration =
                            JavaTypeResolverConfiguration().apply {
                                addProperty("useJSR310Types", "true")
                            }

                    javaModelGeneratorConfiguration =
                            JavaModelGeneratorConfiguration().apply {
                                targetPackage = "com.minmin.mybatis_crud_api.gen.entities"
                                targetProject = "./src/main/kotlin"
                            }

                    javaClientGeneratorConfiguration =
                            JavaClientGeneratorConfiguration().apply {
                                configurationType = "XMLMAPPER"
                                targetPackage = "com.minmin.mybatis_crud_api.gen.mappers"
                                targetProject = "./src/main/kotlin"
                            }

                    // Table Configuration for specific schema and table
                    val tableConfiguration =
                            TableConfiguration(this).apply {
                                schema = "my_schema" // Replace with your schema name
                                tableName = "%"
                            }

                    addTableConfiguration(tableConfiguration)
                }

        config.addContext(context)

        val callback = DefaultShellCallback(overwrite)
        val myBatisGenerator = MyBatisGenerator(config, callback, warnings)
        myBatisGenerator.generate(null)

        for (warning in warnings) {
            println(warning)
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        generateMyBatisFiles()
    }
}
