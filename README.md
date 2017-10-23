<img src="info/icon_glow.png" width="100" height="100" /> <br/>
CPU Info
========
CPU Info provides information about Android device hardware and software.
This project will be next release version after full migration into new MVVM
architecture. Most of the code is written in Kotlin but some old widgets are
still in Java.

Current version can be found on Google Play:<br />
[![Get it on Google Play](info/google-play-badge.png)](https://play.google.com/store/apps/details?id=com.kgurgul.cpuinfo)

Building requirements
=====================
* *res/xml/app_tracker.xml* for Google Analytics
* *google-services.json* file with Firebase configuration

Used libraries
==============
* [Android Architecture Components](https://developer.android.com/topic/libraries/architecture/index.html)
* [RxJava 2](https://github.com/ReactiveX/RxJava)
* [Dagger 2](https://github.com/google/dagger)
* [Gson](https://github.com/google/gson)
* [EventBus](https://github.com/greenrobot/EventBus)
* Small parts from [1](https://github.com/lzyzsd/CircleProgress), [2](https://github.com/akexorcist/Android-RoundCornerProgressBar),
[3](https://github.com/jaredrummler/AndroidProcesses), [4](https://github.com/TUBB/SwipeMenu)

Must have before 3.0 release
============================
* Code cleanup
* Android Architecture Components stable version
* Global testing

Still TODO
==========
* Migration all heavy lifting into coroutines (remove AsyncTask)
* Tests
* Travis integration
* Fix for RAM widget on Android O - or drop it completely

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