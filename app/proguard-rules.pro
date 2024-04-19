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

# Remove logs
-assumenosideeffects class timber.log.Timber* {
    public static *** v(...);
    public static *** d(...);
    public static *** i(...);
    public static *** e(...);
}
