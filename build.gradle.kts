import org.gradle.internal.impldep.org.fusesource.jansi.AnsiRenderer.test

val kgraphql_version = "0.18.0"
val kotlinVersion= project.properties["kotlinVersion"]
val http4kVersion = "4.33.1.0"
val log4jVersion = "2.19.0"
val junitVersion = "5.7.2"
val koin_version = "3.3.2"
val koin_ksp_version = "1.1.0"


plugins {
    application
    id("org.jetbrains.kotlin.jvm") version "1.7.20"
    kotlin("plugin.serialization") version "1.7.20"
    id("org.jetbrains.kotlin.kapt") version "1.7.20"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.7.20"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("org.graalvm.buildtools.native") version "0.9.16"
    id("com.google.devtools.ksp") version "1.7.20-1.0.7"
    id("com.google.cloud.tools.jib") version "3.3.1"
    id ("org.sonarqube") version "3.4.0.2513"
//    id("com.palantir.git-version") version "0.15.0"


}

group = "com.simon.events"

repositories {
    mavenCentral()
}

kotlin {
    sourceSets.main {
        kotlin.srcDir("build/generated/ksp/main/kotlin")
    }
}

dependencies {
    kapt ("org.litote.kmongo:kmongo-annotation-processor:4.8.0")
    ksp("com.squareup.moshi:moshi-kotlin-codegen:1.14.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation(platform("org.http4k:http4k-bom:${http4kVersion}"))
    implementation("org.kodein.di:kodein-di-jvm:7.15.0")
    implementation ("org.http4k:http4k-client-okhttp:${http4kVersion}")
    implementation ("org.http4k:http4k-core:${http4kVersion}")
    implementation ("org.http4k:http4k-contract:${http4kVersion}")
    implementation ("com.auth0:java-jwt:4.2.1")

    implementation ("org.http4k:http4k-server-jetty:${http4kVersion}")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
    implementation ("org.http4k:http4k-format-kotlinx-serialization:${http4kVersion}")
    implementation("org.apache.logging.log4j:log4j-core:$log4jVersion")
    implementation("org.apache.logging.log4j:log4j-api:$log4jVersion")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:$log4jVersion")
    implementation("org.litote.kmongo:kmongo:4.7.1")
    implementation ("org.litote.kmongo:kmongo-id-serialization:4.8.0")
    implementation ("org.litote.kmongo:kmongo-serialization:4.8.0")
    implementation ("org.litote.kmongo:kmongo-serialization:4.7.1")
    implementation ("org.http4k:http4k-serverless-core:${http4kVersion}")
    implementation("org.http4k:http4k-cloudnative:${http4kVersion}")
    implementation("org.http4k:http4k-cloudevents:4.33.1.0")
    implementation("org.http4k:http4k-client-apache:${http4kVersion}")


    implementation ("com.apurebase:kgraphql:$kgraphql_version")
    implementation("org.http4k:http4k-graphql:${http4kVersion}")

    implementation ("org.http4k:http4k-serverless-core:${http4kVersion}")
    implementation ("org.http4k:http4k-serverless-lambda:${http4kVersion}")
    implementation ("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${kotlinVersion}")

    implementation("io.insert-koin:koin-core:$koin_version")
    implementation("io.insert-koin:koin-annotations:$koin_ksp_version")
    ksp("io.insert-koin:koin-ksp-compiler:$koin_ksp_version")

    testImplementation("de.flapdoodle.embed:de.flapdoodle.embed.mongo:4.4.0")
    testImplementation(platform("org.junit:junit-bom:${junitVersion}"))
    testImplementation("org.http4k:http4k-testing-hamkrest")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-engine")

    testImplementation("io.insert-koin:koin-test:$koin_version")
    // Koin for JUnit 4
    // Koin for JUnit 5
    testImplementation("io.insert-koin:koin-test-junit5:$koin_version")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")


}




application {
    mainClass.set("com.simon.DayOffHttp4KKt")
}
java {
    sourceCompatibility = JavaVersion.toVersion("11")
}

graalvmNative {
    toolchainDetection.set(false)
    agent {
//        callerFilterFiles.from("filter.json")
//        accessFilterFiles.from("filter.json")
        builtinCallerFilter.set(true)
        builtinHeuristicFilter.set(true)
        enableExperimentalPredefinedClasses.set(false)
        enableExperimentalUnsafeAllocationTracing.set(false)
        trackReflectionMetadata.set(true)

        // Copies metadata collected from tasks into the specified directories.
        metadataCopy {
            inputTaskNames.add("test") // Tasks previously executed with the agent attached.
            outputDirectories.add("src/main/resources/META-INF/native-image")
            mergeWithExisting.set(true) // Instead of copying, merge with existing metadata in the output directories.
        }

    }
    binaries {
        named("main") {
            buildArgs.add("-H:+StaticExecutableWithDynamicLibC")
            buildArgs.add("--initialize-at-run-time=org.apache.logging.log4j.spi.Provider")
            // Main options
            mainClass.set("com.simon.DayOffHttp4KKt") // The main class to use, defaults to the application.mainClass
            useFatJar.set(true) // Instead of passing each jar individually, builds a fat jar
        }
    }
}


tasks {

//    val versionDetails: groovy.lang.Closure<com.palantir.gradle.gitversion.VersionDetails> by extra
//    val details = versionDetails()
//    details.lastTag
//    details.commitDistance
//    details.gitHash
//    details.gitHashFull // full 40-character Git commit hash
//    details.branchName // is null if the repository in detached HEAD mode
//    details.isCleanTag
//    version = details.gitHash

    sonarqube {
        properties {
            property("sonar.sources", "src/main/kotlin")
        }
    }

    create("printVersionProjects") {
        val result = File("versions.properties")
        result.appendText("${name}=${version}\n")
    }


    if (!project.hasProperty("jvmAtf")) {
        jib {
//            if(!details.isCleanTag) {
//                throw GradleException("A tag shall be provided for pushing artifacts")
//            }
            val dockerUsername = findProperty("DOCKERHUB_USERNAME").toString()
            from {
                image = "prantlf/alpine-glibc"
                auth {
                    username = dockerUsername
                    password = findProperty("DOCKERHUB_PASSWORD").toString()
                }
            }

            pluginExtensions {
                pluginExtension {
                    implementation = "com.google.cloud.tools.jib.gradle.extension.nativeimage.JibNativeImageExtension"
                    properties = mapOf("imageName" to project.name)
                }
            }
            to {
                image = "$dockerUsername/dayoff-${project.name}"
                tags = setOf("${project.version}")
                auth {
                    username = dockerUsername
                    password = findProperty("DOCKERHUB_PASSWORD").toString()
                }
            }
        }
    }
    else {
        jib {
//            if(!details.isCleanTag && !project.hasProperty("dev")) {
//                val message = "A tag shall be provided for pushing artifacts"
//                throw GradleException("A tag shall be provided for pushing artifacts")
//            }
            val dockerUsername = findProperty("DOCKERHUB_USERNAME").toString()
            from {
                image = "adoptopenjdk/openjdk11:jdk-11.0.11_9-alpine-slim"
                auth {
                    username = dockerUsername
                    password = findProperty("DOCKERHUB_PASSWORD").toString()
                }
            }


            to {
                image = "$dockerUsername/dayoff-${project.name}"
                tags = setOf("${project.version}")
                auth {
                    username = dockerUsername
                    password = findProperty("DOCKERHUB_PASSWORD").toString()
                }
            }
        }

    }



    compileKotlin {
        kotlinOptions {
            jvmTarget = "11"
        }
    }
    compileTestKotlin {
        kotlinOptions {
            jvmTarget = "11"
        }
    }

    jib.configure {
        if(project.hasProperty("jvmAtf")) {
            dependsOn(shadowJar)
        }
        else {
            dependsOn(nativeCompile)
        }


    }
    named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
        val nullString: String? = null
        archiveBaseName.set(project.name)
        archiveClassifier.set(nullString)
        archiveVersion.set(nullString)
        mergeServiceFiles()
        manifest {
            attributes(mapOf("Main-Class" to "com.simon.DayOffHttp4Kkt"))
        }
    }


    build {
        dependsOn(jib)
    }
}


tasks.withType<Test> {

    useJUnitPlatform()
}








