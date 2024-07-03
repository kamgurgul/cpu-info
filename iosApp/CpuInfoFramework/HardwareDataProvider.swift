import Foundation

@objc public class HardwareDataProvider: NSObject {
    
    @objc public static let sharedInstance = HardwareDataProvider()
    
    @objc public func getAvailableMemory() -> Int {
        return os_proc_available_memory()
    }
    
    private override init() {} 
}
