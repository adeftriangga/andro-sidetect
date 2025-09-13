package id.adeftriangga.androsidetect

/**
 * Result object returned by the detector.
 */
data class DetectionResult(
    val isSideloadingUnknown: Boolean = false,  // Flag for unknown installer
    val isAccessibilityServiceEnabled: Boolean = false,
    val installSource: String? = null, // e.g., "com.android.vending", "com.oppo.store"
    val message: String = "",
    val hasRisk: Boolean = false,
    val confidence: Double = 0.95 // heuristic confidence
)
