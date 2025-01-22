# Remove logs
-assumenosideeffects class co.touchlab.kermit.Logger {
    public *** v(...);
    public *** d(...);
    public *** i(...);
    public *** e(...);
}

# Keep model classes
-keep class com.kgurgul.cpuinfo.data.local.model.** { *; }
