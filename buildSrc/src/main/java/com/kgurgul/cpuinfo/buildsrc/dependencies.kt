package com.kgurgul.cpuinfo.buildsrc

object Libs {

    const val androidGradlePlugin = "com.android.tools.build:gradle:7.0.3"

    const val gradleVersionsPlugin = "com.github.ben-manes:gradle-versions-plugin:0.39.0"

    const val bus = "org.greenrobot:eventbus:3.2.0"

    const val timber = "com.jakewharton.timber:timber:5.0.1"

    const val relinker = "com.getkeepsafe.relinker:relinker:1.4.4"

    const val junit = "junit:junit:4.13.2"

    const val mockitoCore = "org.mockito:mockito-core:4.0.0"

    const val mockitoKotlin = "com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0"

    const val coil = "io.coil-kt:coil-compose:1.4.0"

    object Kotlin {
        private const val version = "1.5.31"
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$version"
        const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
    }

    object Coroutines {
        private const val version = "1.5.2"
        const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
        const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
        const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$version"
    }

    object AndroidX {
        const val coreKtx = "androidx.core:core-ktx:1.7.0"
        const val activityKtx = "androidx.activity:activity-compose:1.4.0"
        const val fragmentKtx = "androidx.fragment:fragment-ktx:1.3.6"
        const val appCompat = "androidx.appcompat:appcompat:1.3.1"
        const val preference = "androidx.preference:preference:1.1.1"
        const val swipeRefreshLayout = "androidx.swiperefreshlayout:swiperefreshlayout:1.1.0"
        const val constraintLayout = "androidx.constraintlayout:constraintlayout:2.1.1"
        const val multiDex = "androidx.multidex:multidex:2.0.1"
        const val viewPager2 = "androidx.viewpager2:viewpager2:1.0.0"
        const val datastorePreferences = "androidx.datastore:datastore-preferences:1.0.0"

        object Lifecycle {
            private const val version = "2.4.0"
            const val extensions = "androidx.lifecycle:lifecycle-extensions:2.2.0"
            const val viewModelKtx = "androidx.lifecycle:lifecycle-viewmodel-compose:$version"
            const val liveDataKtx = "androidx.lifecycle:lifecycle-livedata-ktx:$version"
            const val common = "androidx.lifecycle:lifecycle-common-java8:$version"
        }

        object Navigation {
            private const val version = "2.3.5"
            const val fragment = "androidx.navigation:navigation-fragment-ktx:$version"
            const val ui = "androidx.navigation:navigation-ui-ktx:$version"
            const val safeArgs = "androidx.navigation:navigation-safe-args-gradle-plugin:$version"
        }

        object Compose {
            const val version = "1.1.0-beta02"
            const val material = "androidx.compose.material:material:$version"
            const val animations = "androidx.compose.animation:animation:$version"
            const val uiTooling = "androidx.compose.ui:ui-tooling:$version"
            const val uiTests = "androidx.compose.ui:ui-test-junit4:$version"
            const val swipeToRefresh = "com.google.accompanist:accompanist-swiperefresh:0.21.1-beta"
        }

        object Test {
            private const val version = "1.4.0"
            const val core = "androidx.test:core:$version"
            const val runner = "androidx.test:runner:$version"
            const val rules = "androidx.test:rules:$version"

            const val archCoreTesting = "androidx.arch.core:core-testing:2.1.0"
            const val jUnitExt = "androidx.test.ext:junit:1.1.3"
            const val orchestrator = "androidx.test:orchestrator:1.4.0"

            object Espresso {
                private const val version = "3.4.0"
                const val core = "androidx.test.espresso:espresso-core:$version"
                const val contrib = "androidx.test.espresso:espresso-contrib:$version"
            }
        }
    }

    object Google {
        const val material = "com.google.android.material:material:1.4.0"
        const val gson = "com.google.code.gson:gson:2.8.9"
    }

    object Rx {
        const val rxJava = "io.reactivex.rxjava3:rxjava:3.1.2"
        const val rxAndroid = "io.reactivex.rxjava3:rxandroid:3.0.0"
    }

    object Hilt {
        private const val version = "2.40"
        const val gradlePlugin = "com.google.dagger:hilt-android-gradle-plugin:$version"
        const val android = "com.google.dagger:hilt-android:$version"
        const val androidCompiler = "com.google.dagger:hilt-android-compiler:$version"
        const val androidTesting = "com.google.dagger:hilt-android-testing:$version"
    }

    object Glide {
        private const val version = "4.12.0"
        const val glide = "com.github.bumptech.glide:glide:$version"
        const val compiler = "com.github.bumptech.glide:compiler:$version"
    }

    object Airbnb {
        private const val version = "4.6.4"
        const val epoxy = "com.airbnb.android:epoxy:$version"
        const val processor = "com.airbnb.android:epoxy-processor:$version"
        const val dataBinding = "com.airbnb.android:epoxy-databinding:$version"
    }
}