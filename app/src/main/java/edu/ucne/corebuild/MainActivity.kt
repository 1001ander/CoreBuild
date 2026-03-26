package edu.ucne.corebuild

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import dagger.hilt.android.AndroidEntryPoint
import edu.ucne.corebuild.presentation.detail.ProductDetailScreen
import edu.ucne.corebuild.presentation.home.HomeScreen
import edu.ucne.corebuild.presentation.navigation.Screen
import edu.ucne.corebuild.ui.theme.CoreBuildTheme

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
                    CoreBuildNavHost()
                }
            }
        }
    }
}

@Composable
fun CoreBuildNavHost() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.Home
    ) {
        composable<Screen.Home> {
            HomeScreen(
                onComponentClick = { id ->
                    navController.navigate(Screen.Detail(id))
                }
            )
        }
        composable<Screen.Detail> { backStackEntry ->
            val detail: Screen.Detail = backStackEntry.toRoute()
            ProductDetailScreen(
                id = detail.id,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}
