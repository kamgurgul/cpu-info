import Foundation

@objc public class IosHardwareDataProvider: NSObject {

    @objc public static let sharedInstance = IosHardwareDataProvider()

    @objc public func getAvailableMemory() -> Int {
        return os_proc_available_memory()
    }

    private override init() {}
}