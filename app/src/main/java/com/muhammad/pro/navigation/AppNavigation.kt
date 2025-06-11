package com.muhammad.pro.navigation

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.unit.IntOffset
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.muhammad.camera.CameraPreviewScreen
import com.muhammad.creation.CreationScreen
import com.muhammad.home.AboutScreen
import com.muhammad.home.HomeScreen
import com.muhammad.pro.navigation.Destinations.Create
import com.muhammad.theme.transitions.ColorSplashTransitionScreen

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AppNavigation() {
    val backStack = rememberMutableStateListOf<Destinations>(Destinations.Home)
    var positionReveal by remember {
        mutableStateOf(IntOffset.Zero)
    }
    var showSplash by remember {
        mutableStateOf(false)
    }
    val motionScheme = MaterialTheme.motionScheme
    NavDisplay(backStack = backStack, onBack = {
        backStack.removeLastOrNull()
    }, entryDecorators = listOf(
        rememberSavedStateNavEntryDecorator()
    ), transitionSpec = {
        ContentTransform(
            fadeIn(motionScheme.defaultEffectsSpec()),
            fadeOut(motionScheme.defaultEffectsSpec())
        )
    }, popTransitionSpec = {
        ContentTransform(
            fadeIn(motionScheme.defaultEffectsSpec()),
            scaleOut(targetScale = 0.7f)
        )
    }, entryProvider = entryProvider{
        entry<Destinations.Home>{
            HomeScreen(onClickLetsGo = {positionOffset ->
                showSplash = true
                positionReveal = positionOffset
            }, onAboutClick = {
                backStack.add(Destinations.About)
            })
        }
        entry<Destinations.About>{
            AboutScreen(onBackPressed = {
                backStack.removeLastOrNull()
            })
        }
        entry<Destinations.Create>{backStackEntry ->
            val fileName = backStackEntry.fileName
            CreationScreen(fileName = fileName, onBackPressed = {
                backStack.removeLastOrNull()
            }, onAboutClick = {
                backStack.add(Destinations.About)
            }, onCameraPressed = {
                backStack.removeAll{it is Destinations.Camera}
                backStack.add(Destinations.Camera)
            })
        }
        entry<Destinations.Camera>{
            CameraPreviewScreen(onImageCaptured = {uri ->
                backStack.removeAll { it is Create }
                backStack.add(Create(uri.toString()))
                backStack.removeAll{it is Destinations.Camera}
            })
        }
    })
    if(showSplash){
        ColorSplashTransitionScreen(startPoint = positionReveal, onTransitionFinished = {
            showSplash = false
        }, onTransitionMidpoint = {
            backStack.add(Create(fileName = null))
        })
    }
}