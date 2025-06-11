package com.muhammad.results

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage




@Composable
fun BotResultCard(
    modifier: Modifier = Modifier,
    resultImage: Bitmap,
    originalImageUrl: Uri?,
    prompt: String?,
    flippableState: FlippableState,
    onFlipStateChange: ((FlippableState) -> Unit)? = null,
) {
    FlippableCard(
        modifier = modifier
            .rotate(-5f)
            .aspectRatio(ASPECT_RATIO)
            .padding(16.dp)
            .safeContentPadding(),
        flippableState = flippableState,
        onFlipStateChanged = onFlipStateChange,
        front = {
                FrontCard(bitmap = resultImage)
        }, back = {
            if(originalImageUrl != null){
                BackCard(originalImageUrl)
            } else{
                BackCardPrompt(prompt ?: "a Nice Dress with a Mop")
            }
        }
    )
}

@Composable
internal fun FrontCard(bitmap: Bitmap) {
    AsyncImage(
        model = bitmap,
        contentDescription = stringResource(R.string.original_image),
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxSize()
            .aspectRatio(ASPECT_RATIO)
            .shadow(
                8.dp,
                MaterialTheme.shapes.large
            )
            .clip(MaterialTheme.shapes.large)
    )
}

@Composable
private fun BackCard(originalImageUrl: Uri) {
    AsyncImage(
        model = originalImageUrl,
        contentDescription = stringResource(R.string.original_image),
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxSize()
            .aspectRatio(ASPECT_RATIO)
            .shadow(
                8.dp,
                MaterialTheme.shapes.large
            )
            .clip(MaterialTheme.shapes.large)
    )
}

@Composable
fun BackCardPrompt(prompt: String) {
    val annotatedString = buildAnnotatedString {
        pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
        append(stringResource(R.string.my_bot_is_wearing))
        append(" ")
        pop()
        pushStyle(SpanStyle(fontWeight = FontWeight.Normal))
        append("a Nice Dress with a Mop")
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .aspectRatio(ASPECT_RATIO)
            .shadow(8.dp, shape = MaterialTheme.shapes.large)
            .clip(MaterialTheme.shapes.large)
            .background(
                MaterialTheme.colorScheme.background
            )
            .padding(16.dp), verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = rememberVectorPainter(ImageVector.vectorResource(R.drawable.pen_spark)),
            modifier = Modifier.size(36.dp),
            contentDescription = null
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = annotatedString,
            style = MaterialTheme.typography.titleLarge.copy(textAlign = TextAlign.Center)
        )
    }
}

private const val ASPECT_RATIO = 3f / 4f