package edu.ucne.corebuild.presentation.home

sealed interface HomeEvent {
    data object LoadComponents : HomeEvent
}
