package edu.ucne.corebuild.domain.buildscore

import edu.ucne.corebuild.domain.compatibility.CompatibilityEngine
import edu.ucne.corebuild.domain.model.CartItem
import edu.ucne.corebuild.domain.model.Component
import javax.inject.Inject

data class BuildScore(
    val score: Int,
    val label: String,
    val recommendations: List<String>
)

class BuildScoreCalculator @Inject constructor(
    private val compatibilityEngine: CompatibilityEngine
) {
    fun calculateScore(items: List<CartItem>): BuildScore {
        if (items.isEmpty()) {
            return BuildScore(0, "❌ Build Incompleto", listOf("Agrega componentes para evaluar tu build."))
        }

        val components = items.map { it.component }
        val cpu = components.filterIsInstance<Component.CPU>().firstOrNull()
        val gpu = components.filterIsInstance<Component.GPU>().firstOrNull()
        val ram = components.filterIsInstance<Component.RAM>().firstOrNull()
        val psu = components.filterIsInstance<Component.PSU>().firstOrNull()
        val mobo = components.filterIsInstance<Component.Motherboard>().firstOrNull()

        var baseScore = 0.0
        baseScore += calculateCpuScore(cpu)
        baseScore += calculateGpuScore(gpu)
        baseScore += calculateRamScore(ram)
        baseScore += calculatePsuScore(psu)
        baseScore += calculateMotherboardScore(mobo)

        val synergyBonus = calculateSynergyBonus(cpu, gpu, mobo, ram, psu, items)
        
        var totalScore = ((baseScore + synergyBonus) / 105.0) * 100.0
        val recs = mutableListOf<String>()

        val warnings = compatibilityEngine.checkCompatibility(items)
        if (warnings.isNotEmpty()) {
            totalScore -= 30
            recs.add("⚠️ Errores de compatibilidad detectados.")
        }

        totalScore -= calculateMissingPenalties(cpu, mobo, ram, psu, gpu, recs)
        totalScore -= calculateSpecificHardwarePenalties(cpu, gpu, psu, recs)

        val finalScore = totalScore.toInt().coerceIn(0, 100)
        val label = determineLabel(finalScore)

        if (recs.isEmpty()) recs.add("¡Excelente elección de componentes!")

        return BuildScore(finalScore, label, recs)
    }

    private fun calculateCpuScore(cpu: Component.CPU?): Int = cpu?.let {
        when {
            it.price > 350 -> 25
            it.price >= 200 -> 18
            it.price >= 100 -> 12
            else -> 5
        }
    } ?: 0

    private fun calculateGpuScore(gpu: Component.GPU?): Int = gpu?.let {
        when {
            it.price > 700 -> 25
            it.price >= 400 -> 18
            it.price >= 200 -> 12
            else -> 5
        }
    } ?: 0

    private fun calculateRamScore(ram: Component.RAM?): Int = ram?.let {
        val name = it.name.uppercase()
        var ramPts = when {
            name.contains("64GB") || name.contains("128GB") -> 15
            name.contains("32GB") -> 12
            name.contains("16GB") -> 8
            else -> 3
        }

        val speedRegex = Regex("""(\d{4,5})\s*MHZ|DDR[45]-(\d{4,5})""")
        val speedMatch = speedRegex.find(name)
        val speed = speedMatch?.groupValues
            ?.drop(1)
            ?.firstOrNull { group -> group.isNotEmpty() }
            ?.toIntOrNull() ?: 0

        if ((name.contains("DDR4") && speed > 3600) || (name.contains("DDR5") && speed > 5600)) {
            ramPts += 3
        }
        ramPts.coerceAtMost(15)
    } ?: 0

    private fun calculatePsuScore(psu: Component.PSU?): Int = psu?.let {
        val name = it.name.uppercase()
        when {
            name.contains("PLATINUM") || name.contains("TITANIUM") -> 10
            name.contains("GOLD") -> 7
            else -> 4
        }
    } ?: 0

    private fun calculateMotherboardScore(mobo: Component.Motherboard?): Int = mobo?.let {
        val name = it.name.uppercase()
        when {
            listOf("X570", "Z690", "Z790", "X670E", "Z890", "X870E", "X870").any { chipset -> name.contains(chipset) } -> 5
            listOf("B450", "B550", "B650", "B760").any { chipset -> name.contains(chipset) } -> 3
            else -> 2
        }
    } ?: 0

    private fun calculateSynergyBonus(
        cpu: Component.CPU?, gpu: Component.GPU?, mobo: Component.Motherboard?,
        ram: Component.RAM?, psu: Component.PSU?, items: List<CartItem>
    ): Double {
        var bonus = 0.0
        if (cpu != null && gpu != null && mobo != null && ram != null && psu != null) bonus += 10
        if (ram?.name?.contains("DDR5", true) == true) bonus += 5
        
        val warnings = compatibilityEngine.checkCompatibility(items)
        if (cpu != null && mobo != null && warnings.none { it.contains("Socket", true) }) bonus += 5

        if (cpu != null && gpu != null && psu != null) {
            val cpuWatts = Regex("""\d+""").find(cpu.tdp)?.value?.toIntOrNull() ?: 65
            val gpuWatts = Regex("""\d+""").find(gpu.consumptionWatts)?.value?.toIntOrNull() ?: 200
            if (psu.wattage >= (cpuWatts + gpuWatts) * 1.2) bonus += 5
        }
        return bonus
    }

    private fun calculateMissingPenalties(
        cpu: Component.CPU?, mobo: Component.Motherboard?, ram: Component.RAM?,
        psu: Component.PSU?, gpu: Component.GPU?, recs: MutableList<String>
    ): Double {
        var penalty = 0.0
        if (cpu == null) { penalty += 10; recs.add("Falta CPU (-10)") }
        if (mobo == null) { penalty += 10; recs.add("Falta Placa Base (-10)") }
        if (ram == null) { penalty += 10; recs.add("Falta RAM (-10)") }
        if (psu == null) { penalty += 10; recs.add("Falta Fuente de Poder (-10)") }
        if (gpu == null) { penalty += 5; recs.add("Falta GPU (-5)") }
        return penalty
    }

    private fun calculateSpecificHardwarePenalties(
        cpu: Component.CPU?, gpu: Component.GPU?, psu: Component.PSU?,
        recs: MutableList<String>
    ): Double {
        var penalty = 0.0
        if (gpu != null && psu != null) {
            val recPSU = Regex("""\d+""").find(gpu.recommendedPSU ?: gpu.consumptionWatts)?.value?.toIntOrNull() ?: 600
            if (psu.wattage < recPSU) {
                penalty += 15
                recs.add("⚠️ PSU insuficiente para la GPU: se sugieren ${recPSU}W.")
            }
        }
        if (cpu != null && gpu != null) {
            val ratio = gpu.price / cpu.price
            when {
                ratio > 4.0 -> { penalty += 10; recs.add("Cuello de botella severo: CPU muy débil para esta GPU.") }
                ratio < 1.0 -> { penalty += 10; recs.add("Cuello de botella severo: GPU muy débil para este CPU.") }
                ratio >= 3.5 -> { penalty += 5; recs.add("Desbalance moderado: El CPU podría limitar la GPU.") }
                ratio <= 1.2 -> { penalty += 5; recs.add("Desbalance moderado: La GPU limita el potencial del CPU.") }
            }
        }
        return penalty
    }

    private fun determineLabel(score: Int): String = when {
        score >= 90 -> "🏆 Build de Ensueño"
        score >= 75 -> "✅ Excelente"
        score >= 55 -> "⚖️ Balanceado"
        score >= 35 -> "⚠️ Mejorable"
        else -> "❌ Build Incompleto"
    }
}
