

plugins {
    java
    id("org.springframework.boot") version "3.1.4"
    id("io.spring.dependency-management") version "1.1.3"
}


group = "ru.itmo"

version = "1.0-SNAPSHOT"


repositories {
    mavenCentral()
}



dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web:3.1.0")
    implementation("com.indeed:lsmtree-core:1.0.7")
    implementation("org.iq80.leveldb:leveldb:0.12")
    implementation("org.rocksdb:rocksdbjni:8.3.2")
    testImplementation(platform("org.junit:junit-bom:5.9.2"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
    implementation("io.lettuce:lettuce-core:6.2.6.RELEASE")
    implementation("org.springframework.boot:spring-boot-starter-data-redis:3.1.4")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.0.4")
    compileOnly ("org.projectlombok:lombok:1.18.26")
    annotationProcessor ("org.projectlombok:lombok:1.18.26")
}

tasks.test {
    useJUnitPlatform()
}