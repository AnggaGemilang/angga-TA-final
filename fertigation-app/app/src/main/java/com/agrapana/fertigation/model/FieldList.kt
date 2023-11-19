package com.agrapana.fertigation.model

import com.google.gson.annotations.SerializedName
import java.sql.Timestamp
import java.time.Instant
import java.time.format.DateTimeFormatter

data class Field(
    var id: String = "",
    var name: String = "",
    var address: String = "",
    var land_area: String = "",
    var hardware_code: String = "",
    var number_of_monitor_device: Int = 0,
    var created_at: String = "",
    var isExpandable: Boolean = false,
)
data class Links(
    val url: String?,
    val label: String?,
    val active: Boolean?
)

data class CropRecommendResponse(
    @SerializedName("recommend_crop") var recommendCrop: String? = null
)

data class PestPredictionResponse(
    @SerializedName("Thripidae") var Thripidae : String? = null
)

data class FieldResponse(
    val current_page: Int?,
    val data: List<Field>?,
    val first_page_url: String?,
    val from: Int?,
    val last_page: Int?,
    val last_page_url: String?,
    val links: List<Links>?,
    val next_page_url: String?,
    val path: String?,
    val per_page: Int?,
    val prev_page_url: String?,
    val to: Int?,
    val total: Int?
)