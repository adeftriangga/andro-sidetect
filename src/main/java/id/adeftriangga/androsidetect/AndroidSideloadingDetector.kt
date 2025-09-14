package id.adeftriangga.androsidetect

import android.content.Context
import android.content.pm.PackageManager
import android.provider.Settings

data class AppDetectionResult(
    val packageName: String,
    val installer: String?,
    val isSideloaded: Boolean,
    val isInstallerUnknown: Boolean,
    val isAccessibilityEnabled: Boolean
)

data class DeviceDetectionResult(
    val sideloadedApps: List<AppDetectionResult>,
    val confidence: Double,
    val message: String
)

class AndroidSideloadingDetector(private val context: Context) {

    fun detectAll(allowlist: Set<String> = defaultAllowlist()): DeviceDetectionResult {
        val pm = context.packageManager
        val installedPackages = pm.getInstalledPackages(PackageManager.GET_META_DATA)

        val enabledServices = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )?.split(":").orEmpty()

        val sideloadedApps = installedPackages.mapNotNull { pkgInfo ->
            val installer = pm.getInstallerPackageName(pkgInfo.packageName)
            val isInstallerUnknown = installer == null
            val isSideloaded = installer != null && !allowlist.contains(installer)

            if (isInstallerUnknown || isSideloaded) {
                val hasAccessibility = enabledServices.any { it.startsWith(pkgInfo.packageName) }
                AppDetectionResult(
                    packageName = pkgInfo.packageName,
                    installer = installer,
                    isSideloaded = isSideloaded,
                    isInstallerUnknown = isInstallerUnknown,
                    isAccessibilityEnabled = hasAccessibility
                )
            } else null
        }

        val confidence = if (sideloadedApps.isNotEmpty()) 0.95 else 0.7
        val message = "Detected ${sideloadedApps.size} sideloaded/unknown apps"

        return DeviceDetectionResult(
            sideloadedApps = sideloadedApps,
            confidence = confidence,
            message = message
        )
    }

    companion object {
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
            "com.bbk.appstore",
            "com.ksmobile.appstore",
            "com.coolpad.appstore",
            "com.sec.android.easyMover"
        )
    }
}
