package com.agrapana.fertigation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    var id: String? = "",
    var name: String? = "",
    var email: String? = "",
    var password: String? = "",
    var role: String? = "",
    var loggedIn: Boolean = false
) : Parcelable

@Parcelize
data class Worker(
    val id: String? = "",
    val name: String? = "",
    val email: String? = "",
    val password: String? = "",
    val role: String? = "",
    val fieldId: String? = "",
) : Parcelable

data class AuthResponse(
    val ownerId: String? = "",
    val name: String? = "",
    val role: String? = "",
    val workerId: String? = "",
    val fieldId: String? = "",
)