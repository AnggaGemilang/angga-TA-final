package com.agrapana.fertigation.model

data class MonitoringMonitorDevice(
    var moisture: Int = 0,
    var waterLevel: Int = 0,
)

data class MonitoringPrimaryDevice(
    var fertilizerTank: Long = 0,
    var fertilizerValve: String = "",
    var fertilizingStatus: String = "",
    var pumpStatus: String = "",
    var waterTank: Long = 0,
    var waterValve: String = "",
    var wateringStatus: String = "",
    var takenAt: String = ""
)