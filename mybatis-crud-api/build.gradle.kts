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
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	// implementation("org.mybatis:mybatis:3.5.6")
	// implementation("org.mybatis.dynamic-sql:mybatis-dynamic-sql:1.2.1")
	implementation("org.postgresql:postgresql:42.7.3")
	// mybatisGenerator("org.mybatis.generator:mybatis-generator-core:1.4.0")
	implementation("org.mybatis.spring.boot:mybatis-spring-boot-starter:3.0.3")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
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
