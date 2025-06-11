@file:OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalSharedTransitionApi::class)

package com.muhammad.creation

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarColors
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.ripple
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.rectangle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.muhammad.results.ResultScreen
import com.muhammad.theme.LimeGreen
import com.muhammad.theme.LocalSharedTransitionScope
import com.muhammad.theme.Primary90
import com.muhammad.theme.ProTheme
import com.muhammad.theme.Secondary
import com.muhammad.theme.SharedElementKey
import com.muhammad.theme.components.GradientAssistElevatedChip
import com.muhammad.theme.components.PrimaryButton
import com.muhammad.theme.components.ProTopAppBar
import com.muhammad.theme.components.ScaleIndicationNodeFactory
import com.muhammad.theme.components.SecondaryOutlinedButton
import com.muhammad.theme.components.SquiggleBackground
import com.muhammad.theme.components.gradientChipColorDefaults
import com.muhammad.theme.components.infinitelyAnimatingLinearGradient
import com.muhammad.theme.sharedBoundsRevealWithShapeMorph
import com.muhammad.theme.sharedBoundsWithDefaults
import com.muhammad.util.AnimatedTextField
import com.muhammad.util.LargeScreensPreview
import com.muhammad.util.dashedRoundedRectBorder
import com.muhammad.util.isAtLeastMedium
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import com.muhammad.creation.R as CreationR
import com.muhammad.theme.R as ThemeR
import com.muhammad.results.R as ResultR

@Composable
fun CreationScreen(
    fileName: String? = null,
    viewModel: CreationViewModel = koinViewModel(),
    isMedium: Boolean = isAtLeastMedium(),
    onCameraPressed: () -> Unit,
    onBackPressed: () -> Unit,
    onAboutClick: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    BackHandler(enabled = state.screenState != ScreenState.EDIT) {
        viewModel.onBackPressed()
    }
    LaunchedEffect(Unit) {
        if (fileName != null) {
            viewModel.onImageSelected(fileName.toUri())
        } else {
            viewModel.onImageSelected(null)
        }
    }
    val pickMedia = rememberLauncherForActivityResult(PickVisualMedia()) { uri ->
        if (uri != null) {
            viewModel.onImageSelected(uri)
        }
    }
    val snackbarHostState by viewModel.snackbarHostState.collectAsStateWithLifecycle()
    when (state.screenState) {
        ScreenState.EDIT -> {
            EditScreen(
                snackbarHostState = snackbarHostState,
                onStartClicked = viewModel::startClicked,
                onCameraPressed = onCameraPressed,
                onBackPressed = onBackPressed,
                isExpanded = isMedium,
                onUndoPressed = viewModel::onUndoPressed,
                onOptionSelected = viewModel::onSelectedPromptOptionChange,
                onBotColorSelected = viewModel::onBotColorChange,
                onAboutClick = onAboutClick,
                onPromptGenerationPressed = viewModel::onPromptGenerationClick,
                onChooseImageClick = {
                    pickMedia.launch(PickVisualMediaRequest(it))
                },
                state = state
            )
        }

        ScreenState.LOADING -> {
            LoadingScreen(onPressCancel = viewModel::onUndoPressed)
        }

        ScreenState.RESULT -> {
            val prompt = state.descriptionText.text.toString()
            val key = if (state.descriptionText.text.isBlank()) {
                state.imageUri.toString()
            } else prompt
            ResultScreen(
                resultImage = state.resultBitmap!!,
                promptText = prompt,
                onAbout = onAboutClick,
                onBackPress = onBackPressed,
                originalImageUri = if (state.selectedPromptType == PromptType.PHOTO) state.imageUri else null
            )
        }
    }
}

