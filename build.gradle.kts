plugins {
    java
    war
    alias(libs.plugins.flyway)
}

group = "io.github.phunguy65.bookstore"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.java.get()))
    }
}

tasks.test {
    useJUnitPlatform()
}

val dbUrl = providers.gradleProperty("dbUrl")
    .orElse(providers.environmentVariable("DB_URL"))
    .orElse("jdbc:postgresql://localhost:5432/bookstore")

val dbUser = providers.gradleProperty("dbUser")
    .orElse(providers.environmentVariable("DB_USER"))
    .orElse("bookstore")

val dbPassword = providers.gradleProperty("dbPassword")
    .orElse(providers.environmentVariable("DB_PASSWORD"))
    .orElse("bookstore")

flyway {
    url = dbUrl.get()
    user = dbUser.get()
    password = dbPassword.get()
    locations = arrayOf("classpath:db/migration")
    baselineOnMigrate = true
}

dependencies {
    compileOnly(libs.jakartaee.api)
    runtimeOnly(libs.postgresql)

    testImplementation(libs.jakartaee.api)
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}
