package edu.ucne.corebuild.presentation.comparator

import edu.ucne.corebuild.domain.model.Component

sealed interface ComparatorEvent {
    data class SelectType(val type: String) : ComparatorEvent
    data class SelectComponent1(val component: Component?) : ComparatorEvent
    data class SelectComponent2(val component: Component?) : ComparatorEvent
}
