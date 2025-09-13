# 🎯 `andro-sidetect` - Android Sideload & Accessibility Detection Library
**Detect sideloaded apps and accessibility service risks with confidence. Lightweight, reusable, and ideal for security-aware apps.**

---

![License](https://img.shields.io/badge/license-MIT-blue.svg)
[![JitPack](https://jitpack.io/v/adeftriangga/andro-sidetect.svg)](https://jitpack.io/#adeftriangga/andro-sidetect)

## ✅ Why Use `andro-sidetect`?

Malicious APKs often:
- Use **social engineering** to trick users into sideloading apps
- **Enable Accessibility Services** to take control of the device

`andro-sidetect` helps your app:
- Detect if it was **sideloaded** (not from Play Store)
- Check if **Accessibility Service is enabled**
- Report detection results via reusable callbacks
- Be **privacy-aware** and compatible with modern Android versions

---

## 🔧 Features

| Feature | Description |
|-------|-----------|
| 🔍 Sideloading Detection | Checks installer package name with fallbacks |
| 🛡️ Accessibility Service Check | Detects if accessibility service is enabled |
| 🔄 Configurable Allowlist | Easily add/remove trusted installers |
| 📱 OEM Installer Support | Pre-configured for: Samsung, Xiaomi, Oppo, Vivo, Huawei, Realme, etc. |
| 💾 Backup/Restore Detection | Detects tools like *Android Easy Mover* |
| 📁 Modular & Reusable | Returns `DetectionResult` via callback—easy to integrate |
| 🌐 Android 11+ Ready | Handles `queries` correctly |

---

## 📦 Installation

### Option 1: JitPack (Recommended)
Add JitPack repository in your `settings.gradle` or `build.gradle`:
```groovy
repositories {
    maven { url 'https://jitpack.io' }
}
dependencies {
    implementation 'com.github.adeftriangga:andro-sidetect:1.1.0'
}
```

### Option 2: Local module
Clone this repo and include it in your `settings.gradle`:
```groovy
include ':andro-sidetect'
```

>⚠️ Requires **AndroidX** and Kotlin (1.9+). Minimum SDK 21.

---

## 🛠️ Usage Example

```kotlin
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
}
```

### Example Output
```text
DetectionResult(
    isSideloadingDetected=true,
    isAccessibilityServiceEnabled=false,
    installSource=null,
    message="Sideloaded app detected (installer=null)",
    hasRisk=true,
    confidence=0.95
)
```

---

## 💡 Detection Result 
```kotlin
data class DetectionResult(
    val isSideloadingDetected: Boolean = false,
    val isAccessibilityServiceEnabled: Boolean = false,
    val installSource: String? = null,
    val message: String = "",
    val hasRisk: Boolean = false,
    val confidence: Double = 0.95
)
```

---

## ⚙️ Customizing Trusted Installers (Allowlist)
```kotlin
val allowlist = mutableSetOf(
    "com.android.vending", // Google Play Store
    "com.oppo.store",      // Oppo
    "com.vivo.appstore",   // Vivo
    "com.samsung.android.apps.securefolder",
    "com.xiaomi.market",
    "com.huawei.appmarket",
    "com.realme.appstore",
    "com.lenovo.store",
    "com.sec.android.easyMover"
)

detector.setAllowlist(allowlist)
```
✅ Add or remove any installer package by modifying the allowlist.

---

## 🔐 Android 11+ Compatibility
⚠️ On Android 11+, `getInstallerPackageName()` may return null unless you declare `<queries>`:

```xml
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
        <package android:name="com.sec.android.easyMover" />
    </queries>

</manifest>
```
✅ This ensures correct installer detection on Android 11+.

---

## 📝 Notes on Installer Detection
Since Android 11+, `getInstallerPackageName()` may return `null` for Play Store installs.
This library  reports `isSideloadingUnknown = true` when installer cannot be determined,
allowing the caller to treat it as *unknown* rather than a confirmed sideload.
This avoids false positives on modern devices.

---
## 🛡️ Security & Privacy
- No data leaves the device  
- No network calls  
- Uses only package metadata  

---

## 🤝 Contributing
- Fork the repo & create a feature branch  
- Add tests for new logic  
- Submit a PR with description  

---

## 📜 License
MIT License  
© 2025 [adeftriangga](https://github.com/adeftriangga)
