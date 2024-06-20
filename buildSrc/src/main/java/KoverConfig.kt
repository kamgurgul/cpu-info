object KoverConfig {

    val includedPackages = listOf("com.kgurgul.cpuinfo.*")
    val excludedPackages = listOf(
        "com.kgurgul.cpuinfo.widgets",
        "com.kgurgul.cpuinfo.ui",
        "com.kgurgul.cpuinfo.*.model*",
        "com.kgurgul.cpuinfo.*.models*",
    )
    val excludedClasses = listOf(
        "*Module*",
        "com.kgurgul.cpuinfo.*Directions*",
        "*MembersInjector*",
        "*_Companion_*",
        "*_Factory*",
    )
    val excludedAnnotations = "*Generated*"
}