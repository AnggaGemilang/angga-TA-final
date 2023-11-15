package com.agrapana.fertigation.model

import com.google.gson.annotations.SerializedName
import java.sql.Timestamp

data class ValueMonitoringMainDevice(
    @SerializedName("wind_temperature") var windTemperature: Int? = null,
    @SerializedName("wind_humidity") var windHumidity: Int? = null,
    @SerializedName("wind_pressure") var windPressure: Int? = null,
    @SerializedName("wind_speed") var windSpeed: Int? = null,
    @SerializedName("rainfall") var rainfall: Int? = null,
    @SerializedName("light_intensity") var lightIntensity: Int? = null
)

data class MonitoringMainDevice(
    var monitoring: ValueMonitoringMainDevice
)

data class InputCropRecommend(
    val sensor_data: RecommendSensorData
)

data class RecommendSensorData(
    val nValue: String?,
    val pValue: String?,
    val kValue:	String?,
    val temperatureValue: String?,
    val humidityValue: String?,
    val phValue: String?,
    val rainfallValue: String?
)

data class AIInput(
    @SerializedName("sensor_data" ) var sensorData : ArrayList<Int> = arrayListOf()
)

data class ChartMainDevice(
    val value: String?,
    val created_at: Timestamp?,
    val time: String?,
)

data class ChartMainDeviceResponse(
    val current_page: Int?,
    val data: List<ChartMainDevice>?,
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