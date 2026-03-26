package edu.ucne.corebuild.presentation.navigation

import kotlinx.serialization.Serializable

sealed class Screen {
    @Serializable
    data object Home : Screen()

    @Serializable
    data class Detail(val id: Int) : Screen()

    @Serializable
    data object Cart : Screen()

    @Serializable
    data object Comparator : Screen()

    @Serializable
    data object Bottleneck : Screen()
}