@Composable
fun EditScreen(
    snackbarHostState: SnackbarHostState,
    isExpanded: Boolean,
    onCameraPressed: () -> Unit,
    onBackPressed: () -> Unit,
    state: CreationState,
    onChooseImageClick: (ActivityResultContracts.PickVisualMedia.VisualMediaType) -> Unit,
    onPromptGenerationPressed: () -> Unit,
    onOptionSelected: (PromptType) -> Unit,
    onUndoPressed: () -> Unit,
    onAboutClick: () -> Unit,
    onBotColorSelected: (BotColor) -> Unit,
    onStartClicked: () -> Unit,
) {
    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState, snackbar = { snackbarData ->
                    Snackbar(
                        snackbarData,
                        shape = SnackbarDefaults.shape,
                        modifier = Modifier.padding(4.dp)
                    )
                }
            )
        }, modifier = Modifier.safeContentPadding(), topBar = {
            ProTopAppBar(
                backEnabled = true,
                isMediumWindowSize = isExpanded,
                aboutEnabled = true,
                onBackPressed = onBackPressed,
                onAboutClicked = onAboutClick, expandedCenterButtons = {
                    PromptTypeToolbar(
                        selectedOption = state.selectedPromptType,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        onOptionSelected = onOptionSelected
                    )
                }
            )
        }, containerColor = MaterialTheme.colorScheme.surface
    ) { contentPadding ->
        SquiggleBackground()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .imePadding()
        ) {
            var showColorPickerBottomSheet by remember { mutableStateOf(false) }
            Column(modifier = Modifier.fillMaxSize()) {
                Spacer(Modifier.height(8.dp))
                if (!isExpanded) {
                    PromptTypeToolbar(
                        selectedOption = state.selectedPromptType,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .align(Alignment.CenterHorizontally),
                        onOptionSelected = onOptionSelected
                    )
                }
                if (isExpanded) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 16.dp)
                    ) {
                        MainCreationPane(
                            state = state,
                            onCameraPressed = onCameraPressed,
                            onUndoPressed = onUndoPressed,
                            onChooseImagePress = {
                                onChooseImageClick(PickVisualMedia.ImageOnly)
                            },
                            onPromptGenerationPressed = onPromptGenerationPressed,
                            onOptionSelected = onOptionSelected,
                            modifier = Modifier.weight(0.6f)
                        )
                        Box(
                            modifier = Modifier
                                .weight(0.4f)
                                .padding(vertical = 16.dp)
                                .fillMaxSize()
                                .background(
                                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                                    shape = MaterialTheme.shapes.large
                                )
                                .border(
                                    width = 2.dp,
                                    shape = MaterialTheme.shapes.large,
                                    color = MaterialTheme.colorScheme.outline
                                )
                        ) {
                            ProBotColorPicker(
                                selectedBotColor = state.botColor,
                                modifier = Modifier.padding(16.dp),
                                onBotColorSelected = onBotColorSelected,
                                listBotColors = state.listBotColors
                            )
                        }
                    }
                } else {
                    MainCreationPane(
                        state = state,
                        onCameraPressed = onCameraPressed,
                        onUndoPressed = onUndoPressed,
                        onChooseImagePress = {
                            onChooseImageClick(PickVisualMedia.ImageOnly)
                        },
                        onPromptGenerationPressed = onPromptGenerationPressed,
                        onOptionSelected = onOptionSelected,
                        modifier = Modifier.weight(0.6f)
                    )
                }
                if (isExpanded) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        TransformButton(
                            modifier = Modifier.padding(bottom = 8.dp),
                            text = stringResource(CreationR.string.start_transformation_button),
                            onClicked = onStartClicked
                        )
                    }
                } else {
                    BottomButtons(
                        onButtonColorClicked = {
                            showColorPickerBottomSheet = !showColorPickerBottomSheet
                        },
                        uiState = state,
                        onStartClicked = onStartClicked,
                        modifier = Modifier.align(
                            Alignment.CenterHorizontally
                        )
                    )
                }
            }
            BottomColorPickerBottomSheet(
                showColorPickerBottomSheet = showColorPickerBottomSheet,
                onDismiss = {
                    showColorPickerBottomSheet = false
                },
                onColorChange = onBotColorSelected,
                listBotColors = state.listBotColors,
                selectedBotColor = state.botColor
            )
        }
    }
}

