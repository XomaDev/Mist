plugins {
  id("java")
  kotlin("jvm")

  id("war")
  id("org.teavm") version("0.11.0")
}

group = "me.ekita.mist"
version = "1.0-SNAPSHOT"

repositories {
  mavenCentral()
}

dependencies {
  testImplementation(platform("org.junit:junit-bom:5.10.0"))
  testImplementation("org.junit.jupiter:junit-jupiter")

  implementation(kotlin("stdlib-jdk8"))
  implementation("org.json:json:20250107")
}

teavm {
  all {
    mainClass = "me.ekita.mist.Mist"
  }
  js {
    addedToWebApp = false
    targetFileName = "mist.js"
  }
  wasmGC {
    addedToWebApp = true
    targetFileName = "mist.wasm"
  }
}

tasks.test {
  useJUnitPlatform()
}

kotlin {
  // While building for Android we'll do it with Java 7/8
  jvmToolchain(11)
}