package com.agrapana.fertigation.model

data class MonitoringMonitorDevice(
    var moisture: Int = 0,
    var waterLevel: Int = 0,
)

data class MonitoringPrimaryDevice(
    var fertilizerTank: Int = 0,
    var waterTank: Int = 0,
    var wateringStatus: String = "",
    var fertilizingStatus: String = "",
    var waterPumpStatus: String = "",
    var fertilizerPumpStatus: String = "",
    var takenAt: String = ""
)