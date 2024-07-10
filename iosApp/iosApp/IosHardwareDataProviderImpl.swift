import Foundation
import UIKit
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
    
    @objc public func getScreenWidth() -> Int32 {
        let widthPt = UIScreen.main.bounds.size.width
        let scale = UIScreen.main.scale
        return Int32(widthPt * scale)
    }
    
    @objc public func getScreenHeight() -> Int32 {
        let heightPt = UIScreen.main.bounds.size.height
        let scale = UIScreen.main.scale
        return Int32(heightPt * scale)
    }
    
    @objc public func getScreenScale() -> Float {
        return Float(UIScreen.main.scale)
    }
    
    @objc public func getScreenBrightness() -> Float {
        return Float(UIScreen.main.brightness)
    }
    
    @objc public func getScreenMaximumFramesPerSecond() -> Int32 {
        return Int32(UIScreen.main.maximumFramesPerSecond)
    }
    
    @objc public func getScreenCalibratedLatency() -> Double {
        return UIScreen.main.calibratedLatency
    }

    private override init() {}
}
