# Kotlin
-dontwarn kotlin.**

-keepclassmembers class **$WhenMappings {
    <fields>;
}

# Dagger
-dontwarn com.google.errorprone.annotations.*

# EventBus
#-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

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

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

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

# VerifyError in Android 4
# https://github.com/material-components/material-components-android/issues/397
-keep class com.google.android.material.tabs.TabLayout$Tab {
*;
}