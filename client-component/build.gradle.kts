plugins {
    java
    id("org.springframework.boot") version "3.0.4"
    id("io.spring.dependency-management") version "1.1.0"
}
group = "ru.itmo"

version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    compileOnly("org.projectlombok:lombok")
    implementation("org.springframework.boot:spring-boot-gradle-plugin:3.0.4")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    annotationProcessor("org.projectlombok:lombok:1.18.26")
    implementation("org.springframework.boot:spring-boot-starter-webflux:3.1.4")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
