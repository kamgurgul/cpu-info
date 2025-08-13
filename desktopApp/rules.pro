-dontnote "module-info"
-dontnote "META-INF**"

# DataStore
-keepclassmembers class * extends androidx.datastore.preferences.protobuf.GeneratedMessageLite {
    <fields>;
}
-dontwarn androidx.datastore.preferences.**

# JNA
-keep class com.sun.jna.** { *; }

# OSHI
-keep class oshi.** { *; }

# Okio
-keep class okio.** { *; }
