package edu.ucne.corebuild

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import dagger.hilt.android.AndroidEntryPoint
import edu.ucne.corebuild.presentation.bottleneck.BottleneckScreen
import edu.ucne.corebuild.presentation.cart.CartScreen
import edu.ucne.corebuild.presentation.comparator.ComparatorScreen
import edu.ucne.corebuild.presentation.detail.ProductDetailScreen
import edu.ucne.corebuild.presentation.home.HomeScreen
import edu.ucne.corebuild.presentation.navigation.Screen
import edu.ucne.corebuild.ui.theme.CoreBuildTheme
import edu.ucne.corebuild.ui.theme.ThemeMode
import edu.ucne.corebuild.ui.theme.ThemeSettings
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CoreBuildTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CoreBuildAppContent()
                }
            }
        }
    }
}

@Composable
fun CoreBuildAppContent() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val items = listOf(
        NavigationItem("Inicio", Icons.Default.Home, Screen.Home),
        NavigationItem("Carrito", Icons.Default.ShoppingCart, Screen.Cart),
        NavigationItem("Comparador", Icons.Default.Info, Screen.Comparator),
        NavigationItem("Cuello de Botella", Icons.Default.Build, Screen.Bottleneck)
    )

    var selectedItem by remember { mutableStateOf(items[0]) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "CoreBuild",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                HorizontalDivider()
                
                Column(modifier = Modifier.weight(1f)) {
                    items.forEach { item ->
                        NavigationDrawerItem(
                            label = { Text(item.title) },
                            selected = item == selectedItem,
                            onClick = {
                                selectedItem = item
                                scope.launch { drawerState.close() }
                                navController.navigate(item.screen) {
                                    popUpTo(Screen.Home) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = null) },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                    }
                }

                HorizontalDivider()
                Text(
                    "Apariencia",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.labelLarge
                )
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ThemeOption(
                        icon = Icons.Default.LightMode,
                        selected = ThemeSettings.themeState == ThemeMode.LIGHT,
                        onClick = { ThemeSettings.themeState = ThemeMode.LIGHT }
                    )
                    ThemeOption(
                        icon = Icons.Default.DarkMode,
                        selected = ThemeSettings.themeState == ThemeMode.DARK,
                        onClick = { ThemeSettings.themeState = ThemeMode.DARK }
                    )
                    ThemeOption(
                        icon = Icons.Default.SettingsSuggest,
                        selected = ThemeSettings.themeState == ThemeMode.SYSTEM,
                        onClick = { ThemeSettings.themeState = ThemeMode.SYSTEM }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = Screen.Home,
            enterTransition = { fadeIn(animationSpec = tween(300)) + slideInHorizontally(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) + slideOutHorizontally(animationSpec = tween(300)) }
        ) {
            composable<Screen.Home> {
                HomeScreen(
                    onComponentClick = { id ->
                        navController.navigate(Screen.Detail(id))
                    },
                    onCartClick = {
                        navController.navigate(Screen.Cart)
                        selectedItem = items[1]
                    },
                    onMenuClick = {
                        scope.launch { drawerState.open() }
                    }
                )
            }
            composable<Screen.Detail> { backStackEntry ->
                val detail: Screen.Detail = backStackEntry.toRoute()
                ProductDetailScreen(
                    id = detail.id,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onCartClick = {
                        navController.navigate(Screen.Cart)
                        selectedItem = items[1]
                    }
                )
            }
            composable<Screen.Cart> {
                CartScreen(
                    onBackClick = {
                        navController.popBackStack()
                        selectedItem = items[0]
                    }
                )
            }
            composable<Screen.Comparator> {
                ComparatorScreen(
                    onMenuClick = {
                        scope.launch { drawerState.open() }
                    }
                )
            }
            composable<Screen.Bottleneck> {
                BottleneckScreen(
                    onMenuClick = {
                        scope.launch { drawerState.open() }
                    }
                )
            }
        }
    }
}

@Composable
fun ThemeOption(
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Icon(icon, contentDescription = null) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}

data class NavigationItem(
    val title: String,
    val icon: ImageVector,
    val screen: Any
)
