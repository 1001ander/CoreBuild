package edu.ucne.corebuild.presentation.navigation

import kotlinx.serialization.Serializable

sealed class Screen {
    @Serializable
    data object Home : Screen()

    @Serializable
    data class Detail(val id: Int) : Screen()
}
