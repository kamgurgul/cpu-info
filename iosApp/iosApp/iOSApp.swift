import SwiftUI
import shared

@main
struct iOSApp: App {
    
    init() {
        InjectorKt.doInitKoin()
        AppInitializerComponent().doInit()
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
