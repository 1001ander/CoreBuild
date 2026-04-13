package edu.ucne.corebuild.presentation.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import edu.ucne.corebuild.presentation.admin.AdminScreen
import edu.ucne.corebuild.presentation.admin.ComponentFormScreen
import edu.ucne.corebuild.presentation.admin.logs.LogsScreen
import edu.ucne.corebuild.presentation.auth.AuthViewModel
import edu.ucne.corebuild.presentation.auth.LoginScreen
import edu.ucne.corebuild.presentation.auth.ProfileScreen
import edu.ucne.corebuild.presentation.auth.RegisterScreen
import edu.ucne.corebuild.presentation.bottleneck.BottleneckScreen
import edu.ucne.corebuild.presentation.cart.CartScreen
import edu.ucne.corebuild.presentation.comparator.ComparatorScreen
import edu.ucne.corebuild.presentation.detail.ProductDetailScreen
import edu.ucne.corebuild.presentation.favorites.FavoritesScreen
import edu.ucne.corebuild.presentation.home.HomeScreen
import edu.ucne.corebuild.presentation.orders.OrderDetailScreen
import edu.ucne.corebuild.presentation.orders.OrdersScreen
import edu.ucne.corebuild.presentation.performance.PerformanceScreen
import edu.ucne.corebuild.presentation.recommendation.RecommendationScreen
import edu.ucne.corebuild.presentation.smartbuild.BuildSelectorScreen
import edu.ucne.corebuild.presentation.smartbuild.SmartBuildScreen

@Composable
fun CoreBuildNavGraph(
    navController: NavHostController,
    startDestination: Screen,
    authViewModel: AuthViewModel,
    onMenuClick: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { fadeIn(animationSpec = tween(300)) + slideInHorizontally(animationSpec = tween(300)) { it } },
        exitTransition = { fadeOut(animationSpec = tween(300)) + slideOutHorizontally(animationSpec = tween(300)) { -it } }
    ) {
        composable<Screen.Home> {
            HomeScreen(
                onComponentClick = { id -> navController.navigate(Screen.Detail(id)) },
                onCartClick = { navController.navigate(Screen.Cart) },
                onMenuClick = onMenuClick
            )
        }
        composable<Screen.Login> {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = { navController.popBackStack() },
                onRegisterClick = { navController.navigate(Screen.Register) }
            )
        }
        composable<Screen.Register> {
            RegisterScreen(
                viewModel = authViewModel,
                onRegisterSuccess = { navController.popBackStack() },
                onLoginClick = { navController.navigate(Screen.Login) }
            )
        }
        composable<Screen.Profile> {
            ProfileScreen(
                onBackClick = { navController.popBackStack() },
                onLogoutSuccess = { 
                    navController.navigate(Screen.Home) { 
                        popUpTo(Screen.Home) { inclusive = true } 
                    } 
                }
            )
        }
        composable<Screen.Cart> {
            CartScreen(
                onBackClick = { navController.popBackStack() },
                onMenuClick = onMenuClick,
                onNavigateToLogin = { navController.navigate(Screen.Login) },
                onNavigateToThanks = { navController.navigate(Screen.Thanks) }
            )
        }
        composable<Screen.Thanks> {
            ThanksScreen(
                onNavigateHome = {
                    navController.navigate(Screen.Home) {
                        popUpTo(Screen.Home) { inclusive = true }
                    }
                }
            )
        }
        composable<Screen.Favorites> {
            FavoritesScreen(
                onComponentClick = { id -> navController.navigate(Screen.Detail(id)) },
                onMenuClick = onMenuClick
            )
        }
        composable<Screen.Orders> {
            OrdersScreen(
                onOrderClick = { id -> navController.navigate(Screen.OrderDetail(id)) },
                onMenuClick = onMenuClick
            )
        }
        composable<Screen.OrderDetail> { backStackEntry ->
            val detail: Screen.OrderDetail = backStackEntry.toRoute()
            OrderDetailScreen(orderId = detail.orderId, onBackClick = { navController.popBackStack() })
        }
        composable<Screen.Detail> { backStackEntry ->
            val detail: Screen.Detail = backStackEntry.toRoute()
            ProductDetailScreen(id = detail.id, onBackClick = { navController.popBackStack() }, onCartClick = { navController.navigate(Screen.Cart) })
        }
        composable<Screen.Comparator> {
            ComparatorScreen(onMenuClick = onMenuClick)
        }
        composable<Screen.Bottleneck> {
            BottleneckScreen(onMenuClick = onMenuClick)
        }
        composable<Screen.Performance> {
            PerformanceScreen(onMenuClick = onMenuClick)
        }
        composable<Screen.BuildSelector> {
            BuildSelectorScreen(
                onRecommendationClick = { navController.navigate(Screen.Recommendation) },
                onSmartBuildClick = { navController.navigate(Screen.SmartBuild) }
            )
        }
        composable<Screen.Recommendation> {
            RecommendationScreen(
                onMenuClick = onMenuClick,
                onComponentClick = { id -> navController.navigate(Screen.Detail(id)) }
            )
        }
        composable<Screen.SmartBuild> {
            SmartBuildScreen(
                onBackClick = { navController.popBackStack() },
                onComponentClick = { id -> navController.navigate(Screen.Detail(id)) },
                onCartClick = { navController.navigate(Screen.Cart) }
            )
        }
        composable<Screen.AdminPanel> {
            AdminScreen(
                onLogsClick = { navController.navigate(Screen.AdminLogs) },
                onAddNewClick = { navController.navigate(Screen.ComponentForm) },
                onEditClick = { _ -> navController.navigate(Screen.EditComponent) },
                onBack = { navController.navigateUp() }
            )
        }
        composable<Screen.AdminLogs> {
            LogsScreen(onBack = { navController.navigateUp() })
        }
        composable<Screen.ComponentForm> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<Screen.AdminPanel>()
            }
            ComponentFormScreen(
                parentEntry = parentEntry,
                isEditing = false,
                onBack = { navController.navigateUp() }
            )
        }
        composable<Screen.EditComponent> { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry<Screen.AdminPanel>()
            }
            ComponentFormScreen(
                parentEntry = parentEntry,
                isEditing = true,
                onBack = { navController.navigateUp() }
            )
        }
    }
}

@Composable
fun ThanksScreen(onNavigateHome: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "¡Gracias por tu compra!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Estamos preparando tu hardware. Recibirás una notificación cuando sea entregado.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(48.dp))
        Button(
            onClick = onNavigateHome,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large
        ) {
            Text("Ir al inicio")
        }
    }
}
