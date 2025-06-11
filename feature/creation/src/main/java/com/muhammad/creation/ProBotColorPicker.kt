package com.muhammad.creation

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProBotColorPicker(
    modifier: Modifier = Modifier,
    selectedBotColor: BotColor,
    onBotColorSelected: (BotColor) -> Unit,
    listBotColors: List<BotColor>,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Text(stringResource(R.string.choose_my_bot_color), fontSize = 20.sp)
        Spacer(Modifier.height(24.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.align(
                Alignment.CenterHorizontally
            )
        ) {
            listBotColors.forEach { color ->
                ProBotIndividualColor(
                    botColor = color,
                    isSelected = selectedBotColor == color,
                    onSelected = { botColor ->
                        onBotColorSelected(botColor)
                    })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ProBotIndividualColor(
    botColor: BotColor,
    isSelected: Boolean,
    onSelected: (BotColor) -> Unit,
) {
    val clip by animateIntAsState(
        if (isSelected) 0 else 50, animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec()
    )
    Box(
        modifier = Modifier
            .border(
                2.dp,
                MaterialTheme.colorScheme.outline,
                RoundedCornerShape(clip)
            )
            .size(36.dp)
            .clip(RoundedCornerShape(clip))
            .clickable {
                onSelected(botColor)
            }) {
        DisplayBotColor(botColor = botColor)
        if (isSelected) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.rounded_check_24),
                contentDescription = stringResource(R.string.cd_bot_color_selected),
                modifier = Modifier.align(
                    Alignment.Center
                )
            )
        }
    }
}

@Composable
fun DisplayBotColor(modifier: Modifier = Modifier, botColor: BotColor) {
    when {
        botColor.imageRes != null -> {
            Image(
                painter = painterResource(botColor.imageRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = modifier.requiredSize(48.dp)
            )
        }

        botColor.color != null -> {
            Box(
                modifier = modifier
                    .semantics {
                        contentDescription = botColor.name
                        this.role = Role.Button
                    }
                    .background(botColor.color)
                    .requiredSize(48.dp))
        }
    }
}