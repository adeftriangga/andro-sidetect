# 🎯 `andro-sidetect` - Android Sideload & Accessibility Detection Library
**Detect sideloaded apps and accessibility service risks with confidence. Lightweight, reusable, and ideal for security-aware apps and beyond.**

---



## ✅ Why Use `andro-sidetect`?

Malicious APKs often:
- Use **social engineering** to trick users into sideloading apps
- **Enable Accessibility Services** to take control of the device

`andro-sidetect` helps your app:
- Detect if it was **sideloaded** (not from Play Store)
- Check if **Accessibility Service is enabled** (a common malware vector)
- Report detection results via reusable callbacks
- Be **privacy-aware** and compatible with modern Android versions

---


## 🔧 Features

| Feature | Description |
|-------|-----------|
| 🔍 Sideloading Detection | Checks installer package name with fallbacks |
| 🛡️ Accessibility Service Check | Detects if accessibility service is enabled |
| 🔄 Configurable Allowlist | Easily add/remove trusted installers (Oppo, Vivo, Samsung, etc.) |
| 📱 OEM Installer Support | Pre-configured for: Samsung, Xiaomi, Oppo, Vivo, Huawei, Realme, etc. |
| 💾 Backup/Restore Detection | Detects tools like *Android Easy Mover* |
| 📁 Modular & Reusable | Return `DetectionResult` via callback—easy to integrate |
| 🌐 Android 11+ Ready | Handles `queries` and `QUERY_ALL_PACKAGES` permissions correctly |

---

## 📦 Installation

Add to your `build.gradle` (Module: app):

```groovy
dependencies {
    implementation 'id.supa:andro-sidetect:1.1.0'
}
```
>⚠️ Note: This library requires androidx.core:core-ktx (already in AndroidX projects).
---


## 🛠️ Usage Example

```groovy
val detector = AndroidSideloadingDetector(context)

detector.detect { result ->
    when {
        result.isSideloadingDetected -> {
            Log.w("Security", "⚠️ App was sideloaded. Risky!")
        }
        result.isAccessibilityServiceEnabled -> {
            Log.w("Security", "⚠️ Accessibility Service is active. Verify source!")
        }
        else -> {
            Log.d("Security", "✅ Safe and installed via Play Store or trusted source.")
        }
    }
    // You can now use result.installSource, result.hasRisk, etc.
}
```

---
## 💡 Detection Result 

The library returns a structured DetectionResult object for clarity and ease of integration:
```groovy
data class DetectionResult(
    val isSideloadingDetected: Boolean = false,
    val isAccessibilityServiceEnabled: Boolean = false,
    val installSource: String? = null, // e.g., "PLAY_STORE", "OPPO_STORE", "EASY_MOVER"
    val message: String = "",
    val hasRisk: Boolean = false,
    val confidence: Double = 0.95 // High confidence
)
```
>You can easily chain this with your own security logic, UI warnings, or logging.
---


## ⚙️ Customizing Trusted Installers (Allowlist)

`andro-sidetect` includes a configurable allowlist of trusted installer packages. You can customize it at runtime:
```groovy
val allowlist = mutableSetOf(
    "com.android.vending",                    // Google Play Store
    "com.oppo.store",                         // Oppo
    "com.vivo.appstore",                      // Vivo
    "com.samsung.android.apps.securefolder", // Samsung
    "com.xiaomi.market",                      // Xiaomi
    "com.huawei.appmarket",                   // Huawei
    "com.realme.appstore",                    // Realme
    "com.lenovo.store",                       // Lenovo
    "com.android.easy_mover"                  // Android Easy Mover
)

detector.setAllowlist(allowlist)
```
>✅ Add or remove any installer package by modifying the allowlist.
---


## 🔐 Android 11+ Compatibility (API 30+)
⚠️ Critical Note: Starting with Android 11 (API 30), getInstallerPackageName() may return null for many apps unless:

You declare queries in AndroidManifest.xml, OR
You have the QUERY_ALL_PACKAGES permission (which requires user approval and is restricted)
✅ Solution: Use <queries> in AndroidManifest.xml
```groovy
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="your.app.package">

    <queries>
        <package android:name="com.android.vending" />
        <package android:name="com.oppo.store" />
        <package android:name="com.vivo.appstore" />
        <package android:name="com.samsung.android.apps.securefolder" />
        <package android:name="com.xiaomi.market" />
        <package android:name="com.huawei.appmarket" />
        <package android:name="com.realme.appstore" />
        <package android:name="com.lenovo.store" />
        <package android:name="com.bbk.appstore" />
        <package android:name="com.ksmobile.appstore" />
        <package android:name="com.coolpad.appstore" />
        <package android:name="com.android.packageinstaller" />
        <package android:name="com.android.easy_mover" />
    </queries>

    ...
</manifest>
```
>✅ This ensures getInstallerPackageName() returns accurate values on Android 11+.
>💡 Tip: You can add more from PackageInstaller or OEM-specific installers
---

## 🛡️ Security & Privacy
- No data is collected or sent over the network.
- All detection is done locally on the device.
- No access to sensitive data beyond package metadata.

---
## 🤝 Contributing
We welcome contributions! Please open issues or PRs to:
- Add support for new OEM installers
- Improve detection logic
- Add tests or documentation
---
## 📜 License

MIT License  
© 2025 [adeftriangga](https://github.com/adeftriangga)
