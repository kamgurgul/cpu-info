enum class ReleaseType(private val level: Int) {
    SNAPSHOT(0),
    DEV(1),
    ALPHA(10),
    BETA(20),
    RC(60),
    RELEASE(100);

    fun isEqualOrMoreStableThan(other: ReleaseType): Boolean = level >= other.level

    fun isLessStableThan(other: ReleaseType): Boolean = level < other.level
}

object DependencyUpdates {
    private val stableKeywords = arrayOf("RELEASE", "FINAL", "GA")
    private val releaseRegex = "^[0-9,.v-]+(-r)?$".toRegex(RegexOption.IGNORE_CASE)
    private val rcRegex = releaseKeywordRegex("rc")
    private val betaRegex = releaseKeywordRegex("beta")
    private val alphaRegex = releaseKeywordRegex("alpha")
    private val devRegex = releaseKeywordRegex("dev")

    @JvmStatic
    fun versionToRelease(version: String): ReleaseType {
        val stableKeyword = stableKeywords.any { version.uppercase().contains(it) }
        if (stableKeyword) return ReleaseType.RELEASE

        return when {
            releaseRegex.matches(version) -> ReleaseType.RELEASE
            rcRegex.matches(version) -> ReleaseType.RC
            betaRegex.matches(version) -> ReleaseType.BETA
            alphaRegex.matches(version) -> ReleaseType.ALPHA
            devRegex.matches(version) -> ReleaseType.DEV
            else -> ReleaseType.SNAPSHOT
        }
    }

    private fun releaseKeywordRegex(keyword: String): Regex {
        return "^[0-9,.v-]+(-$keyword[0-9]*)$".toRegex(RegexOption.IGNORE_CASE)
    }
}
