package edu.ucne.corebuild.presentation.admin

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import coil.compose.AsyncImage

@Composable
fun ComponentFormScreen(
    parentEntry: NavBackStackEntry,
    isEditing: Boolean = false,
    onBack: () -> Unit
) {
    val viewModel: AdminViewModel = hiltViewModel(parentEntry)
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.navigateBack) {
        if (state.navigateBack) {
            onBack()
            viewModel.onEvent(AdminEvent.OnResetForm)
        }
    }

    ComponentFormBody(
        formState = state.formState,
        isEditing = isEditing,
        isSaving = state.isSaving,
        isUploadingImage = state.isUploadingImage,
        errorMessage = state.errorMessage,
        successMessage = state.successMessage,
        onEvent = viewModel::onEvent,
        onBack = onBack,
        onSave = {
            val component = viewModel.buildComponentFromForm()
            if (component != null) {
                if (isEditing) {
                    viewModel.onEvent(AdminEvent.OnUpdateComponent(component))
                } else {
                    viewModel.onEvent(AdminEvent.OnCreateComponent)
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComponentFormBody(
    formState: ComponentFormState,
    isEditing: Boolean,
    isSaving: Boolean,
    isUploadingImage: Boolean,
    errorMessage: String?,
    successMessage: String?,
    onEvent: (AdminEvent) -> Unit,
    onBack: () -> Unit,
    onSave: () -> Unit
) {
    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { onEvent(AdminEvent.OnImageSelected(it.toString())) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (isEditing) "Editar Componente" else "Nuevo Componente")
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item {
                var expanded by remember { mutableStateOf(false) }
                val types = listOf("CPU", "GPU", "Motherboard", "RAM", "PSU")

                ExposedDropdownMenuBox(
                    expanded = if (isEditing) false else expanded,
                    onExpandedChange = { if (!isEditing) expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = formState.tipo,
                        onValueChange = {},
                        readOnly = true,
                        enabled = !isEditing,
                        label = { Text("Tipo de componente") },
                        trailingIcon = {
                            if (!isEditing)
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth()
                    )
                    if (!isEditing) {
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            types.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type) },
                                    onClick = {
                                        onEvent(AdminEvent.OnFormFieldChange("tipo", type))
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(150.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { imageLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (formState.imageUrl.isNotEmpty()) {
                            AsyncImage(
                                model = formState.imageUrl,
                                contentDescription = "Imagen seleccionada",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                Icons.Default.AddAPhoto,
                                contentDescription = "Seleccionar imagen",
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }

                    if (formState.imageUrl.isNotEmpty() &&
                        !formState.imageUrl.startsWith("http")
                    ) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "⚠️ Imagen local. Súbela para que todos puedan verla.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Button(
                            onClick = { onEvent(AdminEvent.OnUploadImage) },
                            enabled = !isUploadingImage,
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            if (isUploadingImage) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Subir a Cloudinary")
                            }
                        }
                    }

                    if (successMessage?.contains("Imagen") == true) {
                        Text(
                            "✅ Imagen subida y lista",
                            color = Color(0xFF4CAF50),
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    FormField(formState.nombre, "nombre", "Nombre del producto", onEvent)
                    FormField(
                        formState.marca, "marca", "Marca", onEvent,
                        supportingText = "Obligatorio para aparecer en el inicio"
                    )
                    FormField(
                        formState.precioUsd, "precioUsd", "Precio (USD)", onEvent,
                        keyboardType = KeyboardType.Decimal,
                        prefix = { Text("$ ") }
                    )
                    FormField(formState.descripcion, "descripcion", "Descripción", onEvent)
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    when (formState.tipo) {
                        "CPU" -> {
                            FormField(formState.socket, "socket", "Socket ej: AM5, LGA1700", onEvent)
                            FormField(formState.generacion, "generacion", "Generación ej: Zen 4", onEvent)
                            FormField(formState.nucleos, "nucleos", "Núcleos", onEvent, KeyboardType.Number)
                            FormField(formState.hilos, "hilos", "Hilos", onEvent, KeyboardType.Number)
                            FormField(formState.frecuenciaBase, "frecuenciaBase", "Frecuencia base", onEvent, suffix = "GHz")
                            FormField(formState.frecuenciaTurbo, "frecuenciaTurbo", "Frecuencia turbo", onEvent, suffix = "GHz")
                            FormField(formState.cacheL3, "cacheL3", "Caché L3", onEvent, isOptional = true, suffix = "MB")
                            FormField(formState.tdpWatts, "tdpWatts", "TDP", onEvent, KeyboardType.Number, suffix = "W")
                            FormField(formState.graficosIntegrados, "graficosIntegrados", "Gráficos integrados", onEvent, isOptional = true)
                            FormField(formState.soporteRam, "soporteRam", "Soporte RAM", onEvent, isOptional = true)
                        }
                        "GPU" -> {
                            FormField(formState.chipset, "chipset", "Chipset ej: AD102", onEvent)
                            FormField(formState.vram, "vram", "VRAM", onEvent, suffix = "GB")
                            FormField(formState.tipoVram, "tipoVram", "Tipo VRAM ej: GDDR6X", onEvent)
                            FormField(formState.busMemoria, "busMemoria", "Bus de memoria", onEvent, isOptional = true, suffix = "bit")
                            FormField(formState.frecuenciaBase, "frecuenciaBase", "Frecuencia base", onEvent, isOptional = true, suffix = "GHz")
                            FormField(formState.frecuenciaBoost, "frecuenciaBoost", "Frecuencia boost", onEvent, suffix = "GHz")
                            FormField(formState.consumoWatts, "consumoWatts", "Consumo", onEvent, KeyboardType.Number, suffix = "W")
                            FormField(formState.fuenteRecomendada, "fuenteRecomendada", "Fuente recomendada ej: 850W", onEvent)
                            FormField(formState.conectoresEnergia, "conectoresEnergia", "Conectores de energía", onEvent, isOptional = true)
                            FormField(formState.versionPcie, "versionPcie", "Versión PCIe", onEvent, isOptional = true)
                        }
                        "Motherboard" -> {
                            FormField(formState.socket, "socket", "Socket ej: AM5, LGA1700", onEvent)
                            FormField(formState.chipsetMobo, "chipsetMobo", "Chipset ej: B650, Z790", onEvent)
                            FormField(formState.formato, "formato", "Formato ej: ATX, Micro-ATX", onEvent)
                            FormField(formState.compatibilidadCpu, "compatibilidadCpu", "Compatibilidad CPU", onEvent, isOptional = true)
                            FormField(formState.tipoRam, "tipoRam", "Tipo de RAM ej: DDR5", onEvent)
                            FormField(formState.velocidadRamMax, "velocidadRamMax", "Velocidad RAM máx", onEvent, isOptional = true, suffix = "MHz")
                            FormField(formState.slotsRam, "slotsRam", "Slots de RAM", onEvent, KeyboardType.Number)
                            FormField(formState.almacenamiento, "almacenamiento", "Almacenamiento M.2/SATA", onEvent, isOptional = true)
                            FormField(formState.puertos, "puertos", "Puertos", onEvent, isOptional = true)
                            FormField(formState.conectividad, "conectividad", "Conectividad WiFi/BT", onEvent, isOptional = true)
                        }
                        "RAM" -> {
                            FormField(formState.tipoRam2, "tipoRam2", "Tipo ej: DDR4, DDR5", onEvent)
                            FormField(formState.capacidadTotal, "capacidadTotal", "Capacidad ej: 32GB (2x16GB)", onEvent)
                            FormField(formState.configuracion, "configuracion", "Configuración ej: Dual Channel", onEvent, isOptional = true)
                            FormField(formState.velocidad, "velocidad", "Velocidad", onEvent, suffix = "MHz")
                            FormField(formState.latencia, "latencia", "Latencia ej: CL16", onEvent)
                            FormField(formState.voltaje, "voltaje", "Voltaje", onEvent, isOptional = true, suffix = "V")
                            FormField(formState.perfil, "perfil", "Perfil ej: XMP, EXPO", onEvent, isOptional = true)
                        }
                        "PSU" -> {
                            FormField(formState.potenciaWatts, "potenciaWatts", "Potencia", onEvent, KeyboardType.Number, suffix = "W")
                            FormField(formState.certificacion, "certificacion", "Certificación ej: 80 Plus Gold", onEvent)
                            FormField(formState.tipoModular, "tipoModular", "Modularidad ej: Totalmente modular", onEvent)
                            FormField(formState.eficiencia, "eficiencia", "Eficiencia", onEvent, isOptional = true)
                            FormField(formState.ventilador, "ventilador", "Ventilador ej: 135mm", onEvent, isOptional = true)
                            FormField(formState.protecciones, "protecciones", "Protecciones", onEvent, isOptional = true)
                            FormField(formState.conectores, "conectores", "Conectores", onEvent, isOptional = true)
                        }
                    }
                }
            }

            if (errorMessage != null) {
                item {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            onBack()
                            onEvent(AdminEvent.OnResetForm)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancelar")
                    }
                    Button(
                        onClick = { onSave() },
                        modifier = Modifier.weight(1f),
                        enabled = !isSaving && !isUploadingImage &&
                                formState.nombre.isNotBlank() &&
                                formState.marca.isNotBlank() &&
                                formState.precioUsd.isNotBlank()
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = Color.White
                            )
                        } else {
                            Text("Guardar")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FormField(
    value: String,
    field: String,
    label: String,
    onEvent: (AdminEvent) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    isOptional: Boolean = false,
    supportingText: String? = null,
    suffix: String? = null,
    prefix: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = { newValue ->
            if (suffix != null ||
                keyboardType == KeyboardType.Decimal ||
                keyboardType == KeyboardType.Number
            ) {
                val filtered = newValue.filter { it.isDigit() || it == '.' }
                onEvent(AdminEvent.OnFormFieldChange(field, filtered))
            } else {
                onEvent(AdminEvent.OnFormFieldChange(field, newValue))
            }
        },
        label = { Text(if (isOptional) "$label (opcional)" else label) },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(
            keyboardType = if (suffix != null) KeyboardType.Decimal else keyboardType
        ),
        supportingText = supportingText?.let { { Text(it) } },
        suffix = suffix?.let { { Text(it) } },
        prefix = prefix,
        singleLine = true
    )
}