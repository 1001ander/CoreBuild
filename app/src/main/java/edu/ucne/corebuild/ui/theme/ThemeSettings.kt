package edu.ucne.corebuild.ui.theme

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

object ThemeSettings {
    var themeState by mutableStateOf(ThemeMode.SYSTEM)
}

enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}
