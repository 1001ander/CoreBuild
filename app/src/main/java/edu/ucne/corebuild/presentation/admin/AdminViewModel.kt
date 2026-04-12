package edu.ucne.corebuild.presentation.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.corebuild.core.network.NetworkManager
import edu.ucne.corebuild.domain.auth.AuthManager
import edu.ucne.corebuild.domain.model.Component
import edu.ucne.corebuild.domain.repository.ComponentRepository
import edu.ucne.corebuild.util.Resource
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val componentRepository: ComponentRepository,
    private val networkManager: NetworkManager,
    private val authManager: AuthManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    init {
        loadComponents()
        checkConnectivity()
    }

    private fun loadComponents() {
        viewModelScope.launch {
            componentRepository.getComponents().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _uiState.update { it.copy(components = resource.data ?: emptyList(), isLoading = false) }
                    }
                    is Resource.Error -> {
                        _uiState.update { it.copy(isLoading = false, errorMessage = resource.message) }
                    }
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }

    private fun checkConnectivity() {
        _uiState.update {
            it.copy(isOnline = networkManager.isOnline())
        }
    }

    fun onEvent(event: AdminEvent) {
        when (event) {
            AdminEvent.OnLoadComponents -> loadComponents()
            is AdminEvent.OnSelectType ->
                _uiState.update { it.copy(selectedType = event.type) }
            is AdminEvent.OnCreateComponent -> createComponent(event.component)
            is AdminEvent.OnUpdateComponent -> updateComponent(event.component)
            is AdminEvent.OnDeleteComponent ->
                deleteComponent(event.id, event.type)
            is AdminEvent.OnSelectComponent ->
                _uiState.update {
                    it.copy(
                        selectedComponent = event.component,
                        showEditDialog = true
                    )
                }
            AdminEvent.OnDismissDialog ->
                _uiState.update {
                    it.copy(
                        showCreateDialog = false,
                        showEditDialog = false,
                        selectedComponent = null
                    )
                }
            AdminEvent.DismissMessage ->
                _uiState.update {
                    it.copy(
                        successMessage = null,
                        errorMessage = null
                    )
                }
            else -> {}
        }
    }

    private fun createComponent(component: Component) {
        if (!networkManager.isOnline()) {
            _uiState.update {
                it.copy(errorMessage = "Se requiere conexión para crear componentes")
            }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            componentRepository.addComponent(component)
                .fold(
                    onSuccess = {
                        _uiState.update {
                            it.copy(
                                isSaving = false,
                                successMessage = "Componente creado correctamente",
                                showCreateDialog = false
                            )
                        }
                    },
                    onFailure = { e ->
                        _uiState.update {
                            it.copy(
                                isSaving = false,
                                errorMessage = e.message ?: "Error al crear"
                            )
                        }
                    }
                )
        }
    }

    private fun updateComponent(component: Component) {
        if (!networkManager.isOnline()) {
            _uiState.update {
                it.copy(errorMessage = "Se requiere conexión para editar componentes")
            }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            componentRepository.updateComponent(component)
                .fold(
                    onSuccess = {
                        _uiState.update {
                            it.copy(
                                isSaving = false,
                                successMessage = "Componente actualizado",
                                showEditDialog = false,
                                selectedComponent = null
                            )
                        }
                    },
                    onFailure = { e ->
                        _uiState.update {
                            it.copy(
                                isSaving = false,
                                errorMessage = e.message ?: "Error al actualizar"
                            )
                        }
                    }
                )
        }
    }

    private fun deleteComponent(id: Int, type: String) {
        if (!networkManager.isOnline()) {
            _uiState.update {
                it.copy(errorMessage = "Se requiere conexión para eliminar componentes")
            }
            return
        }
        viewModelScope.launch {
            componentRepository.deleteComponent(id, type)
                .fold(
                    onSuccess = {
                        _uiState.update {
                            it.copy(successMessage = "Componente eliminado")
                        }
                    },
                    onFailure = { e ->
                        _uiState.update {
                            it.copy(errorMessage = e.message ?: "Error al eliminar")
                        }
                    }
                )
        }
    }
}
