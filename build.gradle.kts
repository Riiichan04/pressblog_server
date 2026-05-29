plugins {
    java
    id("java-library")  // To used library catalog
    id("org.springframework.boot") version "4.0.0"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "vn.id.devblog"
version = "0.0.1-SNAPSHOT"
description = "blog_server"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // For JSTL in JSP
    // api(libs.org.glassfish.web.jakarta.servlet.jsp.jstl)
    // api(libs.jakarta.servlet.jsp.jstl.jakarta.servlet.jsp.jstl.api)
    // compileOnly(libs.jakarta.servlet.jakarta.servlet.api)

//    api(libs.mysql.mysql.connector.java)
    runtimeOnly(libs.postgresql)

    api(libs.org.jdbi.jdbi3.core)
    api(libs.org.jdbi.jdbi3.sqlobject)

    api(libs.org.projectlombok.lombok)
    annotationProcessor(libs.org.projectlombok.lombok)

    api(libs.com.google.code.gson.gson)

    api(libs.org.mindrot.jbcrypt)

    //For Hibernate/JPA ORM
    api(libs.org.hibernate.orm)

    api(libs.jsoup)

    implementation(libs.jjwt.api)
    runtimeOnly(libs.jjwt.impl)
    runtimeOnly(libs.jjwt.jackson)

    implementation (libs.cloudinary)
    implementation (libs.cloudinary.http5)

    api(libs.spring.boot.starter.mail)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter.thymeleaf)
    implementation(libs.springboot.starter)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.data.redis)
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.security)

    testImplementation(libs.org.junit.jupiter.junit.jupiter.api)
    testImplementation(libs.org.junit.jupiter.junit.jupiter.engine)
}


tasks.withType<Test> {
    useJUnitPlatform()
}
