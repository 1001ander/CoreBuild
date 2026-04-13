package edu.ucne.corebuild.domain.recommendation

import edu.ucne.corebuild.domain.compatibility.CompatibilityEngine
import edu.ucne.corebuild.domain.model.Component
import javax.inject.Inject
import kotlin.math.ceil

class BuildRecommender @Inject constructor(
    private val compatibilityEngine: CompatibilityEngine
) {
    fun recommendBuild(
        budget: Double,
        priority: String?,
        allComponents: List<Component>
    ): List<Component> {
        if (allComponents.isEmpty()) return emptyList()

        val components = groupComponents(allComponents)
        if (components == null) return emptyList()

        val cheapestViable = findCheapestViableBuild(components, budget)
        if (cheapestViable == null) return emptyList()

        val recommended = if (priority == "GPU") {
            recommendGpuPriority(budget, components)
        } else {
            recommendBalanced(budget, components, priority == "CPU")
        }

        val finalResult = recommended ?: cheapestViable
        return if (finalResult.sumOf { it.price } <= budget) finalResult else cheapestViable
    }

    private fun findCheapestViableBuild(c: ComponentGroup, budget: Double): List<Component>? {
        var absoluteMinPrice = Double.MAX_VALUE
        var build: List<Component>? = null

        for (cpu in c.cpus) {
            val mobo = c.mobos.find { isSocketCompatible(cpu.socket, it.socket) } ?: continue
            val ram = c.rams.find { isRamCompatible(mobo.ramType, it.type) } ?: continue
            val psu = c.psus.firstOrNull() ?: continue
            
            val total = cpu.price + mobo.price + ram.price + psu.price
            if (total < absoluteMinPrice) {
                absoluteMinPrice = total
                build = listOf(cpu, mobo, ram, psu)
            }
        }
        return if (absoluteMinPrice <= budget) build else null
    }

    private fun recommendGpuPriority(budget: Double, c: ComponentGroup): List<Component>? {
        for (gpu in c.gpus.reversed()) {
            val remaining = budget - gpu.price
            if (remaining < 0) continue
            
            for (cpu in c.cpus.reversed()) {
                val essentialsBudget = remaining - cpu.price
                val essentials = findBestEssentialsForCpu(essentialsBudget, cpu, c.mobos, c.rams, c.psus, gpu)
                if (essentials.isNotEmpty()) {
                    val build = listOf(gpu, cpu) + essentials
                    if (build.sumOf { it.price } <= budget) return build
                }
            }
        }
        return null
    }

    private fun recommendBalanced(budget: Double, c: ComponentGroup, cpuPriority: Boolean): List<Component>? {
        val gpuRatio = if (cpuPriority) 0.25 else 0.35
        val cpusToTry = c.cpus.filter { it.price <= budget * 0.5 }.sortedByDescending { it.price }
        
        for (cpu in cpusToTry) {
            val remainingForOthers = budget - cpu.price
            val compatibleGpus = c.gpus.filter { it.price <= remainingForOthers * 0.8 }.sortedByDescending { it.price }
            
            if (compatibleGpus.isNotEmpty()) {
                val targetGpuPrice = remainingForOthers * gpuRatio
                val selectedGpu = compatibleGpus.find { it.price <= targetGpuPrice } ?: compatibleGpus.last()
                
                val finalBudget = remainingForOthers - selectedGpu.price
                val essentials = findBestEssentialsForCpu(finalBudget, cpu, c.mobos, c.rams, c.psus, selectedGpu)
                
                if (essentials.isNotEmpty()) {
                    val build = listOf(cpu, selectedGpu) + essentials
                    if (build.sumOf { it.price } <= budget) return build
                }
            }
        }
        return null
    }

    private fun groupComponents(all: List<Component>): ComponentGroup? {
        val cpus = all.filterIsInstance<Component.CPU>().sortedBy { it.price }
        val gpus = all.filterIsInstance<Component.GPU>().sortedBy { it.price }
        val mobos = all.filterIsInstance<Component.Motherboard>().sortedBy { it.price }
        val rams = all.filterIsInstance<Component.RAM>().sortedBy { it.price }
        val psus = all.filterIsInstance<Component.PSU>().sortedBy { it.price }

        if (cpus.isEmpty() || mobos.isEmpty() || rams.isEmpty() || psus.isEmpty()) return null
        
        return ComponentGroup(cpus, gpus, mobos, rams, psus)
    }

    private fun findBestEssentialsForCpu(
        budget: Double, cpu: Component.CPU, mobos: List<Component.Motherboard>,
        rams: List<Component.RAM>, psus: List<Component.PSU>, gpu: Component.GPU?
    ): List<Component> {
        val minWatts = calculateRequiredWatts(cpu, gpu)
        val compatibleMobos = mobos.filter { 
            isSocketCompatible(cpu.socket, it.socket) && it.price <= budget * 0.5 
        }.sortedByDescending { it.price }
        
        for (mobo in compatibleMobos) {
            val remaining = budget - mobo.price
            if (remaining < 0) continue
            val selectedRam = rams.filter { isRamCompatible(mobo.ramType, it.type) && it.price <= remaining * 0.7 }
                .maxByOrNull { it.price } ?: rams.lastOrNull() ?: continue
            val psuBudget = remaining - selectedRam.price
            if (psuBudget < 0) continue
            val selectedPsu = psus.filter { it.wattage >= minWatts && it.price <= psuBudget }.maxByOrNull { it.price }
            if (selectedPsu != null) return listOf(mobo, selectedRam, selectedPsu)
        }
        return emptyList()
    }

    private fun calculateRequiredWatts(cpu: Component.CPU, gpu: Component.GPU?): Int {
        if (gpu == null) return 450
        val manufacturerRec = Regex("""\d+""").find(gpu.recommendedPSU ?: "")?.value?.toIntOrNull()
        if (manufacturerRec != null) return manufacturerRec
        
        val cpuWatts = Regex("""\d+""").find(cpu.tdp)?.value?.toIntOrNull() ?: 65
        val gpuCons = Regex("""\d+""").find(gpu.consumptionWatts)?.value?.toIntOrNull() ?: 200
        return (ceil((cpuWatts + gpuCons) * 1.2 / 50.0) * 50).toInt()
    }

    private fun isSocketCompatible(s1: String, s2: String) = s1.clean() == s2.clean() || s1.clean().contains(s2.clean()) || s2.clean().contains(s1.clean())
    private fun isRamCompatible(m: String, r: String) = m.uppercase().contains(r.uppercase()) || r.uppercase().contains(m.uppercase())
    private fun String.clean() = this.replace(" ", "").uppercase()

    private data class ComponentGroup(
        val cpus: List<Component.CPU>, val gpus: List<Component.GPU>,
        val mobos: List<Component.Motherboard>, val rams: List<Component.RAM>, val psus: List<Component.PSU>
    )
}
