package com.agrapana.fertigation.model

import com.google.gson.annotations.SerializedName

data class ValueMonitoringAIProcessing(
    @SerializedName("weather_forecast") var weatherForecast: String? = null,
    @SerializedName("pests_prediction") var pestsPrediction: String? = null,
)

data class Suggestion(
    var cause: String? = null,
    var suggestion: String? = null,
)

data class MonitoringAIProcessing(
    @SerializedName("ai_processing")var aiProcessing: ValueMonitoringAIProcessing
)