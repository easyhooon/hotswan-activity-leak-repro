# HotSwan Activity Leak Reproduction

Minimal Android project for an Activity retention observed with HotSwan `2.0.0-beta03`.

The project intentionally excludes navigation libraries, dependency injection, ViewModels, repositories, and custom Toast implementations. The only retained value under test is a remembered Compose callback that captures `LeakingActivity`.

## Environment

- HotSwan `2.0.0-beta03`
- LeakCanary `2.14`
- Android Gradle Plugin `9.0.1`
- Kotlin `2.4.0`
- Compose BOM `2026.06.00`
- Gradle `9.1.0`
- JDK 17
- minSdk 28 / targetSdk 36

## Reproduction

1. Open the project in Android Studio with JDK 17.
2. Build and install the debug app.
3. Tap **Open Activity**.
4. Tap **Finish Activity**.
5. Repeat steps 3 and 4 if LeakCanary does not analyze immediately.
6. Wait for LeakCanary to report the destroyed `LeakingActivity`.

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

No HotSwan reload needs to be triggered. The original issue was observed after a normal debug launch and Activity finish.

## Comparison Without HotSwan

1. Comment out the following line in `app/build.gradle.kts`:

   ```kotlin
   alias(libs.plugins.hotswan.compiler)
   ```

2. Uninstall `dev.hotswan.leakrepro` to clear the previous APK and LeakCanary database.
3. Clean, rebuild, and reinstall the debug app.
4. Repeat the reproduction flow.

In the source application, LeakCanary stopped reporting the destroyed Activity after this comparison setup was used.

## Notes

- `debugOnly` is explicitly enabled, so release variants do not contain HotSwan.
- LeakCanary automatically watches destroyed Activities in debug builds.
- A full heap dump may contain application data. Share the text leak trace first.
