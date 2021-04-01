import java.util.Calendar

plugins {
    java
    kotlin("jvm") version "1.4.31"
    jacoco
    eclipse
    idea
    `maven-publish`
    id("org.jetbrains.dokka") version "1.4.20"
    id("com.github.hierynomus.license") version "0.15.0"
    id("com.diffplug.spotless") version "5.11.0"
    id("com.github.rising3.semver") version "0.2.0"
}

group = "com.github.rising3"

repositories {
    mavenLocal()
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.4.2")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.4.32")
}

val install by tasks.creating() {
    dependsOn(tasks.publishToMavenLocal)
}

val jar by tasks.getting(Jar::class) {
    manifest {
        attributes["Implementation-Title"] = "semver"
        attributes["Implementation-Version"] = project.version
    }
}

val sourcesJar by tasks.creating(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.getByName("main").allSource)
}

val javadocJar by tasks.creating(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Assembles Kotlin docs with Dokka"
    archiveClassifier.set("javadoc")
    from(tasks.dokkaJavadoc)
    dependsOn(tasks.dokkaJavadoc)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

tasks.dokkaJavadoc.configure {
    outputDirectory.set(buildDir.resolve("javadoc"))
    moduleName.set(rootProject.name)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group as String
            artifactId = project.name
            version = project.version as String
            from(components["java"])
            artifact(javadocJar)
            artifact(sourcesJar)
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/rising3/semver")
            credentials {
                val actor = project.findProperty("github.actor") as String?
                val token = project.findProperty("github.token") as String?
                username = actor ?: System.getenv("GITHUB_ACTOR")
                password = token ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

tasks.test {
    useJUnitPlatform()
    finalizedBy("jacocoTestReport")
}

jacoco { toolVersion = "0.8.6" }

tasks.jacocoTestReport {
    reports {
        xml.isEnabled = false
        csv.isEnabled = false
    }
    finalizedBy("jacocoTestCoverageVerification")
}

spotless {
    java {
        importOrder()
        removeUnusedImports()
        googleJavaFormat("1.8").aosp()
        prettier()
    }
    kotlin {
        ktlint("0.41.0").userData(mapOf("disabled_rules" to "no-wildcard-imports"))
    }
    kotlinGradle {
        ktlint("0.41.0").userData(mapOf("disabled_rules" to "no-wildcard-imports"))
    }
}

license {
    header = rootProject.file("codequality/HEADER")
    strictCheck = true
    license.ext["year"] = Calendar.getInstance().get(Calendar.YEAR)
    license.ext["name"] = project.findProperty("author")
    license.ext["email"] = project.findProperty("email")
}