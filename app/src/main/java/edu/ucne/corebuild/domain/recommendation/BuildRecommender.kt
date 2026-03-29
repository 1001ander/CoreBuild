package edu.ucne.corebuild.domain.recommendation

import edu.ucne.corebuild.domain.compatibility.CompatibilityEngine
import edu.ucne.corebuild.domain.model.Component
import javax.inject.Inject

class BuildRecommender @Inject constructor(
    private val compatibilityEngine: CompatibilityEngine
) {
    fun recommendBuild(
        budget: Double,
        priority: String?, // "CPU" or "GPU"
        allComponents: List<Component>
    ): List<Component> {
        if (allComponents.isEmpty()) return emptyList()

        val cpus = allComponents.filterIsInstance<Component.CPU>().sortedBy { it.price }
        val gpus = allComponents.filterIsInstance<Component.GPU>().sortedBy { it.price }
        val mobos = allComponents.filterIsInstance<Component.Motherboard>().sortedBy { it.price }
        val rams = allComponents.filterIsInstance<Component.RAM>().sortedBy { it.price }
        val psus = allComponents.filterIsInstance<Component.PSU>().sortedBy { it.price }

        if (cpus.isEmpty() || mobos.isEmpty() || rams.isEmpty() || psus.isEmpty()) return emptyList()


        var cheapestViableBuild: List<Component>? = null
        var minPrice = Double.MAX_VALUE

        for (cpu in cpus) {
            val compatibleMobo = mobos.find { isSocketCompatible(cpu.socket, it.socket) } ?: continue
            val compatibleRam = rams.find { isRamCompatible(compatibleMobo.ramType, it.type) } ?: continue
            val compatiblePsu = psus.firstOrNull() ?: continue 

            val total = cpu.price + compatibleMobo.price + compatibleRam.price + compatiblePsu.price
            if (total < minPrice) {
                minPrice = total
                cheapestViableBuild = listOf(cpu, compatibleMobo, compatibleRam, compatiblePsu)
            }
        }

        if (cheapestViableBuild == null || minPrice > budget) {
            return emptyList()
        }


        
        if (priority == "GPU" && gpus.isNotEmpty()) {
            for (gpu in gpus.reversed()) {
                val remainingForEssentials = budget - gpu.price
                if (remainingForEssentials < (minPrice - (gpus.firstOrNull()?.price ?: 0.0))) {

                    continue
                }
                val bestEssentials = findBestEssentialsForGpu(remainingForEssentials, cpus, mobos, rams, psus)
                if (bestEssentials.isNotEmpty()) {
                    return listOf(gpu) + bestEssentials
                }
            }
        } else {

            for (cpu in cpus.reversed()) {
                val remainingForOthers = budget - cpu.price
                if (remainingForOthers < (minPrice - cpus.first().price)) continue
                

                val gpuAllocation = if (priority == "CPU") 0.3 else 0.5
                val gpuBudget = remainingForOthers * gpuAllocation
                
                val compatibleGpus = gpus.filter { it.price <= gpuBudget }.sortedByDescending { it.price }
                
                for (gpu in (listOf(null) + compatibleGpus)) {
                    val finalBudget = remainingForOthers - (gpu?.price ?: 0.0)
                    val essentials = findBestEssentialsForCpu(finalBudget, cpu, mobos, rams, psus)
                    if (essentials.isNotEmpty()) {
                        val build = mutableListOf<Component>(cpu)
                        if (gpu != null) build.add(gpu)
                        build.addAll(essentials)
                        return build
                    }
                }
            }
        }

        return cheapestViableBuild
    }

    private fun findBestEssentialsForGpu(
        budget: Double,
        cpus: List<Component.CPU>,
        mobos: List<Component.Motherboard>,
        rams: List<Component.RAM>,
        psus: List<Component.PSU>
    ): List<Component> {

        for (cpu in cpus.reversed()) {
            val remaining = budget - cpu.price
            val essentials = findBestEssentialsForCpu(remaining, cpu, mobos, rams, psus)
            if (essentials.isNotEmpty()) return listOf(cpu) + essentials
        }
        return emptyList()
    }

    private fun findBestEssentialsForCpu(
        budget: Double,
        cpu: Component.CPU,
        mobos: List<Component.Motherboard>,
        rams: List<Component.RAM>,
        psus: List<Component.PSU>
    ): List<Component> {
        val compatibleMobos = mobos.filter { isSocketCompatible(cpu.socket, it.socket) }.sortedByDescending { it.price }
        for (mobo in compatibleMobos) {
            val remainingAfterMobo = budget - mobo.price
            if (remainingAfterMobo < 0) continue
            
            val compatibleRams = rams.filter { isRamCompatible(mobo.ramType, it.type) }.sortedByDescending { it.price }
            // Try to find a RAM that fits nicely but leave room for PSU
            val selectedRam = compatibleRams.find { it.price <= remainingAfterMobo * 0.6 } ?: compatibleRams.lastOrNull()
            
            if (selectedRam != null) {
                val psuBudget = remainingAfterMobo - selectedRam.price
                if (psuBudget < 0) continue
                
                val selectedPsu = psus.reversed().find { it.price <= psuBudget } ?: psus.firstOrNull()
                
                if (selectedPsu != null && selectedPsu.price <= psuBudget) {
                    return listOf(mobo, selectedRam, selectedPsu)
                }
            }
        }
        return emptyList()
    }

    private fun isSocketCompatible(cpuSocket: String, moboSocket: String): Boolean {
        val s1 = cpuSocket.replace(" ", "").uppercase()
        val s2 = moboSocket.replace(" ", "").uppercase()
        return s1 == s2 || s1.contains(s2) || s2.contains(s1)
    }

    private fun isRamCompatible(moboRam: String, ramType: String): Boolean {
        val r1 = moboRam.uppercase()
        val r2 = ramType.uppercase()
        return r1.contains(r2) || r2.contains(r1)
    }
}
