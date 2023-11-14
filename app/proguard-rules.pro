# Kotlin
-dontwarn kotlin.**

-keepclassmembers class **$WhenMappings {
    <fields>;
}

# Dagger
-dontwarn com.google.errorprone.annotations.*

# General
-renamesourcefileattribute SourceFile
-keepattributes Exceptions,InnerClasses,Signature,SourceFile,LineNumberTable,*Annotation*

# AIDL
-keep class android.content.pm.IPackageStatsObserver$** {
    public <fields>;
    public <methods>;
}
-keep class android.content.pm.IPackageStatsObserver$Stub.** {
    public <fields>;
    public <methods>;
}
-keep interface android.content.pm.IPackageStatsObserver$** {*;}

# Coroutines
# ServiceLoader support
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
# Most of volatile fields are updated with AFU and should not be mangled
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# Remove logs
-assumenosideeffects class timber.log.Timber* {
    public static *** v(...);
    public static *** d(...);
    public static *** i(...);
    public static *** e(...);
}
