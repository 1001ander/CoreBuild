package edu.ucne.corebuild.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "component_stats")
data class StatsEntity(
    @PrimaryKey
    val componentId: Int,
    val views: Int = 0,
    val addedToCart: Int = 0,
    val purchases: Int = 0,
    val lastViewed: Long = 0L
)
