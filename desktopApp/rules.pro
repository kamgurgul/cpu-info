-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.atomicfu.**
-dontwarn org.slf4j.**

# Obfuscation breaks coroutines/ktor for some reason
-dontobfuscate

# DataStore
-keepclassmembers class * extends androidx.datastore.preferences.protobuf.GeneratedMessageLite {
    <fields>;
}
-dontwarn androidx.datastore.preferences.**

# Compose
-optimizations !class/enum/unboxing
-keep,includecode,allowobfuscation,allowshrinking class androidx.compose.runtime.SnapshotStateKt** { *; }
-dontwarn androidx.compose.runtime.**

# JNA
-keep class com.sun.jna.** { *; }

# OSHI
-keep class oshi.** { *; }

# Okio
-keep class okio.** { *; }
