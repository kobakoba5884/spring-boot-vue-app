plugins {
    id("org.springframework.boot") version "3.3.0"
    id("io.spring.dependency-management") version "1.1.5"
    kotlin("jvm") version "1.9.24"
    kotlin("plugin.spring") version "1.9.24"
}

group = "com.minmin"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web:3.3.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.1")
    implementation("org.jetbrains.kotlin:kotlin-reflect:2.0.0")
    implementation("org.postgresql:postgresql:42.7.3")
    implementation("org.mybatis.dynamic-sql:mybatis-dynamic-sql:1.5.2")
    implementation("org.mybatis.generator:mybatis-generator-core:1.4.2")
    implementation("org.mybatis.spring.boot:mybatis-spring-boot-starter:3.0.3")
    developmentOnly("org.springframework.boot:spring-boot-devtools:3.3.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.3.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:2.0.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.10.2")
}

sourceSets {
    main {
        java {
            srcDir("src/main/kotlin")
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    // Skip tests
    onlyIf { false }
}

tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
    mainClass.set("com.minmin.mybatis_crud_api.MybatisCrudApiApplicationKt")
}

tasks.withType<org.springframework.boot.gradle.tasks.run.BootRun> {
    mainClass.set("com.minmin.mybatis_crud_api.MybatisCrudApiApplicationKt")
}

tasks.register<JavaExec>("generateMyBatis") {
    group = "mybatis"
    description = "Generate MyBatis files"
    mainClass.set("com.minmin.mybatis_crud_api.configs.GeneratorConfig")
    classpath = sourceSets["main"].runtimeClasspath
    doFirst {
        environment("DB_URL", System.getenv("DB_URL"))
        environment("DB_USER", System.getenv("DB_USER"))
        environment("DB_PASSWORD", System.getenv("DB_PASSWORD"))
        // Ensure the target directories exist
        val outputDir = file("src/main/kotlin/com/minmin/mybatis_crud_api/gen")
        if (outputDir.exists()) {
            outputDir.deleteRecursively()
        }
        outputDir.mkdirs()

        // Create .gitkeep file
        val gitkeep = file("src/main/kotlin/com/minmin/mybatis_crud_api/gen/.gitkeep")
        gitkeep.createNewFile()
    }
}
