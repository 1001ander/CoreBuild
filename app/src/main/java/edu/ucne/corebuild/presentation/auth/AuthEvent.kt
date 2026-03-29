package edu.ucne.corebuild.presentation.auth

sealed class AuthEvent {
    data class OnLogin(val email: String, val pass: String) : AuthEvent()
    data class OnRegister(val name: String, val email: String, val pass: String) : AuthEvent()
    data class OnUpdateProfilePicture(val imageUrl: String) : AuthEvent()
    object OnLogout : AuthEvent()
    object DismissError : AuthEvent()
}
