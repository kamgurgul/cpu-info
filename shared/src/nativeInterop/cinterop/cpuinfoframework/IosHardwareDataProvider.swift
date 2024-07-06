import Foundation

@objc public class IosHardwareDataProvider: NSObject {

    @objc public static let sharedInstance = IosHardwareDataProvider()

    @objc public func getAvailableMemory() -> Int {
        return os_proc_available_memory()
    }

    @objc public func getTotalDiskSpaceInBytes() -> Int64 {
        do {
            let systemAttributes = try FileManager.default
                .attributesOfFileSystem(forPath: NSHomeDirectory() as String)
            let space = (systemAttributes[FileAttributeKey.systemSize] as?NSNumber)?.int64Value
            return space!
        } catch {
            return 0
        }
    }

    @objc public func getFreeDiskSpaceInBytes() -> Int64 {
        do {
            let systemAttributes = try FileManager.default
                .attributesOfFileSystem(forPath: NSHomeDirectory() as String)
            let freeSpace = (systemAttributes[FileAttributeKey.systemFreeSize] as? NSNumber)?.int64Value
            return freeSpace!
        } catch {
            return 0
        }
    }

    private override init() {}
}
