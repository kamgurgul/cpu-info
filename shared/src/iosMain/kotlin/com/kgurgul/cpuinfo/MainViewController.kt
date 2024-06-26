import androidx.compose.ui.window.ComposeUIViewController
import com.kgurgul.cpuinfo.features.HostScreen
import com.kgurgul.cpuinfo.ui.theme.CpuInfoTheme

fun MainViewController() = ComposeUIViewController {
    CpuInfoTheme {
        HostScreen()
    }
}