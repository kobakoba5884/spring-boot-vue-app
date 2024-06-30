import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    kotlin("jvm") version "1.9.24" apply false
    kotlin("plugin.spring") version "1.9.24" apply false
    id("org.springframework.boot") version "3.3.1" apply false
    id("io.spring.dependency-management") version "1.1.5" apply false
    id("org.openapi.generator") version "7.6.0" apply false
    id("org.springdoc.openapi-gradle-plugin") version "1.9.0" apply false
}

group = "com.minmin"
version = "0.0.1-SNAPSHOT"

tasks.withType<Jar> {
    enabled = false
}

tasks.withType<BootJar> {
    enabled = false
}

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "org.openapi.generator")
    apply(plugin = "org.springdoc.openapi-gradle-plugin")

    // https://kotlinlang.org/docs/gradle-configure-project.html#what-s-next
    dependencies {
        "implementation"("org.springframework.boot:spring-boot-starter-web")
        "implementation"("com.fasterxml.jackson.module:jackson-module-kotlin")
        "implementation"("org.jetbrains.kotlin:kotlin-reflect")
        "developmentOnly"("org.springframework.boot:spring-boot-devtools")
        "implementation"("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")
        "implementation"("org.springdoc:springdoc-openapi-starter-webmvc-api:2.5.0")
        "implementation"("org.springdoc:springdoc-openapi-ui:1.8.0")
        "implementation"("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.1")
        "testImplementation"("org.springframework.boot:spring-boot-starter-test")
        "testImplementation"("org.jetbrains.kotlin:kotlin-test-junit5")
        "testRuntimeOnly"("org.junit.platform:junit-platform-launcher")
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "17"
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
    
}

project(":author-api") {
    dependencies {
        "implementation"(project(":shared"))
    }

    tasks.withType<org.springdoc.openapi.gradle.plugin.OpenApiGeneratorTask> {
        apiDocsUrl.set("http://localhost:8080/api-docs")
        outputDir.set(file("$buildDir/generated/docs"))
        outputFileName.set("swagger.yml")
        waitTimeInSeconds.set(60)
    }

    tasks.register<org.openapitools.generator.gradle.plugin.tasks.GenerateTask>("generateApiClient") {
        dependsOn("generateOpenApiDocs")
        generatorName.set("kotlin")
        inputSpec.set("$buildDir/generated/docs/swagger.yml")
        outputDir.set("$buildDir/generated/author-api-client")
        apiPackage.set("com.minmin.author_api_client.api")
        modelPackage.set("com.minmin.author_api_client.model")
        invokerPackage.set("com.minmin.author_api_client.invoker")
        configOptions.set(mapOf(
            "dateLibrary" to "java17",
        ))
    }
}

project(":shared") {
    tasks.withType<Jar> {
        enabled = true
    }

    tasks.withType<BootJar> {
        enabled = false
    }
}