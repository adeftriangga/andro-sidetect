package id.adeftriangga.androsidetect

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings

/**
 * Lightweight detector for sideload installations and accessibility service status.
 * Usage: val detector = AndroidSideloadingDetector(context); detector.detect { result -> ... }
 */
class AndroidSideloadingDetector(
    private val context: Context,
    private var allowlist: MutableSet<String> = defaultAllowlist().toMutableSet()
) {

    fun setAllowlist(set: MutableSet<String>) {
        allowlist = set
    }

    fun getAllowlist(): Set<String> = allowlist

/**
* Asynchronously detect and give a DetectionResult via callback.
* This method performs light work; if you expect to call it very frequently, call it from a worker thread.
*/
    fun detect(callback: (DetectionResult) -> Unit) {
	// Fire on calling thread — detection is quick but if you prefer use a background thread
        try {
            val pm = context.packageManager
            val pkg = context.packageName

            val installer = resolveInstallerPackageName(pm, pkg)
            val isInstallerUnknown = installer == null
			val isSideload = installer != null && !allowlist.contains(installer)
            val isAccessibilityOn = checkAccessibilityEnabled(context)

            val message = buildMessage(isSideload, isAccessibilityOn, installer)
            val hasRisk = isSideload || isAccessibilityOn
            val confidence = estimateConfidence(isSideload, isAccessibilityOn)

            val result = DetectionResult(
                isSideloadingUnknown = isInstallerUnknown,
				isSideloadingDetected = isSideload,
                isAccessibilityServiceEnabled = isAccessibilityOn,
                installSource = installer,
                message = message,
                hasRisk = hasRisk,
                confidence = confidence
            )

            callback(result)
        } catch (t: Throwable) {
            val fallback = DetectionResult(
                isSideloadingDetected = false,
                isAccessibilityServiceEnabled = false,
                installSource = null,
                message = "Error detecting install source: ${t.localizedMessage}",
                hasRisk = false,
                confidence = 0.5
            )
            callback(fallback)
        }
    }

/**
* Resolve installer package name with Android 11+ support.
*/
    private fun resolveInstallerPackageName(pm: PackageManager, packageName: String): String? {
	// API 30+ provides InstallSourceInfo    
    return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                try {
                    val isi = pm.getInstallSourceInfo(packageName)
					// installingPackageName can be null if installer hidden or restricted
                    val installing = isi.installingPackageName
                    if (!installing.isNullOrBlank()) return installing
                } catch (_: Throwable) {
				// fall through to older API
				}
            }
			
			// Older API or fallback
            pm.getInstallerPackageName(packageName)
        } catch (e: Exception) {
            null
        }
    }

/**
* Check whether any accessibility services (other than the current app) are enabled.
* Returns true if accessibility is enabled and at least one enabled service belongs to another package.
*/
    private fun checkAccessibilityEnabled(context: Context): Boolean {
        return try {
            val enabled = Settings.Secure.getInt(
                context.contentResolver,
                Settings.Secure.ACCESSIBILITY_ENABLED, 0
            )
            if (enabled == 0) return false

            val services = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            ) ?: return true // enabled but not parsable -> be conservative

			// Format is colon-separated list like "com.example/.MyService:com.other/.Service"
            val entries = services.split(":")
            entries.any { entry ->
                val pkg = entry.substringBefore('/')
				// If any enabled accessibility service belongs to a package other than ours, flag it
                !pkg.isNullOrBlank() && pkg != context.packageName
            }
        } catch (_: Throwable) {
			// If we can't read settings, assume safe default (no accessibility enabled) to avoid false positives
            false
        }
    }

    private fun buildMessage(isSideload: Boolean, isAccessibility: Boolean, installer: String?): String {
        return when {
            isSideload && isAccessibility -> "Sideloaded & Accessibility service enabled (installer=$installer)"
            isSideload -> "Sideloaded app detected (installer=$installer)"
            isAccessibility -> "Accessibility service enabled on device"
            installer != null -> "Installed via $installer"
            else -> "Installer unknown"
        }
    }

    private fun estimateConfidence(isSideload: Boolean, isAccessibility: Boolean): Double {
        return when {
            isSideload && isAccessibility -> 0.98
            isSideload -> 0.95
            isAccessibility -> 0.85
            else -> 0.7
        }
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
            "com.sec.android.easyMover"
        )
    }
}
