package edu.ucne.corebuild.domain.model

data class User(
    val id: Int? = null,
    val name: String,
    val email: String,
    val profilePicture: String? = null,
    val isLogged: Boolean = false
)
