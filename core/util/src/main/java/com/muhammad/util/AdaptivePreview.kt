package com.muhammad.util

import androidx.compose.ui.tooling.preview.Devices.DESKTOP
import androidx.compose.ui.tooling.preview.Devices.PIXEL_3A_XL
import androidx.compose.ui.tooling.preview.Devices.PIXEL_7_PRO
import androidx.compose.ui.tooling.preview.Devices.PIXEL_FOLD
import androidx.compose.ui.tooling.preview.Devices.PIXEL_TABLET
import androidx.compose.ui.tooling.preview.Preview

@Preview(device = PIXEL_7_PRO, name = "Phone preview")
annotation class PhonePreview
@Preview(device = PIXEL_3A_XL, name = "Small Phone preview", heightDp = 300, widthDp = 500)
annotation class SmallPhonePreview
@Preview(device = PIXEL_FOLD, name = "Foldable preview")
annotation class FoldablePreview
@Preview(device = PIXEL_TABLET, name = "Tablet preview")
annotation class TabletPreview
@Preview(device = DESKTOP,name = "Desktop preview")
annotation class DesktopPreview

@FoldablePreview
@TabletPreview
@DesktopPreview
annotation class LargeScreensPreview

@PhonePreview
@LargeScreensPreview
annotation class AdaptivePreview