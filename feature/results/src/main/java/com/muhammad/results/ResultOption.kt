package com.muhammad.results

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarColors
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.muhammad.theme.ProTheme

@Preview
@Composable
private fun ResultToolbarPreview() {
    ProTheme {
        Column {
            ResultToolbarOption {  }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ResultToolbarOption(
    modifier: Modifier = Modifier,
    selectedOption: ResultOption = ResultOption.ResultImage,
    wasPromptUsed: Boolean = false,
    onResultOptionSelected: (ResultOption) -> Unit,
) {
    val options = ResultOption.entries
    HorizontalFloatingToolbar(
        modifier = modifier
            .padding(start = 16.dp, end = 16.dp, top = 16.dp)
            .border(
                2.dp,
                MaterialTheme.colorScheme.outline, MaterialTheme.shapes.large
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
                    onResultOptionSelected(label)
                },
                shapes = ToggleButtonDefaults.shapes(checkedShape = MaterialTheme.shapes.large),
                colors = ToggleButtonDefaults.toggleButtonColors(
                    checkedContainerColor = MaterialTheme.colorScheme.onSurface,
                    checkedContentColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Text(stringResource(label.displayText(wasPromptUsed = wasPromptUsed)), maxLines = 1)
            }
            if(index != options.size -1){
                Spacer(Modifier.width(8.dp))
            }
        }
    }
}

enum class ResultOption(val displayName: Int) {
    OriginalInput(R.string.photo),
    ResultImage(R.string.bot);

    fun toFlippableState(): FlippableState {
        return when (this) {
            OriginalInput -> FlippableState.Back
            ResultImage -> FlippableState.Front
        }
    }

    fun displayText(wasPromptUsed: Boolean): Int {
        return if (this == OriginalInput) {
            if (wasPromptUsed) return R.string.prompt else R.string.photo
        } else {
            this.displayName
        }
    }
}