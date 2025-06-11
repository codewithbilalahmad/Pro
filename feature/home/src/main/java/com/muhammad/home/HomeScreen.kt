@file:kotlin.OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.muhammad.home

import androidx.annotation.OptIn
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.onLayoutRectChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.compose.PlayerSurface
import androidx.media3.ui.compose.SURFACE_TYPE_TEXTURE_VIEW
import androidx.media3.ui.compose.state.rememberPlayPauseButtonState
import coil3.compose.AsyncImage
import com.muhammad.theme.Blue
import com.muhammad.theme.SharedElementContextPreview
import com.muhammad.theme.components.ProTopAppBar
import com.muhammad.theme.components.ProTranslucentTopAppBar
import com.muhammad.theme.components.SquiggleBackground
import com.muhammad.util.LargeScreensPreview
import com.muhammad.util.PhonePreview
import com.muhammad.util.isAtLeastMedium
import org.koin.androidx.compose.koinViewModel
import com.muhammad.theme.R as ThemeR

@Composable
fun HomeScreen(
    viewmodel: HomeViewModel = koinViewModel(),
    isMediumWindowSize: Boolean = isAtLeastMedium(),
    onClickLetsGo: (IntOffset) -> Unit = {},
    onAboutClick: () -> Unit = {},
) {
    val state by viewmodel.state.collectAsStateWithLifecycle()
    if (!state.isAppActive) {
        AppInActiveScreen()
    } else {
        HomeScreenContent(
            state.videoLink, state.dancingDroidLink, isMediumWindowSize, onClickLetsGo, onAboutClick
        )
    }
}

@Composable
fun HomeScreenContent(
    videoLink: String?,
    dancingBotLink: String?,
    isMediumWindowSize: Boolean,
    onClickLetsGo: (IntOffset) -> Unit,
    onAboutClick: () -> Unit,
) {
    Box {
        SquiggleBackground()
        var positionButtonClick by remember {
            mutableStateOf(IntOffset.Zero)
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            if (isMediumWindowSize) {
                ProTranslucentTopAppBar(isMediumSizeLayout = true)
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 32.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(0.8f)) {
                        VideoPlayerRotated(
                            videoLink = videoLink,
                            modifier = Modifier
                                .padding(32.dp)
                                .align(Alignment.Center)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1.2f)
                            .align(Alignment.CenterVertically)
                    ) {
                        MainHomeContent(dancingBotLink = dancingBotLink)
                        HomeButton(
                            modifier = Modifier
                                .onLayoutRectChanged {
                                    positionButtonClick = it.boundsInWindow.center
                                }
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 16.dp)
                                .height(64.dp)
                                .width(220.dp), onClick = {
                                onClickLetsGo(positionButtonClick)
                            }
                        )
                    }
                }
            } else {
                CompactPager(
                    videoLink = videoLink,
                    dancingBotLink = dancingBotLink,
                    onClick = onClickLetsGo,
                    onAboutClick = onAboutClick
                )
            }
        }
    }
}

@Composable
private fun CompactPager(
    videoLink: String?,
    dancingBotLink: String?,
    onClick: (IntOffset) -> Unit, onAboutClick: () -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        ProTopAppBar(aboutEnabled = true, onAboutClicked = onAboutClick)
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(0.8f),
            beyondViewportPageCount = 1
        ) { page ->
            when (page) {
                0 -> {
                    MainHomeContent(
                        dancingBotLink = dancingBotLink,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .align(
                                Alignment.CenterHorizontally
                            )
                    )
                }

                1 -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        VideoPlayerRotated(
                            videoLink = videoLink,
                            modifier = Modifier
                                .padding(horizontal = 32.dp)
                                .align(Alignment.Center)
                        )
                    }
                }
            }
        }
        Row(
            modifier = Modifier
                .weight(0.1f)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(pagerState.pageCount) { iteration ->
                val isCurrent by remember { derivedStateOf { pagerState.currentPage == iteration } }
                val animatedColor by animateColorAsState(
                    if (isCurrent) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onTertiary,
                    label = "animatedFirstColor"
                )
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .animateContentSize()
                        .background(
                            animatedColor,
                            RoundedCornerShape(16.dp)
                        )
                        .height(16.dp)
                        .width(if (isCurrent) 40.dp else 16.dp)
                )
            }
        }
        Spacer(Modifier.size(12.dp))
        var buttonPosition by remember {
            mutableStateOf(IntOffset.Zero)
        }
        HomeButton(
            modifier = Modifier
                .onLayoutRectChanged {
                    buttonPosition = it.boundsInWindow.center
                }
                .padding(bottom = 16.dp)
                .height(64.dp)
                .width(220.dp),
            colors = ButtonDefaults.buttonColors().copy(containerColor = Blue),
            onClick = {
                onClick(buttonPosition)
            })
    }
}

