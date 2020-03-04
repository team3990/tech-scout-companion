package com.team3990.techscoutcompanion.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
class GameClimbDurationStatistics(
    val teamNumber:    Long = 0,
    val matchNumber:   Long = 0,
    val climbDuration: String = String()
) {

    /** Properties */

    val climbDurationLong: Long?
        get() = climbDurationLong()

    /** Methods */

    private fun climbDurationLong() : Long? {
        if      (climbDuration == "FAST")   return 1
        else if (climbDuration == "SLOW")   return 3
        else if (climbDuration == "MEDIUM") return 2
        else                                return null
    }

}