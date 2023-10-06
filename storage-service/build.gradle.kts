

plugins {
    java
    id("org.springframework.boot") version "3.1.4"
    id("io.spring.dependency-management") version "1.1.3"
}


group = "org.example"

version = "1.0-SNAPSHOT"


repositories {
    mavenCentral()
}



dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("io.lettuce:lettuce-core:6.2.6.RELEASE")
    implementation("org.springframework.boot:spring-boot-starter-data-redis:3.1.4")
    compileOnly ("org.projectlombok:lombok:1.18.26")
    annotationProcessor ("org.projectlombok:lombok:1.18.26")
}

tasks.test {
    useJUnitPlatform()
}