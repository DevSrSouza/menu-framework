plugins {
    id("org.jetbrains.compose") version "0.4.0-build182"
}

repositories {
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
}

dependencies {
    implementation(compose.runtime)
}