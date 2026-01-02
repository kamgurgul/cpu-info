-dontnote "module-info"
-dontnote "META-INF**"

# DataStore
-keepclassmembers class * extends androidx.datastore.preferences.protobuf.GeneratedMessageLite {
    <fields>;
}
-dontwarn androidx.datastore.preferences.**

# JNA - keep all classes and suppress reflection notes
-keep class com.sun.jna.** { *; }
-dontnote com.sun.jna.**

# WindowsExeIconDecoder - JNA interfaces must be kept with original names
-keep class com.kgurgul.cpuinfo.utils.WindowsExeIconDecoder { *; }
-keep class com.kgurgul.cpuinfo.utils.WindowsExeIconDecoder$* { *; }

# OSHI
-keep class oshi.** { *; }
-dontnote oshi.**
# OSHI optional dependency (not included, but dynamically referenced)
-dontwarn io.github.pandalxb.jlibrehardwaremonitor.**

# Okio
-keep class okio.** { *; }

# Kotlin annotations
-dontwarn kotlin.Deprecated$Container

# Compose Material3 Adaptive
-dontwarn androidx.compose.material3.adaptive.navigationsuite.**

# SLF4J
-dontnote org.slf4j.**

# TwelveMonkeys
-dontnote com.twelvemonkeys.**
