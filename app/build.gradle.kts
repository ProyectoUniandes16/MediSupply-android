plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-parcelize")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.0"
    id("org.jetbrains.kotlinx.kover") version "0.9.3"
    alias(libs.plugins.ktlint)
    id("jacoco")
}

android {
    namespace = "com.uniandes.medisupply"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.uniandes.medisupply"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            enableAndroidTestCoverage = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.0"
    }

    // Exclude duplicate META-INF files that come from test/runtime dependencies
    // This resolves DuplicateRelativeFileException during merge of java/resources
    packaging {
        resources {
            excludes += setOf(
                "META-INF/LICENSE.md",
                "META-INF/LICENSE-notice.md",
                "META-INF/NOTICE",
                "META-INF/NOTICE.txt",
                "META-INF/AL2.0",
                "META-INF/LGPL2.1",
                "META-INF/*.kotlin_module"
            )
        }
    }
}

kover {
    reports {
        filters {
            excludes {
                classes(
                    "com.uniandes.medisupply.MediSupplyApp*",
                    "com.uniandes.medisupply.common.BaseActivity*",
                    "com.uniandes.medisupply.common.NetworkModule*",
                    "com.uniandes.medisupply.common.NavigationProvider*",
                    "com.uniandes.medisupply.presentation.navigation.*",
                    "com.uniandes.medisupply.presentation.containers.*",
                    "com.uniandes.medisupply.presentation.component.*",
                    "com.uniandes.medisupply.di.*",
                    "com.uniandes.medisupply.presentation.ui.*"
                )
            }
        }
        verify {
            rule {
                disabled = false
                bound {
                    minValue = 80
                }
            }
        }
    }
}

jacoco {
    toolVersion = "0.8.7"
}

tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("connectedDebugAndroidTest")

    val coverageFile = fileTree("$buildDir/outputs/code_coverage/debugAndroidTest/connected") {
        include("**/*.ec")
    }
    executionData.setFrom(coverageFile)
    sourceDirectories.setFrom(files("src/main/java", "src/main/kotlin"))

    classDirectories.setFrom(
        files(
            fileTree("$buildDir/intermediates/javac/debug") {
                include("com/uniandes/medisupply/presentation/ui/feature/**/*.class")
                exclude("**/*Preview*.class")
            },
            fileTree("$buildDir/tmp/kotlin-classes/debug") {
                include("com/uniandes/medisupply/presentation/ui/feature/**/*.class")
                exclude("**/*Preview*.class")
            }

        )
    )

    reports {
        html.required.set(true)
        xml.required.set(true)
        csv.required.set(false)
    }
}
tasks.register("verifyJacocoCoverage") {
    mustRunAfter("jacocoTestReport")
    doLast {
        println("== Running verifyJacocoCoverage ==")
        val reportFile = file("$buildDir/reports/jacoco/jacocoTestReport/jacocoTestReport.xml")
        println("XML report exists: ${reportFile.exists()}")
        if (!reportFile.exists()) {
            throw GradleException("Coverage XML report not found at ${reportFile.absolutePath}")
        }

        val factory = javax.xml.parsers.DocumentBuilderFactory.newInstance()
        factory.isValidating = false
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
        val parser = factory.newDocumentBuilder()
        val doc = parser.parse(reportFile)
        val counters = doc.getElementsByTagName("counter")
        var missed = 0
        var covered = 0
        for (i in 0 until counters.length) {
            val c = counters.item(i)
            if (c.attributes.getNamedItem("type").nodeValue == "INSTRUCTION") {
                missed = c.attributes.getNamedItem("missed").nodeValue.toInt()
                covered = c.attributes.getNamedItem("covered").nodeValue.toInt()
            }
        }
        val percent = if (missed + covered == 0) 100.0 else covered.toDouble() / (missed + covered) * 100.0
        val minCoverage = 10.0
        println("Jacoco Instruction coverage: $percent% (min required: $minCoverage%)")

        if (percent < minCoverage) {
            throw GradleException("Coverage ($percent%) is below threshold ($minCoverage%)!")
        }
    }
}

dependencies {
    implementation(kotlin("test"))
    implementation(kotlin("test-junit"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.navigation)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.retrofit2.retrofit)
    implementation(libs.retrofit2.converter)
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    implementation(libs.androidx.compose.material)
    implementation(libs.coil.compose)
    implementation(libs.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.mockk.android)
    androidTestImplementation(libs.mockk.agent)
    androidTestImplementation(libs.koin.test)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.koin.android)
    implementation(libs.koin.compose.viewmodel)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.6.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0")
    testImplementation(libs.androidx.junit)
    testImplementation(libs.mockk.android)
    testImplementation(libs.mockk.agent)
    testImplementation(libs.koin.test)
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")
}
