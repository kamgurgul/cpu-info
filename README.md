![Build and test](https://github.com/kamgurgul/cpu-info/actions/workflows/shared_test.yml/badge.svg)

<img src="info/icon_glow.png" width="100" height="100" />

# CPU Info

CPU Info provides information about device hardware and software. 

[<img src="https://f-droid.org/badge/get-it-on.png"
alt="Get it on F-Droid"
height="80">](https://f-droid.org/packages/com.kgurgul.cpuinfo/)
[<img src="https://play.google.com/intl/en_us/badges/images/generic/en-play-badge.png"
alt="Get it on Google Play"
height="80">](https://play.google.com/store/apps/details?id=com.kgurgul.cpuinfo)
[<img src="info/huawei_badge.png"
alt="Get it on HUAWEI AppGallery"
height="80">](https://appgallery.cloud.huawei.com/ag/n/app/C102414279?channelId=Main+badge+&id=fb28f69db40840f8b79b541cc4a13775&s=1378847C6B5A46F97603F316DD1450C7D3F74C023A62827B43619C2D41F2341D&detailType=0&v=&callType=AGDLINK&installType=0000)
[<img src="https://raw.githubusercontent.com/kamgurgul/cpu-info/master/info/get-it-on-github.png"
alt='Get it on GitHub' height="80">](https://github.com/kamgurgul/cpu-info/releases/latest)
[<img src="https://raw.githubusercontent.com/kamgurgul/cpu-info/master/info/amazon-badge.png"
alt='Available on Amazon AppStore' height="80">](https://www.amazon.com/Kamil-Gurgul-KG-Soft-Info/dp/B088FYQTYR/ref=sr_1_5?keywords=cpu+info&qid=1661020642&s=mobile-apps&sr=1-5)
[<img src="info/app-store-badge.png"
alt="Download on the App Store"
height="80">](https://apps.apple.com/us/app/cpu-info/id6560116815)
[<img src="info/flathub_badge.png"
alt="Get it on Flathub"
height="80">](https://flathub.org/apps/com.kgurgul.cpuinfo)
[<img src="info/ms_badge.png"
alt="Get it from Microsoft"
height="80">](https://apps.microsoft.com/detail/9phxq0f4knbl)
[<img src="info/get-it-on-homebrew.png"
alt="Get it from Homebrew"
height="80">](https://formulae.brew.sh/cask/cpu-info)

# Supported platforms 

| Android | Android TV | Wear OS | iOS | Desktop |                                                                                   Wasm                                                                                   |
|:-------:|:----------:|:-------:|:---:|:-------:|:------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|
|    ✅    |     ✅      |    ✅    |  ✅  |    ✅    | ✅<br/>[In preview](https://kgurgul.com/assets/cpuinfo/)<br/>Known issues: [1](https://youtrack.jetbrains.com/issue/CMP-6900/NavigationRail-items-not-visible-on-WASM-JS) |

# Tech stack

* [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)
* [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
* [Jetpack ViewModel/Lifecycle](https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-lifecycle.html)
* [Coroutines](https://github.com/Kotlin/kotlinx.coroutines)
* [Koin](https://github.com/InsertKoinIO/koin)
* [Coil](https://github.com/coil-kt/coil)
* [DataStore](https://developer.android.com/kotlin/multiplatform/datastore)
* [pytorch/cpuinfo](https://github.com/pytorch/cpuinfo)
* [OSHI](https://github.com/oshi/oshi)

# TODO

* Add about section
* Upload Wear OS app to Play Store when SDK 35 will be supported

# License

    Copyright 2017 KG Soft

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
