import java.io.File
import java.io.FileInputStream
import java.util.Properties

object SigningConfig {

    const val KEY_PATH = "KEYSTORE_PATH"
    const val KEY_PASS = "KEYSTORE_PASS"
    const val KEY_ALIAS = "KEYSTORE_ALIAS"

    fun getDebugProperties(rootDir: File) = Properties().apply {
        setProperty(KEY_PATH, "${rootDir.path}/androidApp/debug.keystore")
        setProperty(KEY_PASS, "android")
        setProperty(KEY_ALIAS, "androiddebugkey")
        setProperty(KEY_PASS, "android")
    }

    fun getReleaseProperties(rootDir: File): Properties {
        val releaseProperties = Properties()
        try {
            releaseProperties.load(FileInputStream(File(rootDir, "local.properties")))
        } catch (e: Exception) {
            println("Cannot load local.properties")
        }
        return if (releaseProperties.getProperty(KEY_PATH, "").isNotEmpty()
            && releaseProperties.getProperty(KEY_PASS, "").isNotEmpty()
            && releaseProperties.getProperty(KEY_ALIAS, "").isNotEmpty()
        ) {
            println("Using local.properties for signing")
            releaseProperties
        } else if (!System.getenv(KEY_PATH).isNullOrEmpty()
            && !System.getenv(KEY_PASS).isNullOrEmpty()
            && !System.getenv(KEY_ALIAS).isNullOrEmpty()
        ) {
            println("Using system env variables for signing")
            releaseProperties[KEY_PATH] = System.getenv(KEY_PATH)
            releaseProperties[KEY_PASS] = System.getenv(KEY_PASS)
            releaseProperties[KEY_ALIAS] = System.getenv(KEY_ALIAS)
            releaseProperties
        } else {
            println("!!!Warning: release keystore not found -> using debug!!!")
            getDebugProperties(rootDir)
        }
    }
}