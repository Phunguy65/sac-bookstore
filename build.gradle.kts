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

fun loadEnvDefaults(path: String): Map<String, String> {
    val file = layout.projectDirectory.file(path).asFile
    check(file.exists()) { "Missing demo defaults file: $path" }
    return file.readLines()
        .map(String::trim)
        .filter { it.isNotEmpty() && !it.startsWith("#") }
        .associate { line ->
            val separator = line.indexOf('=')
            check(separator > 0) { "Invalid env entry in $path: $line" }
            line.substring(0, separator) to line.substring(separator + 1)
        }
}

val demoDefaults = loadEnvDefaults("docker/wildfly/demo-mode.env")

val dbUrl = providers.gradleProperty("dbUrl")
    .orElse(providers.environmentVariable("DB_URL"))
    .orElse("jdbc:postgresql://localhost:5432/bookstore")

val dbUser = providers.gradleProperty("dbUser")
    .orElse(providers.environmentVariable("DB_USER"))
    .orElse("bookstore")

val dbPassword = providers.gradleProperty("dbPassword")
    .orElse(providers.environmentVariable("DB_PASSWORD"))
    .orElse("bookstore")

val demoMode = providers.gradleProperty("bookstore.demo.mode")
    .orElse(providers.environmentVariable("BOOKSTORE_DEMO_MODE"))
    .map { value -> value.equals("true", ignoreCase = true) }
    .orElse(false)

val demoEmail = providers.gradleProperty("bookstore.demo.email")
    .orElse(providers.environmentVariable("BOOKSTORE_DEMO_EMAIL"))
    .orElse(demoDefaults.getValue("BOOKSTORE_DEMO_EMAIL"))

val demoPasswordHash = providers.gradleProperty("bookstore.demo.passwordHash")
    .orElse(providers.environmentVariable("BOOKSTORE_DEMO_PASSWORD_HASH"))
    .orElse(demoDefaults.getValue("BOOKSTORE_DEMO_PASSWORD_HASH"))

flyway {
    url = dbUrl.get()
    user = dbUser.get()
    password = dbPassword.get()
    locations = if (demoMode.get()) {
        arrayOf("classpath:db/migration", "classpath:db/dev")
    } else {
        arrayOf("classpath:db/migration")
    }
    placeholders = mapOf(
        "demoEmail" to demoEmail.get(),
        "demoPasswordHash" to demoPasswordHash.get()
    )
    baselineOnMigrate = true
}

dependencies {
    compileOnly(libs.jakartaee.api)
    implementation(libs.jbcrypt)
    runtimeOnly(libs.postgresql)

    testImplementation(libs.jakartaee.api)
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}
