package edu.ucne.corebuild.data.local.mapper

import edu.ucne.corebuild.data.local.entity.UserEntity
import edu.ucne.corebuild.data.remote.dto.UserDto
import edu.ucne.corebuild.domain.model.User

fun UserEntity.toUser(): User {
    return User(
        id = id,
        name = name,
        email = email,
        profilePicture = profilePicture,
        isLogged = isLogged
    )
}

fun User.toEntity(password: String = ""): UserEntity {
    return UserEntity(
        id = id,
        name = name,
        email = email,
        password = password,
        profilePicture = profilePicture,
        isLogged = isLogged
    )
}

fun UserDto.toUser(): User {
    return User(
        id = id,
        name = nombre,
        email = correo,
        isLogged = false
    )
}

fun User.toDto(): UserDto {
    return UserDto(
        id = id,
        nombre = name,
        correo = email
    )
}
