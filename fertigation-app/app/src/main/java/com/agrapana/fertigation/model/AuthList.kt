package com.agrapana.fertigation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val id: String? = "",
    val name: String? = "",
    val email: String? = "",
    val password: String? = "",
    val role: String? = "",
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
    val id: String? = "",
    val name: String? = "",
    val role: String? = "",
)