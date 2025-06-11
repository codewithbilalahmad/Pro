package com.muhammad.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.muhammad.theme.SharedElementContextPreview
import com.muhammad.theme.components.ProTopAppBar

@Preview
@Composable
fun AppInactivePreview() {
    SharedElementContextPreview {
        AppInActiveScreen()
    }
}

@Composable
fun AppInActiveScreen() {
    Scaffold(topBar = {
        ProTopAppBar()
    }) { contentPadding ->
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.secondary)
                .fillMaxSize()
                .padding(contentPadding)
                .padding(64.dp), verticalArrangement = Arrangement.Center
        ) {
            Text(
                stringResource(R.string.check_back_later),
                style = MaterialTheme.typography.titleLarge.copy(textAlign = TextAlign.Center),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.size(16.dp))
            Text(
                stringResource(R.string.app_is_under_construction),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}