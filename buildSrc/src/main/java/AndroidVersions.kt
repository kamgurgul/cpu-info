object AndroidVersions {

    const val MIN_SDK = 21
    const val WEAR_MIN_SDK = 26
    const val TARGET_SDK = 35
    const val COMPILE_SDK = 35

    private const val VERSION_MAJOR = 6
    private const val VERSION_MINOR = 2
    private const val VERSION_PATCH = 0

    const val VERSION_CODE = VERSION_MAJOR * 10000 + VERSION_MINOR * 100 + VERSION_PATCH
    const val WEAR_VERSION_CODE =
        1000000 + VERSION_MAJOR * 10000 + VERSION_MINOR * 100 + VERSION_PATCH
    const val VERSION_NAME = "$VERSION_MAJOR.$VERSION_MINOR.$VERSION_PATCH"

    const val NDK_VERSION = "27.1.12297006"
}
