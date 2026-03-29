package edu.ucne.corebuild.domain.model

data class Stats(
    val componentId: Int,
    val views: Int = 0,
    val addedToCart: Int = 0,
    val purchases: Int = 0,
    val lastViewed: Long = 0L
)
