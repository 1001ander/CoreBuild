package edu.ucne.corebuild.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import edu.ucne.corebuild.domain.model.Component

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onComponentClick: (Int) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    HomeScreenContent(
        state = state,
        onComponentClick = onComponentClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    state: HomeUiState,
    onComponentClick: (Int) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Componentes CoreBuild") })
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.components) { component ->
                        ComponentItem(
                            component = component,
                            onClick = { onComponentClick(component.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ComponentItem(
    component: Component,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = component.name, style = MaterialTheme.typography.titleLarge)
            Text(text = component.category, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = "$${component.price}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
