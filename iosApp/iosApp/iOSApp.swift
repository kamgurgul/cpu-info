import SwiftUI
import shared

@main
struct iOSApp: App {
    
    init() {
        InjectorKt.doInitKoin(
            iosHardwareDataProvider: IosHardwareDataProviderImpl.sharedInstance
        )
        AppInitializerComponent().doInit()
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
