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

# MacOSIconDecoder - Coil decoder for .icns files, loaded dynamically
-keep class com.kgurgul.cpuinfo.utils.MacOSIconDecoder { *; }
-keep class com.kgurgul.cpuinfo.utils.MacOSIconDecoder$* { *; }

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

# TwelveMonkeys - ImageIO plugin for ICNS files, loaded via Java SPI
-keep class com.twelvemonkeys.** { *; }
-dontnote com.twelvemonkeys.**

# Java ImageIO SPI - keep service provider implementations
-keep class javax.imageio.** { *; }
-keepclassmembers class * implements javax.imageio.spi.ImageReaderSpi { *; }
-keepclassmembers class * implements javax.imageio.spi.ImageWriterSpi { *; }
-keepclassmembers class * extends javax.imageio.spi.IIOServiceProvider { *; }

# Skia - used by MacOSIconDecoder for bitmap conversion
-keep class org.jetbrains.skia.** { *; }
-dontnote org.jetbrains.skia.**

# Coil3 - image loading library, uses reflection for decoder registration
-keep class coil3.** { *; }
-dontnote coil3.**
