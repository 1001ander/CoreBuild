package edu.ucne.corebuild.presentation.home

import edu.ucne.corebuild.domain.model.Component

data class HomeUiState(
    val components: List<Component> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
