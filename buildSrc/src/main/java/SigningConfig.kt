import java.io.File
import java.io.FileInputStream
import java.util.*

object SigningConfig {

    const val KEY_PATH = "keyPath"
    const val KEY_PASS = "keyPass"
    const val KEY_ALIAS = "keyAlias"

    fun getDebugProperties(rootDir: File) = Properties().apply {
        setProperty(KEY_PATH, "${rootDir.path}/debug.keystore")
        setProperty(KEY_PASS, "android")
        setProperty(KEY_ALIAS, "androiddebugkey")
        setProperty(KEY_PASS, "android")
    }

    fun getReleaseProperties(rootDir: File): Properties {
        val releaseProperties = Properties().apply {
            load(FileInputStream(File(rootDir, "local.properties")))
        }
        return if (releaseProperties.getProperty(KEY_PATH).isNotEmpty()
            && releaseProperties.getProperty(KEY_PASS).isNotEmpty()
            && releaseProperties.getProperty(KEY_ALIAS).isNotEmpty()
        ) {
            releaseProperties
        } else {
            println("!!!Warning: release keystore not found -> using debug!!!")
            getDebugProperties(rootDir)
        }
    }
}