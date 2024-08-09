import SwiftUI
import shared

@main
struct iOSApp: App {
    
    init() {
        Injector_iosKt.doInitKoin(
            iosHardwareDataProvider: IosHardwareDataProviderImpl.sharedInstance,
            iosSoftwareDataProvider: IosSoftwareDataProviderImpl.sharedInstance
        )
        AppInitializerComponent().doInit()
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
