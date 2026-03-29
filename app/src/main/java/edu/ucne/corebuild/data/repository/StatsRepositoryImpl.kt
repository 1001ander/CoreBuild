package edu.ucne.corebuild.data.repository

import edu.ucne.corebuild.data.local.dao.ComponentDao
import edu.ucne.corebuild.data.local.dao.StatsDao
import edu.ucne.corebuild.data.local.entity.StatsEntity
import edu.ucne.corebuild.data.local.mapper.toDomain
import edu.ucne.corebuild.domain.model.Component
import edu.ucne.corebuild.domain.repository.StatsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class StatsRepositoryImpl @Inject constructor(
    private val statsDao: StatsDao,
    private val componentDao: ComponentDao
) : StatsRepository {

    override suspend fun recordView(componentId: Int) {
        val current = statsDao.getStatsForComponent(componentId) ?: StatsEntity(componentId)
        statsDao.insertOrUpdate(current.copy(
            views = current.views + 1,
            lastViewed = System.currentTimeMillis()
        ))
    }

    override suspend fun recordAddedToCart(componentId: Int) {
        val current = statsDao.getStatsForComponent(componentId) ?: StatsEntity(componentId)
        statsDao.insertOrUpdate(current.copy(addedToCart = current.addedToCart + 1))
    }

    override suspend fun recordPurchase(componentId: Int) {
        val current = statsDao.getStatsForComponent(componentId) ?: StatsEntity(componentId)
        statsDao.insertOrUpdate(current.copy(purchases = current.purchases + 1))
    }

    override fun getRecentlyViewed(): Flow<List<Component>> {
        return combine(
            statsDao.getRecentlyViewed(),
            componentDao.getComponents()
        ) { stats, components ->
            stats.mapNotNull { stat ->
                components.find { it.id == stat.componentId }?.toDomain()
            }
        }
    }

    override fun getTopRated(): Flow<List<Component>> {
        return combine(
            statsDao.getTopRated(),
            componentDao.getComponents()
        ) { stats, components ->
            stats.mapNotNull { stat ->
                components.find { it.id == stat.componentId }?.toDomain()
            }
        }
    }
}
