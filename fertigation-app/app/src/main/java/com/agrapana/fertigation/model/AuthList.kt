package com.agrapana.fertigation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.sql.Timestamp

data class Auth(
    val id: String?,
    val username: String?,
    val first_name: String?,
    val last_name: String?,
    val no_telp: String?,
    val address: String?,
    val photo: String?,
    val email: String?,
    val email_verified_at: String?,
    val password: String?,
    val remember_token: String?,
    val created_at: Timestamp?,
    val updated_at: Timestamp?
    )

@Parcelize
data class User(
    val id: String? = "",
    val name: String? = "",
    val email: String? = "",
    val password: String? = "",
    val role: String? = "",
) : Parcelable

data class AuthResponse(val id: String?, val name: String?, val role: String?)