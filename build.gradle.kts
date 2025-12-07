plugins {
    java
    war
    id ("io.spring.dependency-management") version "1.0.6.RELEASE"
}


group = "dev.J"
version = "1.0-SNAPSHOT"
val springVersion = "7.0.1"
val jakaartaVersion = "11.0.0"
//why do i need this?
val yassonVersion = "3.0.4"
val hibernateVersion = "7.1.10"

tasks.war {
    archiveFileName.set("planuni.war")
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("org.springframework:spring-core:${springVersion}")
    implementation("org.springframework:spring-context:${springVersion}")
    implementation("org.springframework:spring-beans:${springVersion}")
    implementation("org.springframework:spring-web:${springVersion}")
    implementation("org.springframework:spring-webmvc:${springVersion}")
    implementation("jakarta.platform:jakarta.jakartaee-api:${jakaartaVersion}")
    implementation("org.eclipse:yasson:${yassonVersion}")
    // https://mvnrepository.com/artifact/tools.jackson.core/jackson-databind
    implementation("tools.jackson.core:jackson-databind:3.0.3")


    implementation("org.hibernate.orm:hibernate-core:${hibernateVersion}.Final")
    annotationProcessor("org.hibernate.orm:hibernate-processor:${hibernateVersion}.Final")
    implementation("org.hibernate.validator:hibernate-validator:9.0.1.Final")
    implementation("org.glassfish.expressly:expressly:6.0.0")

    compileOnly("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")

    implementation("org.postgresql:postgresql:42.7.8")


    // https://mvnrepository.com/artifact/org.mindrot/jbcrypt
    implementation("org.mindrot:jbcrypt:0.4")

    implementation("org.springframework.security:spring-security-web")
    implementation("org.springframework.security:spring-security-config")

    implementation("ch.qos.logback:logback-classic:1.5.21")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.security:spring-security-bom:7.0.0")
    }
}


tasks.test {
    useJUnitPlatform()
}