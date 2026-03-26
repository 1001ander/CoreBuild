package edu.ucne.corebuild.domain.use_case

import edu.ucne.corebuild.domain.model.Component
import edu.ucne.corebuild.domain.repository.ComponentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetComponentsUseCase @Inject constructor(
    private val repository: ComponentRepository
) {
    operator fun invoke(): Flow<List<Component>> {
        return repository.getComponents()
    }
}
