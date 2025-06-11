package com.muhammad.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.muhammad.data.ConfigProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val configProvider: ConfigProvider,
) : ViewModel() {
    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isAppActive = !configProvider.isAppInActive(),
                    dancingDroidLink = configProvider.getDancingDroidLink(),
                    videoLink = configProvider.getPromoVideoLink()
                )
            }
        }
    }
}

data class HomeState(
    val isAppActive: Boolean = true,
    val videoLink: String? = null,
    val dancingDroidLink: String? = null,
)