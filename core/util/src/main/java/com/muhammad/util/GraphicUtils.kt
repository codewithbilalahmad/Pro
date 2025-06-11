package com.muhammad.util

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.PathSegment
import androidx.core.graphics.flatten

@Composable
fun Dp.toPx() = with(LocalDensity.current) {
    this@toPx.toPx()
}

@Composable
fun Int.toDp() = with(LocalDensity.current) {
    this@toDp.toDp()
}
fun Modifier.backgroundRepeatX(painter : Painter,desiredTileWidth: Float = painter.intrinsicSize.width) = this.then(
    Modifier.drawWithContent {
        drawRepeatX(painter = painter, desiredTileWidth = desiredTileWidth)
        drawContent()
    }
)
private fun DrawScope.drawRepeatX(painter : Painter,desiredTileWidth : Float){
    val imageWidth = desiredTileWidth
    val imageHeight = painter.intrinsicSize.height
    val canvasWidth = size.width
    var x = 0f
    while(x < canvasWidth){
        with(painter){
            translate(left = x){
                draw(size = Size(width = imageWidth, height = imageHeight))
            }
        }
        x += imageWidth
    }
}
fun Modifier.backgroundRepeatY(
    painter : Painter,
    desiredTileHeight: Float = painter.intrinsicSize.height
) = this.then(
    Modifier.drawWithContent {
        drawRepeatY(painter = painter, desiredTileHeight = desiredTileHeight)
        drawContent()
    }
)
private fun DrawScope.drawRepeatY(painter : Painter, desiredTileHeight : Float){
    val imageHeight = desiredTileHeight
    val imageWidth = painter.intrinsicSize.width
    val canvasHeight = size.height
    var y = 0f
    val ratio =  imageHeight / imageWidth
    val itemHeight = size.width * ratio
    while(y < canvasHeight){
        with(painter){
            translate(top = y){
                draw(
                    size = Size(width = size.width, height = size.height)
                )
            }
        }
        y += itemHeight
    }
}

@Composable
fun Modifier.dashedRoundedRectBorder(
    width: Dp,
    color: Color,
    cornerRadius: Dp,
    intervals: FloatArray = floatArrayOf(10f.dp.toPx(), 10f.dp.toPx()),
    phase: Float = 0f,
): Modifier = this.then(
    Modifier.drawBehind {
        val stroke = Stroke(
            width = width.toPx(),
            cap = StrokeCap.Round,
            pathEffect = PathEffect.dashPathEffect(intervals, phase)
        )
        drawRoundRect(
            color = color,
            style = stroke,
            cornerRadius = CornerRadius(cornerRadius.toPx(), cornerRadius.toPx())
        )
    }
)

@SuppressLint("NewApi")
fun Path.flatten(error: Float = 0.5f): Iterable<PathSegment> {
    return this.asAndroidPath().flatten(error)
}
