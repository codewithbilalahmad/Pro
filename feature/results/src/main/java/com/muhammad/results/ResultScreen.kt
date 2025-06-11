package com.muhammad.results

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.muhammad.theme.ProTheme
import com.muhammad.theme.R
import com.muhammad.theme.components.PrimaryButton
import com.muhammad.theme.components.ProTopAppBar
import com.muhammad.theme.components.ResultsBackground
import com.muhammad.util.AdaptivePreview
import com.muhammad.util.SmallPhonePreview
import com.muhammad.util.allowFullContent
import com.muhammad.util.isAtLeastMedium
import org.koin.androidx.compose.koinViewModel
import com.muhammad.results.R as ResultR

@AdaptivePreview
@SmallPhonePreview
@Preview
@Composable
private fun ResultScreenPreview() {
    ProTheme {
        val bitmap = ImageBitmap.imageResource(ResultR.drawable.placeholderbot)
        val state by remember {
            mutableStateOf(
                ResultState(
                    resultImageBitmap = bitmap.asAndroidBitmap(),
                    promptText = "wearing a hat with straw hair",
                ),
            )
        }
        ResultsScreenContents(
            contentPaddingValues = PaddingValues(0.dp),
            onDownload = {},
            onShare = {},
            state = state
        )
    }
}

@SmallPhonePreview
@Composable
private fun ResultScreenPreviewSmall() {
    ProTheme {
        val bitmap = ImageBitmap.imageResource(ResultR.drawable.placeholderbot)
        val state by remember {
            mutableStateOf(
                ResultState(
                    resultImageBitmap = bitmap.asAndroidBitmap(),
                    promptText = "wearing a hat with straw hair",
                ),
            )
        }
        ResultsScreenContents(
            contentPaddingValues = PaddingValues(0.dp),
            onDownload = {},
            onShare = {},
            state = state
        )
    }
}

@Composable
fun ResultScreen(
    modifier: Modifier = Modifier,
    resultImage: Bitmap,
    originalImageUri: Uri?,
    promptText: String,
    verboseLayout: Boolean = allowFullContent(),
    onBackPress: () -> Unit,
    onAbout: () -> Unit,
    viewModel: ResultViewModel = koinViewModel<ResultViewModel>(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(resultImage, originalImageUri, promptText) {
        viewModel.setArguments(
            resultImageUrl = resultImage,
            originalImageUrl = originalImageUri,
            promptText = promptText
        )
    }
    val context = LocalContext.current
    LaunchedEffect(state.saveUri) {
        val savesImageUri = state.saveUri
        if (savesImageUri != null) {
            shareImage(context = context, savesImageUri)
        }
    }
    val snackbarHostState by viewModel.snackbarHostState.collectAsStateWithLifecycle()
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState, snackbar = { snackbarData ->
                Snackbar(snackbarData = snackbarData, shape = SnackbarDefaults.shape)
            })
        },
        topBar = {
            ProTopAppBar(
                backEnabled = true,
                isMediumWindowSize = isAtLeastMedium(),
                onBackPressed = {
                    onBackPress()
                },
                onAboutClicked = onAbout
            )
        },
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
    ) { contentPadding ->
        ResultsScreenContents(contentPaddingValues = contentPadding, state = state, onDownload = {
            viewModel.downloadClicked()
        }, onShare = {
            viewModel.shareClick()
        }, verboseLayout = verboseLayout)
    }
}

