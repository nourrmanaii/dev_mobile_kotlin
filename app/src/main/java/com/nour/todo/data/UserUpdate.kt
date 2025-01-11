package com.nour.todo.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserUpdate(
    @SerialName("commands")
    val commands: List<UserUpdateCommand>
)

@Serializable
data class UserUpdateCommand(
    @SerialName("type")
    val type: String = "user_update",

    @SerialName("uuid")
    val uuid: String,

    @SerialName("args")
    val args: UserUpdateArgs
)

@Serializable
data class UserUpdateArgs(
    @SerialName("name")
    val name: String
)
