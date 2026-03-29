package edu.ucne.corebuild.presentation.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.corebuild.domain.buildscore.BuildScoreCalculator
import edu.ucne.corebuild.domain.compatibility.CompatibilityEngine
import edu.ucne.corebuild.domain.model.Order
import edu.ucne.corebuild.domain.model.OrderMode
import edu.ucne.corebuild.domain.repository.CartRepository
import edu.ucne.corebuild.domain.repository.OrderRepository
import edu.ucne.corebuild.domain.repository.UserRepository
import edu.ucne.corebuild.presentation.notifications.NotificationHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository,
    private val compatibilityEngine: CompatibilityEngine,
    private val buildScoreCalculator: BuildScoreCalculator,
    private val notificationHelper: NotificationHelper
) : ViewModel() {

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    private val _showOrderConfirmation = MutableStateFlow(false)
    private val _navigateToLogin = MutableStateFlow(false)
    private val _navigateToThanks = MutableStateFlow(false)

    val uiState: StateFlow<CartUiState> = combine(
        cartRepository.getCartItems(),
        cartRepository.getCartTotal(),
        userRepository.getLoggedUser(),
        _snackbarMessage,
        _showOrderConfirmation,
        _navigateToLogin,
        _navigateToThanks
    ) { flows ->
        val items = flows[0] as List<edu.ucne.corebuild.domain.model.CartItem>
        val total = flows[1] as Double
        val user = flows[2] as edu.ucne.corebuild.domain.model.User?
        val message = flows[3] as String?
        val showConfirmation = flows[4] as Boolean
        val navLogin = flows[5] as Boolean
        val navThanks = flows[6] as Boolean
        
        val score = buildScoreCalculator.calculateScore(items)
        CartUiState(
            cartItems = items,
            total = total,
            warnings = compatibilityEngine.checkCompatibility(items),
            snackbarMessage = message,
            showOrderConfirmation = showConfirmation,
            buildScore = score.score,
            buildLabel = score.label,
            buildRecommendations = score.recommendations,
            isLoading = false,
            isLogged = user != null,
            navigateToLogin = navLogin,
            navigateToThanks = navThanks
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CartUiState(isLoading = true)
    )

    fun onEvent(event: CartEvent) {
        viewModelScope.launch {
            when (event) {
                is CartEvent.RemoveFromCart -> {
                    cartRepository.removeComponent(event.componentId)
                    _snackbarMessage.value = "Producto eliminado"
                }
                is CartEvent.UpdateQuantity -> {
                    val item = uiState.value.cartItems.find { it.component.id == event.componentId }
                    if (item != null) {
                        val limit = compatibilityEngine.getLimitForCategory(item.component)
                        if (event.quantity > limit) {
                            _snackbarMessage.value = "Límite alcanzado: Máximo $limit unidades"
                        } else {
                            cartRepository.updateQuantity(event.componentId, event.quantity)
                        }
                    }
                }
                CartEvent.ClearCart -> {
                    cartRepository.clearCart()
                    _snackbarMessage.value = "Carrito vaciado"
                }
                CartEvent.OnCheckout -> {
                    if (!uiState.value.isLogged) {
                        _navigateToLogin.value = true
                        return@launch
                    }

                    val currentItems = uiState.value.cartItems
                    if (currentItems.isNotEmpty()) {
                        val totalPrice = uiState.value.total
                        val orderComponents = currentItems.flatMap { item -> 
                            List(item.quantity) { item.component } 
                        }
                        

                        val order = Order(
                            components = orderComponents,
                            totalPrice = totalPrice,
                            date = Date(),
                            status = OrderMode.CREATED
                        )
                        orderRepository.createOrder(order)
                        cartRepository.clearCart()
                        
                        _showOrderConfirmation.value = true
                        _snackbarMessage.value = "¡Pedido realizado con éxito!"
                        
                        viewModelScope.launch {
                            delay(5000)
                            val orders = orderRepository.getAllOrders().first()
                            val lastOrder = orders.maxByOrNull { it.id }
                            if (lastOrder != null) {
                                orderRepository.createOrder(lastOrder.copy(status = OrderMode.ENVIADO))
                            }
                            
                            notificationHelper.sendOrderDeliveredNotification()
                            _showOrderConfirmation.value = false
                        }
                    } else {
                        _snackbarMessage.value = "El carrito está vacío"
                    }
                }
                CartEvent.DismissSnackbar -> {
                    _snackbarMessage.value = null
                }
                CartEvent.ResetNavigation -> {
                   _navigateToLogin.value = false
                   _navigateToThanks.value = false
                }
            }
        }
    }
}