@Composable
fun ResultsScreenContents(
    contentPaddingValues: PaddingValues,
    state: ResultState,
    verboseLayout: Boolean = allowFullContent(),
    onDownload: () -> Unit,
    onShare: () -> Unit,
    defaultSelectedResult: ResultOption = ResultOption.ResultImage,
) {
    ResultsBackground()
    val showResult = state.resultImageBitmap != null
    var selectedResultOption by remember {
        mutableStateOf(defaultSelectedResult)
    }
    val wasPromptUsed = state.originalImageUrl == null
    val promptToolBar = @Composable { modifier: Modifier ->
        ResultToolbarOption(
            modifier = modifier,
            selectedOption = selectedResultOption,
            wasPromptUsed = wasPromptUsed,
            onResultOptionSelected = { option ->
                selectedResultOption = option
            })
    }
    val botResultCard = @Composable { modifier: Modifier ->
        AnimatedVisibility(
            showResult,
            enter = fadeIn(tween(300, delayMillis = 1000)) + slideInVertically(
                tween(1000, easing = EaseOutBack, delayMillis = 1000),
                initialOffsetY = { fullHeight -> fullHeight }
            )) {
            Box(modifier = Modifier.fillMaxSize()) {
                BotResultCard(
                    resultImage = state.resultImageBitmap!!,
                    originalImageUrl = state.originalImageUrl,
                    prompt = state.promptText,
                    modifier = Modifier.align(
                        Alignment.Center
                    ),
                    flippableState = selectedResultOption.toFlippableState(),
                    onFlipStateChange = { flipOption ->
                        selectedResultOption = when (flipOption) {
                            FlippableState.Front -> ResultOption.ResultImage
                            FlippableState.Back -> ResultOption.OriginalInput
                        }
                    }
                )
            }
        }
    }
    val buttonRow = @Composable { modifier: Modifier ->
        BotActionSection(
            onShare = onShare,
            onDownload = onDownload,
            modifier = modifier,
            verboseLayout = verboseLayout
        )
    }
    val backgroundQuotes = @Composable { modifier: Modifier ->
        AnimatedVisibility(
            showResult,
            enter = slideInHorizontally(animationSpec = tween(1000)) { fullWidth -> fullWidth },
            modifier = Modifier.fillMaxSize()
        ) {
            BackgroundRandomQuotes(verboseLayout)
        }
    }
    if (verboseLayout) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(contentPaddingValues)
        ) {
            promptToolBar(Modifier.align(Alignment.CenterHorizontally))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
            ) {
                backgroundQuotes(Modifier)
                botResultCard(Modifier)
            }
            buttonRow(
                Modifier
                    .padding(vertical = 16.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
    } else {
        Box {
            backgroundQuotes(Modifier.fillMaxSize())
            botResultCard(Modifier)
            promptToolBar(
                Modifier
                    .align(Alignment.BottomStart)
                    .padding(bottom = 16.dp)
            )
            buttonRow(
                Modifier
                    .padding(end = 16.dp, bottom = 16.dp)
                    .align(Alignment.BottomEnd)
            )
        }
    }
}

@Composable
private fun BackgroundRandomQuotes(verboseLayout: Boolean = true) {
    val localInspectionMode = LocalInspectionMode.current
    Box(modifier = Modifier.fillMaxSize()) {
        val listResultCompliments = stringArrayResource(ResultR.array.list_compliments)
        val randomQuote = remember {
            if (localInspectionMode) {
                listResultCompliments.first()
            } else {
                listResultCompliments.random()
            }
        }
        val iterations = if (localInspectionMode) 0 else 100
        Text(
            text = randomQuote,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 120.sp
            ),
            modifier = Modifier
                .align(if (verboseLayout) Alignment.TopCenter else Alignment.Center)
                .basicMarquee(
                    iterations = iterations,
                    repeatDelayMillis = 0,
                    velocity = 80.dp,
                    initialDelayMillis = 500
                )
        )
        if (verboseLayout) {
            val listMinusOther = listResultCompliments.asList().minus(randomQuote)
            val randomQuote2 = remember {
                if (localInspectionMode) {
                    listMinusOther.first()
                } else {
                    listMinusOther.random()
                }
            }
            Text(
                text = randomQuote2,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 110.sp
                ),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .basicMarquee(
                        iterations = iterations,
                        repeatDelayMillis = 0,
                        velocity = 60.dp,
                        initialDelayMillis = 500
                    )
            )
        }
    }
}

@Composable
fun BotActionSection(
    modifier: Modifier = Modifier,
    onShare: () -> Unit,
    onDownload: () -> Unit,
    verboseLayout: Boolean = false,
) {
    val context = LocalContext.current
    val activity = context as ComponentActivity
    var showRationalDialog by remember { mutableStateOf(false) }
    var isWriteStoragePermissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    var shouldWriteStoragePermissionRational by remember {
        mutableStateOf(
            ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        )
    }
    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            isWriteStoragePermissionGranted = isGranted
            shouldWriteStoragePermissionRational =
                ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
        }
    Row(modifier) {
        PrimaryButton(onClick = {
            onShare()
        }, leadingIcon = {
            Row {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.sharp_share_24),
                    contentDescription = null
                )
                Spacer(Modifier.width(8.dp))
            }
        }, buttonText = if (verboseLayout) stringResource(ResultR.string.share_your_bot) else null)
        PrimaryButton(onClick = {
            if (!isWriteStoragePermissionGranted) {
                if (shouldWriteStoragePermissionRational) {
                    showRationalDialog = true
                } else {
                    permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            } else {
                onDownload()
            }
        }, leadingIcon = {
            Icon(
                imageVector = ImageVector.vectorResource(ResultR.drawable.rounded_download_24),
                contentDescription = stringResource(ResultR.string.download_bot)
            )
        })
        PermissionRationaleDialog(
            showRationaleDialog = shouldWriteStoragePermissionRational,
            onDismiss = {
                showRationalDialog = false
            },
            launchPermissionRequest = {
                permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            })
    }
}