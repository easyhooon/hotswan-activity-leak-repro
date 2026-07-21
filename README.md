# HotSwan Activity Leak Reproduction

Minimal Android project that mirrors the Activity/Compose callback shape involved in an Activity retention observed with HotSwan `2.0.0-beta03`.

The project intentionally excludes navigation libraries, dependency injection, ViewModels, and repositories. It keeps the two lifecycle details involved in the source report: a remembered Activity-bound custom toast wrapper and a `LaunchedEffect`/`repeatOnLifecycle` flow collector whose callback shows the toast and finishes the Activity.

## Environment

- HotSwan `2.0.0-beta03`
- LeakCanary `2.14`
- Android Gradle Plugin `9.0.1`
- Kotlin `2.4.0`
- Compose BOM `2026.06.00`
- Gradle `9.1.0`
- JDK 17
- minSdk 28 / targetSdk 36

## Reproduction attempt

1. Open the project in Android Studio with JDK 17.
2. Build and install the debug app.
3. Tap **Open Activity**.
4. Tap **Finish Activity**. This emits a flow event; the collector shows the custom toast and calls `finish()`.
5. Repeat steps 3 and 4 if LeakCanary does not analyze immediately.
6. Wait for LeakCanary to analyze the destroyed `LeakingActivity`.

This project currently mirrors the reported callback and custom-toast lifecycle, but it has not been confirmed as a standalone reproduction of the HotSwan retention path. HotSwan starts `HotSwanV2Server` lazily when an interpreted content/dispatcher path is used; a regular `ComponentActivity.setContent` call may be skipped by the compiler plugin. Confirm that the device log contains `HotSwanV2Server-accept` before treating a LeakCanary result as evidence for this issue.

The suspected retention path is:

```text
HotSwanV2Server-accept
-> InterpreterDispatcher.interpreter
-> StackInterpreter.recordedCacheValuesByIdentity
-> ConcurrentHashMap
-> Proxy
-> InterpreterLambda captured values
-> destroyed LeakingActivity
```

The original issue was observed after a normal debug launch and Activity finish in the YeoBee application, where the HotSwan dispatcher was active.

## Comparison Without HotSwan

1. Comment out the following line in `app/build.gradle.kts`:

   ```kotlin
   alias(libs.plugins.hotswan.compiler)
   ```

2. Uninstall `dev.hotswan.leakrepro` to clear the previous APK and LeakCanary database.
3. Clean, rebuild, and reinstall the debug app.
4. Repeat the reproduction flow.

In the source application, LeakCanary stopped reporting the destroyed Activity after this comparison setup was used. The custom toast wrapper also clears its Activity reference in `onDestroy`, so a positive result is not explained by an intentionally broken toast cleanup alone.

## Notes

- `debugOnly` is explicitly enabled, so release variants do not contain HotSwan.
- LeakCanary automatically watches destroyed Activities in debug builds.
- A full heap dump may contain application data. Share the text leak trace first.
