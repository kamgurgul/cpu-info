# Building xcframework

* xcodebuild archive -project iosApp.xcodeproj -scheme CpuInfoFramework -destination "
  generic/platform=iOS" -archivePath "archives/iOS"
* xcodebuild archive -project iosApp.xcodeproj -scheme CpuInfoFramework -destination "
  generic/platform=iOS Simulator" -archivePath "archives/iOS-sim"
* xcodebuild -create-xcframework -archive archives/iOS.xcarchive -framework
  CpuInfoFramework.framework -archive archives/iOS-sim.xcarchive -framework
  CpuInfoFramework.framework -output xcframeworks/CpuInfoFramework.xcframework