@Composable
private fun MainCreationPane(
    state: CreationState,
    modifier: Modifier = Modifier,
    onCameraPressed: () -> Unit,
    onChooseImagePress: () -> Unit,
    onUndoPressed: () -> Unit,
    onPromptGenerationPressed: () -> Unit,
    onOptionSelected: (PromptType) -> Unit,
) {
    Box(modifier = modifier) {
        val spatialSpec = MaterialTheme.motionScheme.slowSpatialSpec<Float>()
        val pagerState =
            rememberPagerState(state.selectedPromptType.ordinal) { PromptType.entries.size }
        val focusManager = LocalFocusManager.current
        LaunchedEffect(state.selectedPromptType) {
            launch {
                pagerState.animateScrollToPage(
                    state.selectedPromptType.ordinal,
                    animationSpec = spatialSpec
                )
            }.invokeOnCompletion {
                if (state.selectedPromptType != PromptType.entries[pagerState.currentPage]) {
                    onOptionSelected(PromptType.entries[pagerState.currentPage])
                }
            }
        }
        LaunchedEffect(pagerState) {
            snapshotFlow { pagerState.currentPage }.collect { page ->
                onOptionSelected(PromptType.entries[page])
            }
        }
        LaunchedEffect(pagerState) {
            snapshotFlow { pagerState.targetPage }.collect {
                if (pagerState.targetPage != PromptType.TEXT.ordinal) {
                    focusManager.clearFocus()
                }
            }
        }
        HorizontalPager(
            state = pagerState,
            modifier = modifier.fillMaxSize(),
            pageSpacing = 16.dp,
            contentPadding = PaddingValues(16.dp)
        ) { index ->
            when (index) {
                PromptType.PHOTO.ordinal -> {
                    val imageUri = state.imageUri
                    if (imageUri == null) {
                        UploadEmptyState(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(2.dp),
                            onCameraPressed = onCameraPressed,
                            onChooseImagePress = onChooseImagePress
                        )
                    } else {
                        ImagePreview(
                            uri = imageUri,
                            onUndoPressed = onUndoPressed,
                            onChooseImagePress = onChooseImagePress,
                            modifier = Modifier
                                .fillMaxSize()
                                .heightIn(min = 200.dp)
                        )
                    }
                }

                PromptType.TEXT.ordinal -> {
                    TextPrompt(
                        textFieldState = state.descriptionText,
                        promptGenerationInProgress = state.promptGenerationInProgress,
                        generatedPrompt = state.generatedPrompt,
                        onPromptGenerationPressed = onPromptGenerationPressed,
                        modifier = Modifier
                            .fillMaxSize()
                            .heightIn(min = 200.dp)
                            .padding(2.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ColumnScope.BottomButtons(
    onButtonColorClicked: () -> Unit,
    uiState: CreationState,
    onStartClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        maxItemsInEachRow = 2,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .wrapContentSize()
            .padding(8.dp)
            .align(Alignment.CenterHorizontally)
    ) {
        SecondaryOutlinedButton(
            onClick = {
                onButtonColorClicked()
            },
            buttonText = stringResource(CreationR.string.bot_color_button),
            modifier = Modifier.fillMaxRowHeight(),
            leadingIcon = {
                Row {
                    DisplayBotColor(
                        botColor = uiState.botColor,
                        modifier = modifier
                            .clip(CircleShape)
                            .border(2.dp, color = MaterialTheme.colorScheme.outline, CircleShape)
                            .size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
            })
        TransformButton(modifier = Modifier.fillMaxRowHeight(), onClicked = onStartClicked)
    }
}

@Composable
fun TransformButton(
    modifier: Modifier = Modifier, onClicked: () -> Unit = {},
    text: String = stringResource(CreationR.string.transform_button),
) {
    PrimaryButton(modifier = modifier, onClick = onClicked, buttonText = text, trailingIcon = {
        Row {
            Spacer(Modifier.width(8.dp))
            Icon(
                imageVector = ImageVector.vectorResource(ThemeR.drawable.rounded_arrow_forward_24),
                contentDescription = null
            )
        }
    })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BottomColorPickerBottomSheet(
    showColorPickerBottomSheet: Boolean,
    onDismiss: () -> Unit,
    onColorChange: (BotColor) -> Unit,
    listBotColors: List<BotColor>, selectedBotColor: BotColor,
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    if (showColorPickerBottomSheet) {
        ModalBottomSheet(modifier = Modifier, sheetState = sheetState, onDismissRequest = {
            onDismiss()
        }) {
            Column(
                modifier = Modifier.padding(
                    start = 36.dp,
                    end = 36.dp,
                    top = 16.dp,
                    bottom = 8.dp
                )
            ) {
                ProBotColorPicker(
                    selectedBotColor = selectedBotColor,
                    onBotColorSelected = { color ->
                        onColorChange(color)
                        scope.launch {
                            delay(400)
                            sheetState.hide()
                        }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                onDismiss()
                            }
                        }
                    },
                    listBotColors = listBotColors
                )
            }
        }
    }
}

@Composable
fun ImagePreview(
    modifier: Modifier = Modifier,
    uri: Uri,
    onUndoPressed: () -> Unit,
    onChooseImagePress: () -> Unit,
) {
    val context = LocalContext.current
    val sharedElementScope = LocalSharedTransitionScope.current
    with(sharedElementScope) {
        Box(modifier) {
            AsyncImage(
                ImageRequest.Builder(context).data(uri).crossfade(true).build(),
                placeholder = null,
                contentDescription = stringResource(CreationR.string.cd_selected_image),
                modifier = Modifier
                    .align(
                        Alignment.Center
                    )
                    .sharedBoundsWithDefaults(
                        rememberSharedContentState(
                            SharedElementKey.CaptureImageToDetails
                        )
                    )
                    .clip(MaterialTheme.shapes.large)
                    .fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
            ) {
                SecondaryOutlinedButton(onClick = {
                    onUndoPressed()
                }, leadingIcon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(CreationR.drawable.rounded_redo_24),
                        contentDescription = stringResource(CreationR.string.cd_retake_photo)
                    )
                })
                Spacer(modifier = Modifier.width(8.dp))
                SecondaryOutlinedButton(
                    onClick = {
                        onChooseImagePress()
                    },
                    buttonText = stringResource(CreationR.string.photo_picker_choose_photo_label),
                    leadingIcon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(CreationR.drawable.choose_picture_image),
                            contentDescription = stringResource(CreationR.string.cd_choose_photo)
                        )
                    })
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TextPromptGenerationPreview() {
    ProTheme {
        TextPrompt(
            textFieldState = TextFieldState(),
            promptGenerationInProgress = false,
            generatedPrompt = "Wearing a red sweater", onPromptGenerationPressed = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun TextPromptGenerationInProgressPreview() {
    ProTheme {
        TextPrompt(
            textFieldState = TextFieldState(),
            promptGenerationInProgress = true,
            generatedPrompt = "Wearing a red sweater", onPromptGenerationPressed = {})
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TextPrompt(
    modifier: Modifier = Modifier,
    textFieldState: TextFieldState,
    promptGenerationInProgress: Boolean,
    generatedPrompt: String? = null,
    onPromptGenerationPressed: () -> Unit,
) {
    Column(modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = rememberVectorPainter(ImageVector.vectorResource(CreationR.drawable.rounded_draw_24)),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.size(8.dp))
            Text(
                stringResource(CreationR.string.headline_my_bot_is),
                style = MaterialTheme.typography.headlineLarge.copy(fontSize = 24.sp)
            )
        }
        Spacer(Modifier.size(8.dp))
        Column(
            modifier = Modifier
                .dashedRoundedRectBorder(
                    2.dp, MaterialTheme.colorScheme.outline, cornerRadius = 28.dp
                )
                .padding(horizontal = 16.dp)
                .padding(vertical = 16.dp)
                .fillMaxSize()
        ) {
            AnimatedTextField(
                textFieldState = textFieldState,
                targetEndState = generatedPrompt,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                style = TextStyle(fontSize = 24.sp),
                decorator = { innerTextField ->
                    if (textFieldState.text.isEmpty()) {
                        Text(
                            stringResource(CreationR.string.prompt_text_hint),
                            color = Color.Black,
                            fontSize = 20.sp
                        )
                    }
                    innerTextField()
                })
            AnimatedVisibility(
                !WindowInsets.isImeVisible,
                enter = fadeIn(MaterialTheme.motionScheme.defaultEffectsSpec()),
                exit = fadeOut(MaterialTheme.motionScheme.defaultEffectsSpec())
            ) {
                HelpWriteButton(
                    promptGenerationInProgress = promptGenerationInProgress,
                    onPromptGenerationPressed = onPromptGenerationPressed
                )
            }
        }
    }
}

@Composable
private fun HelpWriteButton(
    promptGenerationInProgress: Boolean,
    onPromptGenerationPressed: () -> Unit,
) {
    val color = if (promptGenerationInProgress) {
        Brush.infinitelyAnimatingLinearGradient(
            listOf(LimeGreen, Primary90, Secondary)
        )
    } else SolidColor(MaterialTheme.colorScheme.surfaceContainerLow)
    GradientAssistElevatedChip(
        onClick = {
            onPromptGenerationPressed()
        }, label = {
            val label =
                if (promptGenerationInProgress) CreationR.string.writing else CreationR.string.write_me_a_prompt
            Text(text = stringResource(label))
        }, leadingIcon = {
            Icon(
                painter = rememberVectorPainter(ImageVector.vectorResource(CreationR.drawable.pen_spark_24)),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )
        }, colors = gradientChipColorDefaults().copy(
            containerColor = color, disabledContainerColor = color
        ), enabled = !promptGenerationInProgress
    )
}

@Composable
fun PromptTypeToolbar(
    modifier: Modifier = Modifier,
    selectedOption: PromptType,
    onOptionSelected: (PromptType) -> Unit,
) {
    val options = PromptType.entries
    HorizontalFloatingToolbar(
        modifier = modifier.border(
            2.dp,
            color = MaterialTheme.colorScheme.outline,
            shape = MaterialTheme.shapes.large
        ), colors = FloatingToolbarColors(
            toolbarContainerColor = MaterialTheme.colorScheme.surface,
            toolbarContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            fabContainerColor = MaterialTheme.colorScheme.tertiary,
            fabContentColor = MaterialTheme.colorScheme.onTertiary
        ), expanded = true
    ) {
        options.forEachIndexed { index, label ->
            val checked = selectedOption == label
            ToggleButton(
                modifier = Modifier,
                checked = checked,
                onCheckedChange = {
                    onOptionSelected(label)
                },
                shapes = ToggleButtonDefaults.shapes(checkedShape = MaterialTheme.shapes.large),
                colors = ToggleButtonDefaults.toggleButtonColors(
                    checkedContainerColor = MaterialTheme.colorScheme.onSurface,
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Text(label.displayName, maxLines = 1)
            }
            if (index != options.size - 1) {
                Spacer(Modifier.width(8.dp))
            }
        }
    }
}

@LargeScreensPreview
@Preview
@Composable
private fun UploadEmptyPreview() {
    ProTheme {
        UploadEmptyState(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            onCameraPressed = {},
            onChooseImagePress = {})
        UploadEmptyState(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp),
            onCameraPressed = {},
            onChooseImagePress = {})
    }
}

@Composable
private fun UploadEmptyState(
    modifier: Modifier = Modifier,
    onCameraPressed: () -> Unit,
    onChooseImagePress: () -> Unit,
) {
    Column(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(28.dp)
            )
            .dashedRoundedRectBorder(
                2.dp, MaterialTheme.colorScheme.outline, cornerRadius = 28.dp
            )
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            stringResource(CreationR.string.photo_picker_title),
            fontSize = 28.sp,
            textAlign = TextAlign.Center,
            lineHeight = 40.sp,
            maxLines = 2,
            minLines = 2
        )
        Spacer(Modifier.height(16.dp))
        TakePhotoButton(onCameraPressed)
        Spacer(Modifier.height(32.dp))
        SecondaryOutlinedButton(onClick = {
            onChooseImagePress()
        }, leadingIcon = {
            Image(
                painterResource(CreationR.drawable.choose_picture_image),
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .sizeIn(24.dp)
            )
        }, buttonText = stringResource(CreationR.string.photo_picker_choose_photo_label))
    }
}

@Composable
private fun TakePhotoButton(onCameraPressed: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec<Float>()
    val sharedElementScope = LocalSharedTransitionScope.current
    with(sharedElementScope) {
        Box(
            modifier = Modifier
                .defaultMinSize(minHeight = 48.dp, minWidth = 48.dp)
                .sizeIn(
                    minHeight = 48.dp,
                    minWidth = 48.dp,
                    maxWidth = ButtonDefaults.ExtraLargeContainerHeight,
                    maxHeight = ButtonDefaults.ExtraLargeContainerHeight
                )
                .aspectRatio(1f, matchHeightConstraintsFirst = true)
                .indication(
                    interactionSource = interaction,
                    ScaleIndicationNodeFactory(animationSpec)
                )
                .background(
                    MaterialTheme.colorScheme.onSurface,
                    MaterialShapes.Cookie9Sided.toShape()
                )
                .clickable(
                    interactionSource = interaction,
                    indication = ripple(color = Color.White),
                    onClick = {
                        onCameraPressed()
                    },
                    role = Role.Button,
                    enabled = true,
                    onClickLabel = stringResource(CreationR.string.take_picture_content_description)
                )
                .sharedBoundsRevealWithShapeMorph(
                    rememberSharedContentState(SharedElementKey.CameraButtonToFullScreenCamera),
                    restingShape = MaterialShapes.Cookie9Sided,
                    targetShape = RoundedPolygon.rectangle().normalized(),
                    targetValueByState = {
                        when (it) {
                            EnterExitState.PreEnter -> 0f
                            EnterExitState.Visible -> 1f
                            EnterExitState.PostExit -> 1f
                        }
                    }
                )
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(ThemeR.drawable.photo_camera),
                tint = MaterialTheme.colorScheme.surface,
                contentDescription = stringResource(CreationR.string.take_picture_content_description),
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.Center)
            )
        }
    }
}