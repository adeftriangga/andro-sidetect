# 🎯 `andro-sidetect` - Android Sideload & Accessibility Detection Library
**Detect sideloaded apps on a device, and check if they have accessibility services enabled. Lightweight, reusable, and ideal for security-aware apps.**

---

## ✅ Why Use `andro-sidetect`?

Malicious APKs often:
- Use **social engineering** to trick users into sideloading apps
- **Enable Accessibility Services** to take control of the device

`andro-sidetect` helps your app:
- Detect if there are  **sideloaded apps** (not from Play Store) on a device
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
| 💾 Backup/Restore App Detection | Detects tools like *Android Easy Mover* |
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
val result = detector.detectAll()

when {
    result.sideloadedApps.any { it.isAccessibilityEnabled } -> {
        Log.w("Security", "⚠️ High Risk: ${result.sideloadedApps.size} sideloaded apps with accessibility enabled")
    }
    result.sideloadedApps.isNotEmpty() -> {
        Log.w("Security", "⚠️ Found ${result.sideloadedApps.size} sideloaded apps")
    }
    else -> {
        Log.d("Security", "✅ No sideloaded apps detected")
    }
}
```

You can also iterate through all detected apps:

```kotlin
result.sideloadedApps.forEach { app ->
    Log.i("Security", "• ${app.packageName} (installer=${app.installer ?: "unknown"})" +
            if (app.isAccessibilityEnabled) " [Accessibility Enabled]" else "")
}
```
---
## 💡 Example Output
```
DeviceDetectionResult(
    sideloadedApps = listOf(
        AppDetectionResult(packageName=com.evil.app, installer=null, isSideloaded=false, isInstallerUnknown=true, isAccessibilityEnabled=true)
    ),
    confidence = 0.95,
    message = "Detected 1 sideloaded/unknown apps"
)
```
---



## ⚙️ Customizing Trusted Installers (Allowlist)
```kotlin
fun defaultAllowlist(): Set<String> = setOf(
            "com.android.vending",
            "com.oppo.store",
            "com.vivo.appstore",
            "com.samsung.android.app.samsungapps",
            "com.samsung.android.apps.securefolder",
            "com.xiaomi.market",
            "com.huawei.appmarket",
            "com.realme.appstore",
            "com.lenovo.store",
            "com.sec.android.easyMover"
        )
```
✅ Add or remove any installer package by modifying the allowlist.

---

## 🔐 Android 11+ Compatibility
⚠️ On Android 11+, `getInstallerPackageName()` may return null unless you declare `<queries>`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.example.myapp">

    <!-- Needed for Android 11+ to query installed apps -->
    <uses-permission android:name="android.permission.QUERY_ALL_PACKAGES" />

    <application
        android:allowBackup="true"
        android:label="@string/app_name"
        android:supportsRtl="true"
        android:theme="@style/Theme.MyApp">
        
        <!-- Your activities/services/etc go here -->

    </application>

    <!-- Allow querying visibility of all installed apps -->
    <queries>
        <!-- Wildcard: see all installed apps -->
        <package android:name="*" />
    </queries>

</manifest>
```

---

## 📝 Notes

- Installer detection uses `PackageManager.getInstallerPackageName()`.
- Accessibility check is only performed **for sideloaded/unknown apps** for better performance.

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
