<img src="info/icon_glow.png" width="100" height="100" />

CPU Info [![Build Status](https://travis-ci.org/kamgurgul/cpu-info.svg?branch=master)](https://travis-ci.org/kamgurgul/cpu-info)<br/>
========
CPU Info provides information about Android device hardware and software.
Most of the code is written in Kotlin but some old widgets are still in
Java.

[<img src="https://f-droid.org/badge/get-it-on.png"
     alt="Get it on F-Droid"
     height="80">](https://f-droid.org/packages/com.kgurgul.cpuinfo/)
[<img src="https://play.google.com/intl/en_us/badges/images/generic/en-play-badge.png"
     alt="Get it on Google Play"
     height="80">](https://play.google.com/store/apps/details?id=com.kgurgul.cpuinfo)

Used libraries
==============
* [Android Architecture Components](https://developer.android.com/topic/libraries/architecture/index.html)
* [RxJava 2](https://github.com/ReactiveX/RxJava)
* [Dagger 2](https://github.com/google/dagger)
* [Gson](https://github.com/google/gson)
* [EventBus](https://github.com/greenrobot/EventBus)
* [Glide](https://github.com/bumptech/glide)
* Small parts from [1](https://github.com/lzyzsd/CircleProgress), [2](https://github.com/akexorcist/Android-RoundCornerProgressBar),
[3](https://github.com/jaredrummler/AndroidProcesses), [4](https://github.com/TUBB/SwipeMenu)

Testing tools
=============
* [Mockito](http://site.mockito.org/)
* [mockito-kotlin](https://github.com/nhaarman/mockito-kotlin)
* [Espresso](https://developer.android.com/training/testing/ui-testing/espresso-testing.html)
* [Jacoco (for coverage)](http://www.eclemma.org/jacoco/)

CI
===
* Travis

Still TODO
==========
* Tests
* Fix for RAM widget on Android O
* Add benchmarks

License
-------
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
