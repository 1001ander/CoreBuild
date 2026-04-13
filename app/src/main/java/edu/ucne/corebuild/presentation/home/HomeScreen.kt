package edu.ucne.corebuild.presentation.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import edu.ucne.corebuild.domain.model.Component
import edu.ucne.corebuild.presentation.components.AmazonGridItem
import edu.ucne.corebuild.presentation.components.AnimatedFilterChip
import edu.ucne.corebuild.presentation.components.AnimatedListItem
import edu.ucne.corebuild.presentation.components.bounceClick
import edu.ucne.corebuild.presentation.components.toPrice
import edu.ucne.corebuild.ui.theme.CoreBuildTheme
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onComponentClick: (Int) -> Unit,
    onCartClick: () -> Unit,
    onMenuClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    DisposableEffect(Unit) {
        viewModel.onScreenEnter()
        onDispose { viewModel.onScreenExit() }
    }

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collectLatest { event ->
            when (event) {
                HomeNavigationEvent.NavigateToCart -> onCartClick()
            }
        }
    }

    HomeScreenContent(
        state = state,
        onEvent = viewModel::onEvent,
        onComponentClick = { id ->
            viewModel.recordComponentClick(id)
            onComponentClick(id)
        },
        onCartClick = onCartClick,
        onMenuClick = onMenuClick
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreenContent(
    state: HomeUiState,
    onEvent: (HomeEvent) -> Unit,
    onComponentClick: (Int) -> Unit,
    onCartClick: () -> Unit,
    onMenuClick: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    if (state.showBuildDialog && state.featuredBuild != null) {
        FeaturedBuildDialog(
            build = state.featuredBuild,
            onDismiss = { onEvent(HomeEvent.OnToggleBuildDialog) },
            onAddToCart = { onEvent(HomeEvent.OnAddFeaturedToCart) }
        )
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("CoreBuild", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onMenuClick, modifier = Modifier.bounceClick()) {
                        Icon(Icons.Default.Menu, contentDescription = "Menú")
                    }
                },
                actions = {
                    IconButton(onClick = onCartClick, modifier = Modifier.bounceClick()) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Carrito")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                HomeSearchBar(
                    query = state.searchQuery,
                    onQueryChange = { onEvent(HomeEvent.OnSearchQueryChange(it)) }
                )
            }

            stickyHeader {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(bottom = 4.dp)
                ) {
                    CategoryFilter(
                        selectedCategory = state.selectedCategory,
                        onCategorySelected = { onEvent(HomeEvent.OnCategoryChange(it)) }
                    )
                }
            }

            if (state.isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            } else {
                if (state.selectedCategory == null && state.searchQuery.isBlank()) {
                    state.featuredBuild?.let { build ->
                        item {
                            FeaturedBuildSection(build = build, onBuildClick = { onEvent(HomeEvent.OnToggleBuildDialog) })
                        }
                    }

                    if (state.smartRecommendations.isNotEmpty()) {
                        SmartRecommendationsSection(
                            recommendations = state.smartRecommendations,
                            onComponentClick = onComponentClick
                        )
                    }

                    BrandSection("Procesadores Intel", "Potencia para gaming", state.intelComponents, onComponentClick)
                    BrandSection("Procesadores AMD Ryzen", "Eficiencia y núcleos", state.amdCpuComponents, onComponentClick)
                    BrandSection("NVIDIA GeForce RTX", "Ray Tracing y DLSS", state.nvidiaComponents, onComponentClick)
                    BrandSection("AMD Radeon RX", "Gráficos de alto nivel", state.radeonComponents, onComponentClick)

                    if (state.recentlyViewed.isNotEmpty()) {
                        item {
                            SectionHeader("Visto recientemente", "Tu historial")
                            ComponentHorizontalRow(state.recentlyViewed, onComponentClick)
                        }
                    }
                }

                item {
                    SectionHeader(
                        title = if (state.selectedCategory != null) "Categoría: ${state.selectedCategory}" else "Más componentes",
                        subtitle = "${state.filteredComponents.size} disponibles"
                    )
                }

                if (state.filteredComponents.isEmpty()) {
                    item { EmptyComponentsMessage() }
                } else {
                    item {
                        ComponentsGrid(
                            components = state.filteredComponents,
                            onComponentClick = onComponentClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FeaturedBuildSection(build: PredefinedBuild, onBuildClick: () -> Unit) {
    AnimatedListItem {
        FeaturedBuildCard(build = build, onClick = onBuildClick)
    }
}

@Composable
private fun SmartRecommendationsSection(
    recommendations: List<Component>,
    onComponentClick: (Int) -> Unit
) {
    Column {
        AnimatedListItem {
            SectionHeader(title = "✨ Recomendado para ti", subtitle = "Basado en tu actividad")
        }
        ComponentHorizontalRow(recommendations, onComponentClick)
    }
}

private fun LazyListScope.SmartRecommendationsSection(
    recommendations: List<Component>,
    onComponentClick: (Int) -> Unit
) {
    item {
        SectionHeader(title = "✨ Recomendado para ti", subtitle = "Basado en tu actividad")
    }
    item {
        ComponentHorizontalRow(recommendations, onComponentClick)
    }
}

private fun LazyListScope.BrandSection(
    title: String,
    subtitle: String,
    components: List<Component>,
    onComponentClick: (Int) -> Unit
) {
    if (components.isNotEmpty()) {
        item {
            SectionHeader(title, subtitle)
        }
        item {
            ComponentHorizontalRow(components, onComponentClick)
        }
    }
}

@Composable
private fun ComponentsGrid(
    components: List<Component>,
    onComponentClick: (Int) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        components.chunked(2).forEach { rowItems ->
            AnimatedListItem {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowItems.forEach { component ->
                        AmazonGridItem(
                            component = component,
                            onClick = onComponentClick,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (rowItems.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyComponentsMessage() {
    Box(
        modifier = Modifier.fillMaxWidth().padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("No se encontraron productos", color = MaterialTheme.colorScheme.outline)
    }
}

@Composable
fun SectionHeader(title: String, subtitle: String) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
    }
}

@Composable
fun CategoryFilter(selectedCategory: String?, onCategorySelected: (String?) -> Unit) {
    val categories = listOf("CPU", "GPU", "RAM", "Motherboard", "PSU")
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            AnimatedFilterChip(
                selected = selectedCategory == null,
                onClick = { onCategorySelected(null) },
                label = { Text("Todos") }
            )
        }
        items(categories) { category ->
            AnimatedFilterChip(
                selected = selectedCategory == category,
                onClick = { onCategorySelected(category) },
                label = { Text(category) }
            )
        }
    }
}

@Composable
fun HomeSearchBar(query: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        placeholder = { Text("¿Qué estás buscando?") },
        leadingIcon = { Icon(Icons.Default.Search, null) },
        singleLine = true,
        shape = MaterialTheme.shapes.medium
    )
}

@Composable
fun FeaturedBuildCard(build: PredefinedBuild, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().height(220.dp).padding(16.dp).bounceClick().clickable { onClick() },
        shape = MaterialTheme.shapes.extraLarge,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(model = build.imageUrl, contentDescription = build.name, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            Box(modifier = Modifier.fillMaxSize().background(androidx.compose.ui.graphics.Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)))))
            Column(modifier = Modifier.align(Alignment.BottomStart).padding(20.dp)) {
                Surface(color = MaterialTheme.colorScheme.primary, shape = CircleShape) {
                    Text(text = "Build Destacada", modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, color = Color.White, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = build.name, style = MaterialTheme.typography.headlineSmall, color = Color.White, fontWeight = FontWeight.ExtraBold)
                Text(text = "Desde ${build.totalPrice.toPrice()}", style = MaterialTheme.typography.titleMedium, color = Color.White.copy(alpha = 0.9f), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun FeaturedBuildDialog(build: PredefinedBuild, onDismiss: () -> Unit, onAddToCart: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(build.name, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text(build.description, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Componentes incluidos:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                build.components.forEach { component ->
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "• ${component.name}", style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(text = component.price.toPrice(), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total Build:", fontWeight = FontWeight.Bold)
                    Text(build.totalPrice.toPrice(), fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                }
            }
        },
        confirmButton = { Button(onClick = onAddToCart) { Text("Añadir Todo al Carrito") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cerrar") } }
    )
}

@Composable
fun ComponentHorizontalRow(components: List<Component>, onComponentClick: (Int) -> Unit) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(components) { component ->
            AnimatedListItem {
                AmazonGridItem(component, onComponentClick)
            }
        }
    }
    Spacer(modifier = Modifier.height(24.dp))
}
