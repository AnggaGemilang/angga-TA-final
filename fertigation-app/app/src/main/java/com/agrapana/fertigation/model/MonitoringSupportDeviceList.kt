package com.agrapana.fertigation.model

import com.google.gson.annotations.SerializedName
import java.sql.Timestamp

data class ValueMonitoringSupportDevice(
    @SerializedName("soil_temperature") var soilTemperature: Int? = null,
    @SerializedName("soil_humidity") var soilHumidity: Int? = null,
    @SerializedName("soil_ph") var soilPh: Int? = null,
    @SerializedName("soil_nitrogen") var soilNitrogen: Int? = null,
    @SerializedName("soil_phosphor") var soilPhosphor: Int? = null,
    @SerializedName("soil_kalium") var soilKalium: Int? = null
)

data class MonitoringSupportDevice(
    var monitoring: ValueMonitoringSupportDevice
)

data class ChartSupportDevice(
    val value: String?,
    val created_at: Timestamp?,
    val time: String?,
)

data class ChartSupportDeviceResponse(
    val current_page: Int?,
    val data: List<ChartSupportDevice>?,
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