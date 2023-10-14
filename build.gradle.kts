plugins {
	java
	id("org.springframework.boot") version "3.1.4"
	id("io.spring.dependency-management") version "1.1.3"
}

group = "java-17-app"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
}

dependencies {
	annotationProcessor("org.projectlombok:lombok:1.18.22")
	compileOnly("org.projectlombok:lombok:1.18.22")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-data-rest")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("com.google.guava:guava:30.0-jre")
	implementation("org.liquibase:liquibase-core")
	implementation("org.apache.poi:poi:5.2.0")
	implementation("org.apache.poi:poi-ooxml:5.2.0")

	runtimeOnly("org.postgresql:postgresql")
	//api docs
	implementation("org.springdoc:springdoc-openapi-webflux-ui:1.6.12")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
