package edu.ucne.corebuild.data.local.datasource

import edu.ucne.corebuild.domain.model.Component
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class ComponentLocalDataSource @Inject constructor() {
    private val components = listOf(
        // CPUS INTEL (IDs 1-24)
        Component.CPU(1, "Intel Core i3-10100F", "Procesador de entrada ideal para builds de bajo presupuesto. Sin gráficos integrados, requiere GPU dedicada.", 70.0, "Intel", "LGA1200", "10a Gen (Comet Lake)", 4, 8, "3.6 GHz", "4.3 GHz", "65W"),
        Component.CPU(2, "Intel Core i5-10400F", "CPU de gama media-baja con 6 núcleos y 12 hilos. Sin iGPU, ideal para builds gaming de presupuesto ajustado.", 90.0, "Intel", "LGA1200", "10a Gen (Comet Lake)", 6, 12, "2.9 GHz", "4.3 GHz", "65W"),
        Component.CPU(3, "Intel Core i7-10700K", "Procesador desbloqueado de alto rendimiento con 8 núcleos y capacidad de overclocking. Incluye iGPU UHD 630.", 180.0, "Intel", "LGA1200", "10a Gen (Comet Lake)", 8, 16, "3.8 GHz", "5.1 GHz", "125W"),
        Component.CPU(4, "Intel Core i9-10900K", "Flagship de 10a generación con 10 núcleos y frecuencias turbo de hasta 5.3 GHz. Orientado a gaming extremo.", 220.0, "Intel", "LGA1200", "10a Gen (Comet Lake)", 10, 20, "3.7 GHz", "5.3 GHz", "125W"),
        Component.CPU(5, "Intel Core i5-11400F", "Sucesor del i5-10400F con mejoras de IPC y soporte para RAM más rápida. Sin iGPU.", 110.0, "Intel", "LGA1200", "11a Gen (Rocket Lake)", 6, 12, "2.6 GHz", "4.4 GHz", "65W"),
        Component.CPU(6, "Intel Core i7-11700K", "CPU desbloqueada de 11a Gen con 8 núcleos y mejoras de IPC. Soporte PCIe 4.0. Incluye iGPU UHD 750.", 200.0, "Intel", "LGA1200", "11a Gen (Rocket Lake)", 8, 16, "3.6 GHz", "5.0 GHz", "125W"),
        Component.CPU(7, "Intel Core i3-12100F", "Procesador de entrada de 12a generación con arquitectura híbrida. Sin gráficos integrados.", 95.0, "Intel", "LGA1700", "12a Gen (Alder Lake)", 4, 8, "3.3 GHz", "4.3 GHz", "58W"),
        Component.CPU(8, "Intel Core i5-12400F", "Uno de los procesadores gaming de mejor relación calidad-precio. Soporte DDR4 y DDR5. Sin iGPU.", 150.0, "Intel", "LGA1700", "12a Gen (Alder Lake)", 6, 12, "2.5 GHz", "4.4 GHz", "65W"),
        Component.CPU(9, "Intel Core i5-12600KF", "CPU desbloqueada con 6 núcleos P y 4 núcleos E. Excelente rendimiento gaming y multitarea.", 190.0, "Intel", "LGA1700", "12a Gen (Alder Lake)", 10, 16, "3.7 GHz", "4.9 GHz", "125W"),
        Component.CPU(10, "Intel Core i7-12700K", "Procesador de alto rendimiento con 8 núcleos P + 4 núcleos E. Ideal para gaming y streaming.", 260.0, "Intel", "LGA1700", "12a Gen (Alder Lake)", 12, 20, "3.6 GHz", "5.0 GHz", "125W"),
        Component.CPU(11, "Intel Core i9-12900K", "Flagship de 12a generación con 16 núcleos (8P+8E). Máximo rendimiento para creación de contenido.", 320.0, "Intel", "LGA1700", "12a Gen (Alder Lake)", 16, 24, "3.2 GHz", "5.2 GHz", "125W"),
        Component.CPU(12, "Intel Core i3-13100F", "Procesador de gama de entrada de 13a generación. Sin iGPU, pensado exclusivamente para gaming.", 110.0, "Intel", "LGA1700", "13a Gen (Raptor Lake)", 4, 8, "3.4 GHz", "4.5 GHz", "58W"),
        Component.CPU(13, "Intel Core i5-13400F", "CPU gaming de gama media. 10 núcleos (6P+4E), sin iGPU, TDP contenido. Ideal para gaming 1440p.", 200.0, "Intel", "LGA1700", "13a Gen (Raptor Lake)", 10, 16, "2.5 GHz", "4.6 GHz", "65W"),
        Component.CPU(14, "Intel Core i5-13600K", "CPU desbloqueado de 13a Gen con 14 núcleos. Destaca en gaming y productividad.", 300.0, "Intel", "LGA1700", "13a Gen (Raptor Lake)", 14, 20, "3.5 GHz", "5.1 GHz", "125W"),
        Component.CPU(15, "Intel Core i7-13700K", "Procesador de alto rendimiento con 16 núcleos (8P+8E). Ideal para multitarea exigente.", 380.0, "Intel", "LGA1700", "13a Gen (Raptor Lake)", 16, 24, "3.4 GHz", "5.4 GHz", "125W"),
        Component.CPU(16, "Intel Core i9-13900K", "El CPU más potente de Raptor Lake con 24 núcleos. Máximo rendimiento absoluto en renderizado 3D.", 500.0, "Intel", "LGA1700", "13a Gen (Raptor Lake)", 24, 32, "3.0 GHz", "5.8 GHz", "125W"),
        Component.CPU(17, "Intel Core i3-14100", "CPU de entrada de 14a generación con iGPU integrada. Adecuado para PCs de oficina y tareas básicas.", 120.0, "Intel", "LGA1700", "14a Gen (Raptor Lake Refresh)", 4, 8, "3.5 GHz", "4.7 GHz", "60W"),
        Component.CPU(18, "Intel Core i5-14400F", "Refresh del i5-13400F con ligeras mejoras de frecuencia. Sin iGPU. Opción sólida para gaming.", 210.0, "Intel", "LGA1700", "14a Gen (Raptor Lake Refresh)", 10, 16, "2.5 GHz", "4.7 GHz", "65W"),
        Component.CPU(19, "Intel Core i5-14600K", "CPU desbloqueado de 14a Gen con 14 núcleos. Mejoras de frecuencia turbo respecto al 13600K.", 320.0, "Intel", "LGA1700", "14a Gen (Raptor Lake Refresh)", 14, 20, "3.5 GHz", "5.3 GHz", "125W"),
        Component.CPU(20, "Intel Core i7-14700K", "Versión refresh con 20 núcleos (8P+12E). Turbo de 5.6 GHz. Ideal para multitarea de alto nivel.", 420.0, "Intel", "LGA1700", "14a Gen (Raptor Lake Refresh)", 20, 28, "3.4 GHz", "5.6 GHz", "125W"),
        Component.CPU(21, "Intel Core i9-14900K", "Flagship de 14a generación con turbo de 6.0 GHz. Máximo desempeño en gaming y workstations.", 580.0, "Intel", "LGA1700", "14a Gen (Raptor Lake Refresh)", 24, 32, "3.2 GHz", "6.0 GHz", "125W"),
        Component.CPU(22, "Intel Core Ultra 5 245K", "Primera generación Arrow Lake. Optimizado para eficiencia energética y rendimiento por núcleo.", 350.0, "Intel", "LGA1851", "Arrow Lake (Core Ultra 200)", 14, 14, "4.2 GHz", "5.2 GHz", "125W"),
        Component.CPU(23, "Intel Core Ultra 7 265K", "CPU de gama alta Arrow Lake con 20 núcleos. Diseñado para cargas de trabajo con IA y gaming avanzado.", 450.0, "Intel", "LGA1851", "Arrow Lake (Core Ultra 200)", 20, 20, "3.9 GHz", "5.5 GHz", "125W"),
        Component.CPU(24, "Intel Core Ultra 9 285K", "El flagship de Arrow Lake. 24 núcleos sin HT, orientado a workstations y gaming extremo.", 620.0, "Intel", "LGA1851", "Arrow Lake (Core Ultra 200)", 24, 24, "3.7 GHz", "5.7 GHz", "125W"),

        // CPUS AMD (IDs 25-48)
        Component.CPU(25, "AMD Ryzen 5 3600", "Clásico de la plataforma AM4 con arquitectura Zen 2. Excelente para gaming y multitarea.", 90.0, "AMD", "AM4", "Zen 2", 6, 12, "3.6 GHz", "4.2 GHz", "65W"),
        Component.CPU(26, "AMD Ryzen 7 3700X", "CPU de 8 núcleos Zen 2 con excelente eficiencia. Ideal para streaming y edición.", 120.0, "AMD", "AM4", "Zen 2", 8, 16, "3.6 GHz", "4.4 GHz", "65W"),
        Component.CPU(27, "AMD Ryzen 9 3900X", "Workstation de 12 núcleos en plataforma AM4. Enorme caché L3 de 64MB. Ideal para renderizado.", 180.0, "AMD", "AM4", "Zen 2", 12, 24, "3.8 GHz", "4.6 GHz", "105W"),
        Component.CPU(28, "AMD Ryzen 3 4100", "Procesador de entrada AM4 para presupuestos muy ajustados. Suficiente para ofimática.", 60.0, "AMD", "AM4", "Zen 2", 4, 8, "3.8 GHz", "4.0 GHz", "65W"),
        Component.CPU(29, "AMD Ryzen 5 4500", "CPU de 6 núcleos de bajo costo en plataforma AM4. Buena opción de gama baja para gaming.", 75.0, "AMD", "AM4", "Zen 2", 6, 12, "3.6 GHz", "4.1 GHz", "65W"),
        Component.CPU(30, "AMD Ryzen 5 5500", "Versión accesible de Zen 3 para AM4. IPC mejorado respecto a Zen 2. Buen rendimiento gaming.", 90.0, "AMD", "AM4", "Zen 3", 6, 12, "3.6 GHz", "4.2 GHz", "65W"),
        Component.CPU(31, "AMD Ryzen 5 5600", "Procesador gaming de mejor relación calidad-precio en AM4. Arquitectura Zen 3 con gran IPC.", 120.0, "AMD", "AM4", "Zen 3", 6, 12, "3.5 GHz", "4.4 GHz", "65W"),
        Component.CPU(32, "AMD Ryzen 5 5600X", "Versión X del Ryzen 5 5600 con frecuencias superiores. Excelente rendimiento gaming 1080p.", 140.0, "AMD", "AM4", "Zen 3", 6, 12, "3.7 GHz", "4.6 GHz", "65W"),
        Component.CPU(33, "AMD Ryzen 5 5600X3D", "Ryzen 5 orientado al gaming con tecnología 3D V-Cache. Rendimiento gaming superior.", 200.0, "AMD", "AM4", "Zen 3 + 3D V-Cache", 6, 12, "3.3 GHz", "4.4 GHz", "65W"),
        Component.CPU(34, "AMD Ryzen 7 5700X", "CPU de 8 núcleos Zen 3 con TDP eficiente de 65W. Excelente para gaming y multitarea.", 170.0, "AMD", "AM4", "Zen 3", 8, 16, "3.4 GHz", "4.6 GHz", "65W"),
        Component.CPU(35, "AMD Ryzen 7 5800X3D", "El mejor CPU gaming de plataforma AM4 gracias a su enorme caché 3D V-Cache de 96MB.", 320.0, "AMD", "AM4", "Zen 3 + 3D V-Cache", 8, 16, "3.4 GHz", "4.5 GHz", "105W"),
        Component.CPU(36, "AMD Ryzen 9 5900X", "Workstation de 12 núcleos Zen 3 para plataforma AM4. Potente para rendering y edición.", 300.0, "AMD", "AM4", "Zen 3", 12, 24, "3.7 GHz", "4.8 GHz", "105W"),
        Component.CPU(37, "AMD Ryzen 9 5950X", "Flagship AM4 con 16 núcleos Zen 3. Máxima capacidad multicore en plataforma AM4.", 380.0, "AMD", "AM4", "Zen 3", 16, 32, "3.4 GHz", "4.9 GHz", "105W"),
        Component.CPU(38, "AMD Ryzen 5 7500F", "CPU Zen 4 de entrada para plataforma AM5 sin iGPU. Excelente opción para gaming.", 180.0, "AMD", "AM5", "Zen 4", 6, 12, "3.7 GHz", "5.0 GHz", "65W"),
        Component.CPU(39, "AMD Ryzen 5 7600", "Zen 4 de gama media con iGPU integrada Radeon 760M. Gran IPC y eficiencia energética.", 220.0, "AMD", "AM5", "Zen 4", 6, 12, "3.8 GHz", "5.1 GHz", "65W"),
        Component.CPU(40, "AMD Ryzen 5 7600X", "Versión X del 7600 con frecuencias más altas. Plataforma AM5 lista para el futuro.", 250.0, "AMD", "AM5", "Zen 4", 6, 12, "4.7 GHz", "5.3 GHz", "105W"),
        Component.CPU(41, "AMD Ryzen 7 7700X", "CPU de 8 núcleos Zen 4 de alto rendimiento. Excelentes frecuencias para gaming.", 330.0, "AMD", "AM5", "Zen 4", 8, 16, "4.5 GHz", "5.4 GHz", "105W"),
        Component.CPU(42, "AMD Ryzen 7 7800X3D", "El CPU gaming más popular de AM5 gracias a los 96MB de 3D V-Cache. Rendimiento excepcional.", 420.0, "AMD", "AM5", "Zen 4 + 3D V-Cache", 8, 16, "4.5 GHz", "5.0 GHz", "120W"),
        Component.CPU(43, "AMD Ryzen 9 7900X", "CPU de 12 núcleos Zen 4 para workstations y creadores. Altísimas frecuencias.", 420.0, "AMD", "AM5", "Zen 4", 12, 24, "4.7 GHz", "5.6 GHz", "170W"),
        Component.CPU(44, "AMD Ryzen 9 7950X3D", "El CPU más potente de AMD para AM5. 16 núcleos con 3D V-Cache de 128MB. Workstation y Gaming.", 650.0, "AMD", "AM5", "Zen 4 + 3D V-Cache", 16, 32, "4.2 GHz", "5.7 GHz", "120W"),
        Component.CPU(45, "AMD Ryzen 5 9600X", "Primera generación Zen 5 de gama media. IPC notablemente mejorado. TDP eficiente.", 300.0, "AMD", "AM5", "Zen 5", 6, 12, "3.9 GHz", "5.4 GHz", "65W"),
        Component.CPU(46, "AMD Ryzen 7 9700X", "CPU Zen 5 de 8 núcleos con TDP de solo 65W. Excelente eficiencia energética.", 400.0, "AMD", "AM5", "Zen 5", 8, 16, "3.8 GHz", "5.5 GHz", "65W"),
        Component.CPU(47, "AMD Ryzen 7 9800X3D", "El mejor CPU gaming del mercado. Zen 5 con 3D V-Cache de 96MB. Rey del gaming AM5.", 520.0, "AMD", "AM5", "Zen 5 + 3D V-Cache", 8, 16, "4.7 GHz", "5.2 GHz", "120W"),
        Component.CPU(48, "AMD Ryzen 9 9950X", "Flagship absoluto de AMD con arquitectura Zen 5 y 16 núcleos. Máximo rendimiento.", 700.0, "AMD", "AM5", "Zen 5", 16, 32, "4.3 GHz", "5.7 GHz", "170W"),

        // GPUS NVIDIA (IDs 49-63)
        Component.GPU(49, "NVIDIA GeForce GTX 1660 Super", "GPU de gama media-baja excelente para 1080p en calidad media-alta.", 180.0, "NVIDIA", "TU116", "6GB", "GDDR6", "450W"),
        Component.GPU(50, "NVIDIA GeForce RTX 2060", "Primera GPU RTX con soporte de ray tracing y DLSS. Rendimiento sólido en 1080p.", 220.0, "NVIDIA", "TU106", "6GB", "GDDR6", "500W"),
        Component.GPU(51, "NVIDIA GeForce RTX 3060", "GPU de gama media con generosa VRAM de 12GB. Excelente para 1080p.", 290.0, "NVIDIA", "GA106", "12GB", "GDDR6", "550W"),
        Component.GPU(52, "NVIDIA GeForce RTX 3060 Ti", "GPU de alto rendimiento con bus de 256-bit. Ideal para 1440p gaming.", 340.0, "NVIDIA", "GA104", "8GB", "GDDR6", "600W"),
        Component.GPU(53, "NVIDIA GeForce RTX 3070", "GPU de gama alta-media Ampere. Excelente para 1440p y capaz en 4K.", 380.0, "NVIDIA", "GA104", "8GB", "GDDR6", "650W"),
        Component.GPU(54, "NVIDIA GeForce RTX 3080", "GPU de gama alta Ampere con GDDR6X. Excelente para 4K gaming.", 480.0, "NVIDIA", "GA102", "10GB", "GDDR6X", "750W"),
        Component.GPU(55, "NVIDIA GeForce RTX 4060", "GPU Ada Lovelace con consumo eficiente. Soporte DLSS 3 con Frame Generation.", 320.0, "NVIDIA", "AD107", "8GB", "GDDR6", "550W"),
        Component.GPU(56, "NVIDIA GeForce RTX 4060 Ti", "Arquitectura Ada Lovelace con altísimas frecuencias. Excelente para 1080p/1440p.", 400.0, "NVIDIA", "AD106", "8GB", "GDDR6", "600W"),
        Component.GPU(57, "NVIDIA GeForce RTX 4070 Super", "Excelente relación rendimiento/precio. Ada Lovelace con GDDR6X y 12GB.", 620.0, "NVIDIA", "AD104", "12GB", "GDDR6X", "700W"),
        Component.GPU(58, "NVIDIA GeForce RTX 4080 Super", "GPU de alto rendimiento con 16GB GDDR6X. Excelente para 4K gaming ultra.", 1000.0, "NVIDIA", "AD103", "16GB", "GDDR6X", "800W"),
        Component.GPU(59, "NVIDIA GeForce RTX 4090", "La GPU de consumo más poderosa. 24GB GDDR6X. Domina en 4K, 8K y AI.", 1800.0, "NVIDIA", "AD102", "24GB", "GDDR6X", "850W"),
        Component.GPU(60, "NVIDIA GeForce RTX 5060 Ti", "Primera generación Blackwell con GDDR7 y PCIe 5.0. 16GB de VRAM.", 500.0, "NVIDIA", "GB206", "16GB", "GDDR7", "600W"),
        Component.GPU(61, "NVIDIA GeForce RTX 5070", "GPU Blackwell de gama alta-media con GDDR7. Rendimiento comparable a RTX 4090.", 700.0, "NVIDIA", "GB205", "12GB", "GDDR7", "700W"),
        Component.GPU(62, "NVIDIA GeForce RTX 5070 Ti", "GPU Blackwell de alto rendimiento con 16GB GDDR7. Potencia 4K nativo.", 900.0, "NVIDIA", "GB203", "16GB", "GDDR7", "800W"),
        Component.GPU(63, "NVIDIA GeForce RTX 5090", "El GPU de consumo más potente jamás lanzado. 32GB GDDR7 con bus de 512-bit.", 2200.0, "NVIDIA", "GB202", "32GB", "GDDR7", "1000W"),

        // GPUS AMD (IDs 64-76)
        Component.GPU(64, "AMD Radeon RX 580", "GPU clásica de gama baja-media. Aún capaz para 1080p en calidad media.", 90.0, "AMD", "Polaris 20", "8GB", "GDDR5", "500W"),
        Component.GPU(65, "AMD Radeon RX 6600", "GPU RDNA 2 eficiente para 1080p gaming. Bajo consumo con buen rendimiento.", 210.0, "AMD", "Navi 23", "8GB", "GDDR6", "550W"),
        Component.GPU(66, "AMD Radeon RX 6650 XT", "Versión mejorada del RX 6600 XT. Excelente para 1080p gaming alta.", 250.0, "AMD", "Navi 23", "8GB", "GDDR6", "600W"),
        Component.GPU(67, "AMD Radeon RX 6700 XT", "GPU RDNA 2 de gama media-alta con 12GB de VRAM. Excelente para 1440p.", 330.0, "AMD", "Navi 22", "12GB", "GDDR6", "650W"),
        Component.GPU(68, "AMD Radeon RX 6800", "GPU de gama alta RDNA 2 con 16GB GDDR6. Sobresaliente en 1440p.", 420.0, "AMD", "Navi 21", "16GB", "GDDR6", "700W"),
        Component.GPU(69, "AMD Radeon RX 6800 XT", "Flagship de gama alta RDNA 2 con 16GB GDDR6. Excelente para 4K gaming.", 500.0, "AMD", "Navi 21", "16GB", "GDDR6", "750W"),
        Component.GPU(70, "AMD Radeon RX 7600", "GPU RDNA 3 de entrada con buen rendimiento en 1080p. Soporte AV1.", 270.0, "AMD", "Navi 33", "8GB", "GDDR6", "550W"),
        Component.GPU(71, "AMD Radeon RX 7700 XT", "GPU RDNA 3 con diseño chiplet. 12GB GDDR6 para gaming 1440p.", 420.0, "AMD", "Navi 32", "12GB", "GDDR6", "700W"),
        Component.GPU(72, "AMD Radeon RX 7800 XT", "GPU RDNA 3 de gama alta con 16GB GDDR6. Gran capacidad 1440p/4K.", 520.0, "AMD", "Navi 32", "16GB", "GDDR6", "700W"),
        Component.GPU(73, "AMD Radeon RX 7900 XT", "GPU de gama alta RDNA 3 con 20GB de VRAM. Excelente para 4K gaming.", 720.0, "AMD", "Navi 31", "20GB", "GDDR6", "800W"),
        Component.GPU(74, "AMD Radeon RX 7900 XTX", "Flagship RDNA 3 con 24GB GDDR6. La GPU más potente de Radeon.", 950.0, "AMD", "Navi 31", "24GB", "GDDR6", "850W"),
        Component.GPU(75, "AMD Radeon RX 9070", "Nueva generación RDNA 4 con PCIe 5.0 y mejoras en ray tracing. 16GB GDDR6.", 650.0, "AMD", "Navi 48", "16GB", "GDDR6", "700W"),
        Component.GPU(76, "AMD Radeon RX 9070 XT", "El flagship de RDNA 4. Gran salto en rendimiento ray tracing.", 800.0, "AMD", "Navi 48", "16GB", "GDDR6", "800W"),

        // MOTHERBOARDS INTEL (IDs 77-83)
        Component.Motherboard(77, "ASUS Prime H510M", "Placa económica para builds de presupuesto. Chipset H510.", 80.0, "ASUS", "LGA1200", "H510", "Micro-ATX", "DDR4"),
        Component.Motherboard(78, "MSI MAG B560 Tomahawk", "Placa ATX sólida para gama media con chipset B560. VRM robusto.", 150.0, "MSI", "LGA1200", "B560", "ATX", "DDR4"),
        Component.Motherboard(79, "Gigabyte H610M S2H", "Placa económica LGA1700 para Alder/Raptor Lake. Buena de entrada.", 95.0, "Gigabyte", "LGA1700", "H610", "Micro-ATX", "DDR4"),
        Component.Motherboard(80, "MSI PRO B760-P WiFi", "Placa versátil B760. Soporta DDR4/DDR5. WiFi 6E integrado.", 180.0, "MSI", "LGA1700", "B760", "ATX", "DDR4/DDR5"),
        Component.Motherboard(81, "ASUS ROG Strix Z790-E", "Placa Z790 de gama entusiasta. 18+1 VRM para overclocking.", 420.0, "ASUS", "LGA1700", "Z790", "ATX", "DDR5"),
        Component.Motherboard(82, "ASRock Z890 Taichi", "Placa flagship Z890 para Arrow Lake. WiFi 7 y doble LAN.", 500.0, "ASRock", "LGA1851", "Z890", "ATX", "DDR5"),
        Component.Motherboard(83, "Gigabyte Z890 Aorus Elite", "Placa Z890 de gama media-alta. 4 ranuras M.2 con PCIe 5.0.", 320.0, "Gigabyte", "LGA1851", "Z890", "ATX", "DDR5"),

        // MOTHERBOARDS AMD (IDs 84-91)
        Component.Motherboard(84, "Gigabyte A320M-S2H", "Placa económica AM4 para presupuesto muy ajustado. Chipset A320.", 65.0, "Gigabyte", "AM4", "A320", "Micro-ATX", "DDR4"),
        Component.Motherboard(85, "ASUS Prime B450M-A", "Placa Micro-ATX versátil B450. Soporta Ryzen 1a a 5a Gen.", 90.0, "ASUS", "AM4", "B450", "Micro-ATX", "DDR4"),
        Component.Motherboard(86, "MSI B550 Tomahawk", "Una de las placas B550 más populares. Excelente VRM.", 170.0, "MSI", "AM4", "B550", "ATX", "DDR4"),
        Component.Motherboard(87, "ASRock X570 Steel Legend", "Placa X570 con 3 ranuras M.2 y WiFi 6 integrado.", 220.0, "ASRock", "AM4", "X570", "ATX", "DDR4"),
        Component.Motherboard(88, "ASUS TUF A620M", "Placa de entrada AM5. Permite EXPO para RAM DDR5.", 130.0, "ASUS", "AM5", "A620", "Micro-ATX", "DDR5"),
        Component.Motherboard(89, "Gigabyte B650 Gaming X AX", "Placa ATX B650 equilibrada con WiFi 6E. Soporte EXPO.", 220.0, "Gigabyte", "AM5", "B650", "ATX", "DDR5"),
        Component.Motherboard(90, "MSI MPG X670E Carbon", "Placa X670E con PCIe 5.0 completo y USB4 integrado.", 420.0, "MSI", "AM5", "X670E", "ATX", "DDR5"),
        Component.Motherboard(91, "ASRock X870E Nova WiFi", "Placa X870E de última generación. WiFi 7 y Thunderbolt 4.", 500.0, "ASRock", "AM5", "X870E", "ATX", "DDR5"),

        // RAM (IDs 92-105)
        Component.RAM(92, "Corsair Vengeance LPX DDR4-3200 2x8GB", "Memoria confiable con disipador de perfil bajo.", 70.0, "Corsair", "DDR4", "16GB (2x8GB)", "3200 MHz", "CL16"),
        Component.RAM(93, "Corsair Vengeance LPX DDR4-3200 2x16GB", "Kit de 32GB ideal para edición de video y multitarea.", 90.0, "Corsair", "DDR4", "32GB (2x16GB)", "3200 MHz", "CL16"),
        Component.RAM(94, "Corsair Vengeance LPX DDR4-3200 4x8GB", "Kit de 4 módulos para maximizar el ancho de banda.", 100.0, "Corsair", "DDR4", "32GB (4x8GB)", "3200 MHz", "CL16"),
        Component.RAM(95, "G.Skill Ripjaws V DDR4-3600 2x8GB", "Memoria de alto rendimiento. Punto dulce para Ryzen.", 80.0, "G.Skill", "DDR4", "16GB (2x8GB)", "3600 MHz", "CL18"),
        Component.RAM(96, "G.Skill Ripjaws V DDR4-3600 2x16GB", "Capacidad y velocidad ideal para sistemas Ryzen gama alta.", 100.0, "G.Skill", "DDR4", "32GB (2x16GB)", "3600 MHz", "CL18"),
        Component.RAM(97, "Corsair Vengeance RGB Pro DDR4-3600 2x8GB", "Memoria con iluminación RGB individual por LED.", 95.0, "Corsair", "DDR4", "16GB (2x8GB)", "3600 MHz", "CL18"),
        Component.RAM(98, "Corsair Vengeance RGB Pro DDR4-3600 2x16GB", "Kit RGB de 32GB para builds gaming de gama alta.", 125.0, "Corsair", "DDR4", "32GB (2x16GB)", "3600 MHz", "CL18"),
        Component.RAM(99, "Kingston Fury Beast DDR5-5600 2x16GB", "Memoria DDR5 de entrada con soporte para plataformas modernas.", 120.0, "Kingston", "DDR5", "32GB (2x16GB)", "5600 MHz", "CL36"),
        Component.RAM(100, "Kingston Fury Beast DDR5-5600 2x32GB", "Kit de 64GB DDR5 para workstations and edición de video 4K.", 220.0, "Kingston", "DDR5", "64GB (2x32GB)", "5600 MHz", "CL36"),
        Component.RAM(101, "G.Skill Trident Z5 RGB DDR5-6000 2x16GB", "RAM DDR5 premium con latencia CL30 excepcionalmente baja.", 180.0, "G.Skill", "DDR5", "32GB (2x16GB)", "6000 MHz", "CL30"),
        Component.RAM(102, "G.Skill Trident Z5 RGB DDR5-6000 2x32GB", "Kit de 64GB Trident Z5 para workstations de alto nivel.", 340.0, "G.Skill", "DDR5", "64GB (2x32GB)", "6000 MHz", "CL30"),
        Component.RAM(103, "Corsair Dominator Platinum RGB DDR5-6200 2x16GB", "RAM entusiasta con diseño premium and LEDs CAPELLIX.", 220.0, "Corsair", "DDR5", "32GB (2x16GB)", "6200 MHz", "CL32"),
        Component.RAM(104, "Corsair Dominator Platinum RGB DDR5-6200 2x32GB", "Kit de 64GB Dominator para workstations entusiastas.", 420.0, "Corsair", "DDR5", "64GB (2x32GB)", "6200 MHz", "CL32"),
        Component.RAM(105, "Corsair Dominator Platinum RGB DDR5-6200 2x64GB", "128GB de RAM DDR5 para nivel profesional extremo.", 750.0, "Corsair", "DDR5", "128GB (2x64GB)", "6200 MHz", "CL32"),

        // FUENTES PSU (IDs 106-115)
        Component.PSU(106, "EVGA 500 W3", "Fuente de entrada 80 Plus Bronze para builds de presupuesto.", 55.0, "EVGA", 500, "80 Plus Bronze", "No modular"),
        Component.PSU(107, "Cooler Master MWE Gold 550", "Fuente Gold con mayor eficiencia y menor calor. Silenciosa.", 85.0, "Cooler Master", 550, "80 Plus Gold", "No modular"),
        Component.PSU(108, "Corsair CX650M", "Fuente semi-modular popular. Simplifica cable management.", 95.0, "Corsair", 650, "80 Plus Bronze", "Semi-modular"),
        Component.PSU(109, "MSI MPG A650GF", "Fuente Gold totalmente modular. Ventilador de 135mm silencioso.", 110.0, "MSI", 650, "80 Plus Gold", "Totalmente modular"),
        Component.PSU(110, "Seasonic Focus GX-750", "Una de las mejores fuentes Gold. Calidad excepcional.", 140.0, "Seasonic", 750, "80 Plus Gold", "Totalmente modular"),
        Component.PSU(111, "Corsair RM750e", "Referente en el mercado. Modo cero RPM bajo carga baja.", 150.0, "Corsair", 750, "80 Plus Gold", "Totalmente modular"),
        Component.PSU(112, "EVGA SuperNova 850 G6", "Ideal para sistemas con GPU de alto consumo. 10 años garantía.", 170.0, "EVGA", 850, "80 Plus Gold", "Totalmente modular"),
        Component.PSU(113, "ASUS ROG Thor 850P", "Fuente Platinum con pantalla OLED para monitoreo en tiempo real.", 250.0, "ASUS", 850, "80 Plus Platinum", "Totalmente modular"),
        Component.PSU(114, "be quiet! Dark Power 13", "La fuente silenciosa por excelencia con eficiencia Titanium.", 320.0, "be quiet!", 1000, "80 Plus Titanium", "Totalmente modular"),
        Component.PSU(115, "Corsair HX1200i", "Fuente de 1200W con monitoreo digital via iCUE. Para sistemas extremos.", 350.0, "Corsair", 1200, "80 Plus Platinum", "Totalmente modular")
    )

    fun getComponents(): Flow<List<Component>> = flowOf(components)

    fun getComponentById(id: Int): Flow<Component?> = flowOf(components.find { it.id == id })
}
