plugins {
	id("org.springframework.boot") version "3.3.0"
	id("io.spring.dependency-management") version "1.1.5"
	// id("com.arenagod.gradle.MybatisGenerator") version "1.4"
	kotlin("jvm") version "1.9.24"
	kotlin("plugin.spring") version "1.9.24"
}

group = "com.minmin"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web:3.0.3")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.1")
	implementation("org.jetbrains.kotlin:kotlin-reflect:2.0.0")
	// implementation("org.mybatis:mybatis:3.5.6")
	implementation("org.mybatis.dynamic-sql:mybatis-dynamic-sql:1.5.2")
	implementation("org.postgresql:postgresql:42.7.3")
	implementation("org.mybatis.generator:mybatis-generator-core:1.4.2")
	implementation("org.mybatis.spring.boot:mybatis-spring-boot-starter:3.0.3")
	developmentOnly("org.springframework.boot:spring-boot-devtools:3.3.0")
	testImplementation("org.springframework.boot:spring-boot-starter-test:3.3.0")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:2.0.0")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.10.2")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

// mybatisGenerator {
// 	verbose = true
// }
