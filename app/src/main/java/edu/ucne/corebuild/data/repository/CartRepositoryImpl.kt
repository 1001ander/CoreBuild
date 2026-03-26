package edu.ucne.corebuild.data.repository

import edu.ucne.corebuild.domain.model.CartItem
import edu.ucne.corebuild.domain.model.Component
import edu.ucne.corebuild.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepositoryImpl @Inject constructor() : CartRepository {
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())

    override fun getCartItems(): Flow<List<CartItem>> = _cartItems

    override fun getCartTotal(): Flow<Double> = _cartItems.map { items ->
        items.sumOf { it.total }
    }

    override fun getCartItemCount(): Flow<Int> = _cartItems.map { items ->
        items.sumOf { it.quantity }
    }

    override suspend fun addComponent(component: Component) {
        _cartItems.update { items ->
            val existingItem = items.find { it.component.id == component.id }
            if (existingItem != null) {
                items.map {
                    if (it.component.id == component.id) it.copy(quantity = it.quantity + 1)
                    else it
                }
            } else {
                items + CartItem(component)
            }
        }
    }

    override suspend fun removeComponent(componentId: Int) {
        _cartItems.update { items ->
            items.filterNot { it.component.id == componentId }
        }
    }

    override suspend fun updateQuantity(componentId: Int, quantity: Int) {
        _cartItems.update { items ->
            if (quantity <= 0) {
                items.filterNot { it.component.id == componentId }
            } else {
                items.map {
                    if (it.component.id == componentId) it.copy(quantity = quantity)
                    else it
                }
            }
        }
    }

    override suspend fun clearCart() {
        _cartItems.value = emptyList()
    }
}
