import Foundation

@objcMembers public class HardwareDataProvider: NSObject {
    
    public func getAvailableMemory() -> Int {
        return os_proc_available_memory()
    }
}
