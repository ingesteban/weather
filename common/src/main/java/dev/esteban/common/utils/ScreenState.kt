package dev.esteban.common.utils

sealed class ScreenState {
    data object Loading : ScreenState()
    data object Success : ScreenState()
    data object Error : ScreenState()
    data object None : ScreenState()
}
