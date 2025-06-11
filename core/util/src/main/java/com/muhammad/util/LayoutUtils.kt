package com.muhammad.util

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.window.WindowSdkExtensions
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.computeWindowSizeClass
import androidx.window.layout.FoldingFeature
import androidx.window.layout.SupportedPosture
import androidx.window.layout.WindowInfoTracker
import androidx.window.layout.WindowMetricsCalculator
import androidx.window.layout.adapter.computeWindowSizeClass
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.math.abs

@Composable
fun calculateWindowSizeClass() : WindowSizeClass{
    val currentWindowMetrics = WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(LocalContext.current)
    return WindowSizeClass.BREAKPOINTS_V1.computeWindowSizeClass(currentWindowMetrics)
}

@Composable
fun isAtLeastMedium() : Boolean{
    val sizeClass = calculateWindowSizeClass()
    return sizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)
}

@Composable
fun allowFullContent() : Boolean{
    val context = LocalContext.current
    val windowMetrics = WindowMetricsCalculator.getOrCreate().computeCurrentWindowMetrics(context)
    val isSmallHeight = windowMetrics.heightDp < WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND
    return !isSmallHeight
}

@OptIn(ExperimentalContracts::class)
fun isTabletTopPosture(foldFeature: FoldingFeature?): Boolean {
    contract { returns(true) implies (foldFeature != null) }
    return foldFeature?.state == FoldingFeature.State.HALF_OPENED && foldFeature.orientation == FoldingFeature.Orientation.HORIZONTAL
}

@SuppressLint("RequiresWindowSdk")
@Composable
fun supportTabletTop(): Boolean {
    return if (WindowSdkExtensions.getInstance().extensionVersion >= 6) {
        val postures = WindowInfoTracker.getOrCreate(LocalContext.current).supportedPostures
        postures.contains(SupportedPosture.TABLETOP)
    } else false
}

fun shouldShowTabletopLayout(supportsTabletop: Boolean, isTabletop: Boolean) =
    supportsTabletop || isTabletop

fun calculateCorrectAspectRatio(height: Int, width: Int, defaultAspectRatio: Float): Float {
    val newAspectRatio = width.toFloat() / height.toFloat()
    val tolerance = 0.13f
    val distance = abs(newAspectRatio - defaultAspectRatio)
    return if (distance < tolerance) {
        defaultAspectRatio
    } else {
        newAspectRatio
    }
}

class FoldablePreviewParametersProvider : PreviewParameterProvider<FoldablePreviewParameters> {
    override val values: Sequence<FoldablePreviewParameters> = sequenceOf(
        FoldablePreviewParameters(supportsTabletop = true, isTabletop = true),
        FoldablePreviewParameters(supportsTabletop = true, isTabletop = false),
        FoldablePreviewParameters(supportsTabletop = false, isTabletop = false)
    )
}

data class FoldablePreviewParameters(
    val supportsTabletop: Boolean, val isTabletop: Boolean,
)