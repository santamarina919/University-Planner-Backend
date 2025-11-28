plugins {
    java
    war
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

    implementation("org.hibernate.orm:hibernate-core:${hibernateVersion}.Final")
    annotationProcessor("org.hibernate.orm:hibernate-processor:${hibernateVersion}.Final")

}

tasks.test {
    useJUnitPlatform()
}