@Composable
fun VideoPlayerRotated(modifier: Modifier = Modifier, videoLink: String?) {
    val aspectRatio = 280f / 380f
    val videoInstructionText = stringResource(R.string.instruction_video_transcript)
    Box(
        modifier = modifier
            .focusable()
            .semantics {
                contentDescription = videoInstructionText
            }
            .aspectRatio(aspectRatio)
            .rotate(-3f)
            .shadow(elevation = 8.dp, shape = MaterialTheme.shapes.large)
            .background(
                color = Color.White, shape = MaterialTheme.shapes.large
            )
    ) {
        VideoPlayer(
            videoLink = videoLink,
            modifier = Modifier
                .aspectRatio(aspectRatio)
                .align(Alignment.Center)
                .clip(
                    MaterialTheme.shapes.large
                )
                .clipToBounds()
        )
    }
}

@ExperimentalMaterial3ExpressiveApi
@PhonePreview
@Composable
private fun HomeScreenPhonePreview() {
    SharedElementContextPreview {
        HomeScreenContent(
            isMediumWindowSize = false,
            onClickLetsGo = {},
            videoLink = "",
            dancingBotLink = "https://services.google.com/fh/files/misc/android_dancing.gif",
            onAboutClick = {},
        )
    }
}

@ExperimentalMaterial3ExpressiveApi
@LargeScreensPreview
@Composable
private fun HomeScreenLargeScreenPreview() {
    SharedElementContextPreview {
        HomeScreenContent(
            videoLink = "",
            isMediumWindowSize = true,
            dancingBotLink = "https://services.google.com/fh/files/misc/android_dancing.gif",
            onAboutClick = {},
            onClickLetsGo = {})
    }
}

@Composable
private fun MainHomeContent(modifier: Modifier = Modifier, dancingBotLink: String?) {
    Column(modifier = modifier) {
        DecorativeSquiggleLimeGreen()
        DancingBotHeadlineText(dancingBotLink = dancingBotLink, modifier = Modifier.weight(1f))
        DecorativeSquiggleLightGreen()
    }
}

@Composable
private fun ColumnScope.DecorativeSquiggleLimeGreen() {
    val infiniteTransition = rememberInfiniteTransition()
    val rotationAnimation by infiniteTransition.animateFloat(
        0f, -720f, animationSpec = infiniteRepeatable(
            tween(24000, easing = LinearEasing), repeatMode = RepeatMode.Reverse
        )
    )
    Image(
        painter = rememberVectorPainter(ImageVector.vectorResource(ThemeR.drawable.decorative_squiggle)),
        contentDescription = null,
        modifier = Modifier
            .padding(end = 8.dp)
            .size(60.dp)
            .align(Alignment.Start)
            .graphicsLayer {
                rotationZ = rotationAnimation
            }
    )
}

