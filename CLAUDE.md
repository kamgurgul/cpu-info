# Project

This is a compose multiplatform project build for Android, iOS, JVM and web. App is focused around 
hardware and software of the devices. App is built using MVVM architecture with AndroidX lifecycle 
and Kotlin Coroutines for asynchronous programming. It is using DataStore for key-value storage. 

# Commands

- Run tests: `./gradlew allTests`
- Format code: `./gradlew spotlessApply`

# Code style

- Use 4 spaces for indentation
- Pass data to VM with Interactor
- From VM expose data as StateFlow using single UiState
- For events use Channel<Event>(Channel.BUFFERED) and observe them in compose with ObserveAsEvents
- In VM use `on` prefix for callback functions like `onEvent`, `onClick` etc.
- Use composeResources for all strings
