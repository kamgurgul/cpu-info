import SwiftUI

@main
struct iOSApp: App {
    
    init() {
        InjectorKt.doInitKoin()
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
