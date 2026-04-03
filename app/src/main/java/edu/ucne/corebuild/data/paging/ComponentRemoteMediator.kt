package edu.ucne.corebuild.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import edu.ucne.corebuild.data.local.dao.ComponentDao
import edu.ucne.corebuild.data.local.database.CoreBuildDatabase
import edu.ucne.corebuild.data.local.entity.ComponentEntity
import edu.ucne.corebuild.data.sync.SyncManager
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class ComponentRemoteMediator @Inject constructor(
    private val dao: ComponentDao,
    private val db: CoreBuildDatabase,
    private val syncManager: SyncManager
) : RemoteMediator<Int, ComponentEntity>() {

    override suspend fun initialize(): InitializeAction =
        InitializeAction.LAUNCH_INITIAL_REFRESH

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ComponentEntity>
    ): MediatorResult {
        if (loadType == LoadType.PREPEND || loadType == LoadType.APPEND) {
            return MediatorResult.Success(endOfPaginationReached = true)
        }

        return try {
            syncManager.syncAll()
            MediatorResult.Success(endOfPaginationReached = true)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}
