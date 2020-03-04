package com.team3990.techscoutcompanion.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class GamePowerCellStatistics(
    val teamNumber:               Long = 0,
    val matchNumber:              Long = 0,
    val autoInnerPortPowerCells:  Long = 0,
    val autoOuterPortPowerCells:  Long = 0,
    val autoBottomPortPowerCells: Long = 0,
    val teleInnerPortPowerCells:  Long = 0,
    val teleOuterPortPowerCells:  Long = 0,
    val teleBottomPortPowerCells: Long = 0
) {

    val totalAutonomousPowerCells: Long
        get() = autoInnerPortPowerCells + autoOuterPortPowerCells + autoBottomPortPowerCells

    val totalTeleopPowerCells: Long
        get() = teleInnerPortPowerCells + teleOuterPortPowerCells + teleBottomPortPowerCells

}