import org.gradle.api.JavaVersion.VERSION_1_8

group = "com.sfwltd"
version = "0.1-SNAPSHOT"

buildscript {
    configure(listOf(repositories, project.repositories)) {
        maven {setUrl("https://repo.gradle.org/gradle/repo")}
        maven {setUrl("http://dl.bintray.com/kotlin/kotlin-eap-1.1")}
    }

    repositories {
        mavenCentral()
        mavenLocal()
    }

    dependencies {
        classpath(kotlinModule("gradle-plugin", version = "1.1.0-beta-17"))
        classpath("org.springframework.boot:spring-boot-gradle-plugin:+")
    }
}

apply {
    plugin("java")
    plugin("kotlin")
    plugin("idea")
    plugin("org.springframework.boot")
}

configure<JavaPluginConvention> {
    sourceCompatibility = VERSION_1_8
    targetCompatibility = VERSION_1_8
}

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    compile("org.jetbrains.kotlin:kotlin-reflect:+")
    compile(kotlinModule("stdlib", version = "1.1.0-beta-17"))
    compile("org.springframework.boot:spring-boot-starter-web:+") {
        exclude("spring-boot-starter-tomcat")
    }
    compile("org.springframework.boot:spring-boot-starter-jetty:+")
    compile("com.github.kittinunf.fuel:fuel:+")
    compile("com.beust:klaxon:+")
    compile("redis.clients:jedis:+")

    testCompile("junit:junit")
    testCompile("org.springframework.boot:spring-boot-starter-test")
    testCompile("com.github.tomakehurst:wiremock:+")
    testCompile("com.orange.redis-embedded:embedded-redis:+")
    testCompile("com.natpryce:hamkrest:+")
}