@Composable
private fun ColumnScope.DecorativeSquiggleLightGreen() {
    val infiniteTransition = rememberInfiniteTransition()
    val rotationAnimation by infiniteTransition.animateFloat(
        0f, 720f, animationSpec = infiniteRepeatable(
            tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    Image(
        painter = rememberVectorPainter(ImageVector.vectorResource(ThemeR.drawable.decorative_squiggle_2)),
        contentDescription = null,
        modifier = Modifier
            .padding(start = 60.dp)
            .size(60.dp)
            .align(Alignment.End)
            .graphicsLayer {
                rotationZ = rotationAnimation
            })
}

@Preview
@Composable
fun HomeButton(
    modifier: Modifier = Modifier,
    colors: ButtonColors = ButtonDefaults.buttonColors().copy(containerColor = Blue),
    onClick: () -> Unit = {},
) {
    val style = MaterialTheme.typography.titleLarge.copy(
        fontWeight = FontWeight(700),
        letterSpacing = 0.15.sp
    )
    Button(onClick = onClick, modifier = modifier, colors = colors) {
        Text(stringResource(R.string.home_button_label), style = style)
    }
}

@Composable
private fun DancingBot(
    dancingBotLink: String?,
    modifier: Modifier,
) {
    AsyncImage(model = dancingBotLink, modifier = modifier, contentDescription = null)
}

@Composable
fun DancingBotHeadlineText(modifier: Modifier = Modifier, dancingBotLink: String?) {
    Box(modifier = modifier) {
        val animatedBot = "animatedBot"
        val text = buildAnnotatedString {
            append(stringResource(R.string.customize_your_own))
            appendInlineContent(animatedBot)
            append(stringResource(R.string.into_an_android_bot))
        }
        var placeHolderSize by remember {
            mutableStateOf(220.sp)
        }
        val inlineContent = mapOf(
            Pair(
                animatedBot, InlineTextContent(
                    Placeholder(
                        width = placeHolderSize,
                        height = placeHolderSize,
                        placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
                    )
                ) {
                    DancingBot(
                        dancingBotLink = dancingBotLink,
                        modifier = Modifier
                            .padding(top = 32.dp)
                            .fillMaxSize()
                    )
                })
        )
        BasicText(
            text = text,
            modifier = modifier
                .align(Alignment.Center)
                .padding(bottom = 16.dp, end = 16.dp, start = 16.dp),
            style = MaterialTheme.typography.titleLarge,
            autoSize = TextAutoSize.StepBased(maxFontSize = 220.sp),
            maxLines = 5,
            onTextLayout = { result ->
                placeHolderSize = result.layoutInput.style.fontSize * 3.5f
            },
            inlineContent = inlineContent
        )
    }
}

@OptIn(UnstableApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun VideoPlayer(
    videoLink: String?,
    modifier: Modifier = Modifier,
) {
    if (LocalInspectionMode.current) return
    val context = LocalContext.current
    val view = LocalView.current
    var player by remember { mutableStateOf<Player?>(null) }
    LifecycleStartEffect(videoLink) {
        if (videoLink != null) {
            player = ExoPlayer.Builder(context).build().apply {
                setMediaItem(MediaItem.fromUri(videoLink))
                repeatMode = Player.REPEAT_MODE_ONE
                prepare()
            }
        }
        onStopOrDispose {
            player?.release()
            player = null
        }
    }
    var videoFullyOnScreen by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
            .onVisibilityChanged(
                containerWidth = view.width,
                containerHeight = view.height
            ) { fullVisible ->
                videoFullyOnScreen = fullVisible
            }
            .then(modifier)
    ) {
        player?.let { currentPlayer ->
            LaunchedEffect(videoFullyOnScreen) {
                if (videoFullyOnScreen) currentPlayer.play() else currentPlayer.pause()
            }
            PlayerSurface(player = currentPlayer, surfaceType = SURFACE_TYPE_TEXTURE_VIEW)
            val playOrPauseState = rememberPlayPauseButtonState(currentPlayer)
            OutlinedIconButton(
                onClick = playOrPauseState::onClick,
                enabled = playOrPauseState.isEnabled,
                modifier = Modifier
                    .align(
                        Alignment.BottomEnd
                    )
                    .padding(16.dp), colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest
                )
            ) {
                val icon =
                    if (playOrPauseState.showPlay) R.drawable.rounded_play_arrow_24 else R.drawable.rounded_pause_24
                val contentDescription =
                    if (playOrPauseState.showPlay) R.string.play else R.string.pause
                Icon(
                    painter = painterResource(icon),
                    contentDescription = stringResource(contentDescription)
                )
            }
        }
    }
}

fun Modifier.onVisibilityChanged(
    containerWidth: Int,
    containerHeight: Int,
    onChanged: (Boolean) -> Unit,
) = this then Modifier.onLayoutRectChanged(100, 0) { layoutBounds ->
    onChanged(
        layoutBounds.boundsInRoot.top > 0 && layoutBounds.boundsInRoot.left > 0 &&
                layoutBounds.boundsInRoot.bottom < containerHeight &&
                layoutBounds.boundsInRoot.right < containerWidth
    )
}