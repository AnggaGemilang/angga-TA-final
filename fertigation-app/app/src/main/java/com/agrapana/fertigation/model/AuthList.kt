package com.agrapana.fertigation.model

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
data class AuthResponse(val status: String?, val message: String?, val data: Auth?)