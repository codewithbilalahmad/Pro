package com.muhammad.home

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.muhammad.theme.LocalSharedTransitionScope
import com.muhammad.theme.SharedElementContextPreview
import com.muhammad.theme.SharedElementKey
import com.muhammad.theme.components.SecondaryOutlinedButton
import com.muhammad.theme.sharedBoundsReveal
import com.muhammad.util.LargeScreensPreview
import com.muhammad.util.PhonePreview
import com.muhammad.util.isAtLeastMedium

@PhonePreview
@Composable
private fun AboutPreviewCompact() {
    SharedElementContextPreview {
        AboutScreen(isMediumWindowSize = false) {}
    }
}

@LargeScreensPreview
@Composable
private fun AboutPreviewLargeScreens() {
    SharedElementContextPreview {
        AboutScreen(isMediumWindowSize = true) {}
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AboutScreen(isMediumWindowSize: Boolean = isAtLeastMedium(), onBackPressed: () -> Unit) {
    val sharedElementScope = LocalSharedTransitionScope.current
    val navScope = LocalNavAnimatedContentScope.current
    with(sharedElementScope) {
        Scaffold(
            topBar = {
                IconButton(
                    onClick = {
                        onBackPressed()
                    },
                    modifier = Modifier
                        .safeDrawingPadding()
                        .padding(16.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    shape = CircleShape
                ) {
                    Icon(
                        painter = rememberVectorPainter(ImageVector.vectorResource(com.muhammad.theme.R.drawable.rounded_close_24)),
                        contentDescription = stringResource(R.string.about_back_content_description)
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            modifier = Modifier.sharedBoundsReveal(
                rememberSharedContentState(SharedElementKey.AboutKey),
                animatedVisibilityScope = navScope
            )
        ) { contentPadding ->
            if (isMediumWindowSize) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .safeDrawingPadding()
                        .padding(contentPadding)
                        .verticalScroll(
                            rememberScrollState()
                        )
                        .padding(start = 24.dp, end = 24.dp, top = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .wrapContentHeight()
                            .width(700.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.how_it_works),
                            style = MaterialTheme.typography.displayLarge
                        )
                        Spacer(Modifier.size(48.dp))
                        AboutMessage(textStyle = MaterialTheme.typography.headlineMedium)
                        Spacer(Modifier.size(48.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            BulletPoint(
                                bulletText = "1",
                                title = stringResource(R.string.about_step1_title),
                                text = stringResource(R.string.about_step1_label),
                                modifier = Modifier.weight(1 / 3f),
                                textStyle = MaterialTheme.typography.bodySmall
                            )
                            BulletPoint(
                                bulletText = "2",
                                title = stringResource(R.string.about_step2_title),
                                text = stringResource(R.string.about_step2_label),
                                modifier = Modifier.weight(1 / 3f),
                                textStyle = MaterialTheme.typography.bodySmall
                            )
                            BulletPoint(
                                bulletText = "3",
                                title = stringResource(R.string.about_step3_title),
                                text = stringResource(R.string.about_step3_label),
                                modifier = Modifier.weight(1 / 3f),
                                textStyle = MaterialTheme.typography.bodySmall
                            )
                        }
                        Spacer(Modifier.size(48.dp))
                        FooterButtons()
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(contentPadding)
                        .padding(start = 24.dp, end = 24.dp, top = 24.dp)
                ) {
                    Text(
                        text = stringResource(R.string.how_it_works),
                        style = MaterialTheme.typography.displayLarge
                    )
                    AboutMessage()
                    Spacer(Modifier.size(36.dp))
                    BulletPoint(
                        bulletText = "1",
                        title = stringResource(R.string.about_step1_title),
                        text = stringResource(R.string.about_step1_label),
                    )
                    Spacer(Modifier.size(24.dp))
                    BulletPoint(
                        bulletText = "2",
                        title = stringResource(R.string.about_step2_title),
                        text = stringResource(R.string.about_step2_label),
                    )
                    Spacer(Modifier.size(24.dp))
                    BulletPoint(
                        bulletText = "3",
                        title = stringResource(R.string.about_step3_title),
                        text = stringResource(R.string.about_step3_label),
                    )
                    Spacer(Modifier.size(24.dp))
                    FooterButtons()
                }
            }
        }
    }
}

@Composable
private fun FooterButtons() {
    val uriHandler = LocalUriHandler.current
    Row {
        SecondaryOutlinedButton(onClick = {
            uriHandler.openUri("https://policies.google.com/terms")
        }, buttonText = stringResource(R.string.terms))
        Spacer(Modifier.size(16.dp))
        SecondaryOutlinedButton(onClick = {
            uriHandler.openUri("https://policies.google.com/privacy")
        }, buttonText = stringResource(R.string.privacy))
    }
}

@Composable
private fun AboutMessage(textStyle: TextStyle = MaterialTheme.typography.bodyLarge) {
    Text(
        stringResource(R.string.about_message),
        style = textStyle.copy(fontWeight = FontWeight.Bold)
    )
}

@Composable
fun BulletPoint(
    modifier: Modifier = Modifier,
    bulletText: String,
    title: String,
    text: String,
    textStyle: TextStyle = MaterialTheme.typography.bodyLarge,
) {
    Column(modifier.height(IntrinsicSize.Min)) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(MaterialTheme.colorScheme.primary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(bulletText, style = textStyle, color = MaterialTheme.colorScheme.onPrimary)
        }
        Spacer(Modifier.size(8.dp))
        Column(modifier = Modifier) {
            Text(
                text = title,
                style = textStyle.copy(fontWeight = FontWeight.Bold),
                modifier = modifier
            )
            Spacer(Modifier.size(12.dp))
            Text(text = text, style = textStyle, modifier = Modifier)
        }
    }
}