import UIKit
import SwiftUI
import shared

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        if #available(iOS 26.0, *) {
            // Navigation elements are rendered natively to get the Liquid Glass appearance
            let host = LiquidGlassNavigationViewController()
            host.embed(MainViewControllerKt.MainViewController(nativeNavigationRenderer: host))
            return host
        }
        return MainViewControllerKt.MainViewController(nativeNavigationRenderer: nil)
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        ComposeView()
            .ignoresSafeArea()
    }
}
