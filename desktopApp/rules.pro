-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.atomicfu.**
-dontwarn org.slf4j.**

# Obfuscation breaks coroutines/ktor for some reason
-dontobfuscate