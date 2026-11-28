import Foundation
import shared

@objc public class IosHardwareDataProviderImpl: NSObject, IosHardwareDataProvider {

    @objc public static let sharedInstance = IosHardwareDataProviderImpl()

    @objc public func getAvailableMemory() -> Int64 {
        return Int64(os_proc_available_memory())
    }

    @objc public func getTotalDiskSpaceInBytes() -> Int64 {
        do {
            let systemAttributes = try FileManager.default
                .attributesOfFileSystem(forPath: NSHomeDirectory() as String)
            let space = (systemAttributes[FileAttributeKey.systemSize] as?
                         NSNumber)?.int64Value
            return space ?? 0
        } catch {
            return 0
        }
    }

    @objc public func getFreeDiskSpaceInBytes() -> Int64 {
        // Includes purgeable space, so it matches the value presented in the Settings app
        let homeUrl = URL(fileURLWithPath: NSHomeDirectory())
        if let values = try? homeUrl.resourceValues(forKeys: [.volumeAvailableCapacityForImportantUsageKey]),
           let capacity = values.volumeAvailableCapacityForImportantUsage,
           capacity > 0 {
            return capacity
        }
        do {
            let systemAttributes = try FileManager.default
                .attributesOfFileSystem(forPath: NSHomeDirectory() as String)
            let freeSpace = (systemAttributes[FileAttributeKey.systemFreeSize] as? NSNumber)?.int64Value
            return freeSpace ?? -1
        } catch {
            return -1
        }
    }

    private override init() {}
}
