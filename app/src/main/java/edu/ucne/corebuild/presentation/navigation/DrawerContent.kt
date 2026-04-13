package edu.ucne.corebuild.presentation.navigation

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.SettingsSuggest
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import edu.ucne.corebuild.domain.model.User
import edu.ucne.corebuild.ui.theme.ThemeMode
import edu.ucne.corebuild.ui.theme.ThemeSettings

@Composable
fun CoreBuildDrawerContent(
    isLogged: Boolean,
    isAdmin: Boolean,
    user: User?,
    currentRoute: String?,
    onProfileClick: () -> Unit,
    onNavigate: (Screen) -> Unit,
    onThemeChange: (ThemeMode) -> Unit
) {
    val context = LocalContext.current

    ModalDrawerSheet(
        drawerContainerColor = MaterialTheme.colorScheme.surface,
        drawerTonalElevation = 4.dp,
        modifier = Modifier.width(280.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    .clickable { onProfileClick() },
                contentAlignment = Alignment.Center
            ) {
                if (user?.profilePicture != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(user.profilePicture)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Perfil",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Perfil",
                        modifier = Modifier.size(28.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = if (isLogged) user?.name ?: "Usuario" else "Invitado",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (isLogged) "Ver perfil" else "Toca para iniciar sesión",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onProfileClick() }
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp))
        Spacer(modifier = Modifier.height(12.dp))
        
        DrawerItem(
            icon = Icons.Default.Home,
            label = "Inicio",
            selected = currentRoute?.contains("Home") == true,
            onClick = { onNavigate(Screen.Home) }
        )
        DrawerItem(
            icon = Icons.Default.Favorite,
            label = "Mis Favoritos",
            selected = currentRoute?.contains("Favorites") == true,
            onClick = { onNavigate(Screen.Favorites) }
        )
        DrawerItem(
            icon = Icons.Default.ReceiptLong,
            label = "Mis Pedidos",
            selected = currentRoute?.contains("Orders") == true,
            onClick = { onNavigate(Screen.Orders) }
        )
        DrawerItem(
            icon = Icons.Default.ShoppingCart,
            label = "Mi Carrito",
            selected = currentRoute?.contains("Cart") == true,
            onClick = { onNavigate(Screen.Cart) }
        )
        DrawerItem(
            icon = Icons.Default.Compare,
            label = "Comparador",
            selected = currentRoute?.contains("Comparator") == true,
            onClick = { onNavigate(Screen.Comparator) }
        )
        DrawerItem(
            icon = Icons.Default.Speed,
            label = "Cuello Botella",
            selected = currentRoute?.contains("Bottleneck") == true,
            onClick = { onNavigate(Screen.Bottleneck) }
        )
        DrawerItem(
            icon = Icons.Default.VideogameAsset,
            label = "Simulador FPS",
            selected = currentRoute?.contains("Performance") == true,
            onClick = { onNavigate(Screen.Performance) }
        )
        DrawerItem(
            icon = Icons.Default.AutoAwesome,
            label = "Recomendador IA",
            selected = currentRoute?.contains("BuildSelector") == true || currentRoute?.contains("Recommendation") == true || currentRoute?.contains("SmartBuild") == true,
            onClick = { onNavigate(Screen.BuildSelector) }
        )
        
        if (isAdmin) {
            HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp))
            Text(
                "Administración",
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
            DrawerItem(
                icon = Icons.Outlined.AdminPanelSettings,
                label = "Panel Admin",
                selected = currentRoute?.contains("AdminPanel") == true,
                onClick = { onNavigate(Screen.AdminPanel) }
            )
            DrawerItem(
                icon = Icons.Outlined.History,
                label = "Historial de cambios",
                selected = currentRoute?.contains("AdminLogs") == true,
                onClick = { onNavigate(Screen.AdminLogs) }
            )
        }

        Spacer(modifier = Modifier.weight(1f))
        HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ThemeOption(Icons.Outlined.LightMode, ThemeSettings.themeState == ThemeMode.LIGHT) {
                onThemeChange(ThemeMode.LIGHT)
            }
            ThemeOption(Icons.Outlined.SettingsSuggest, ThemeSettings.themeState == ThemeMode.SYSTEM) {
                onThemeChange(ThemeMode.SYSTEM)
            }
            ThemeOption(Icons.Outlined.DarkMode, ThemeSettings.themeState == ThemeMode.DARK) {
                onThemeChange(ThemeMode.DARK)
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun DrawerItem(icon: ImageVector, label: String, selected: Boolean, onClick: () -> Unit) {
    NavigationDrawerItem(
        icon = { Icon(icon, contentDescription = null) },
        label = { Text(label) },
        selected = selected,
        onClick = onClick,
        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
    )
}

@Composable
private fun ThemeOption(icon: ImageVector, selected: Boolean, onClick: () -> Unit) {
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
