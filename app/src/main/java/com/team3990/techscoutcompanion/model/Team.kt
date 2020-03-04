package com.team3990.techscoutcompanion.model

import com.google.firebase.firestore.IgnoreExtraProperties
import java.io.Serializable

@IgnoreExtraProperties
data class Team(
    val name: String? = null,
    val location: String? = null,
    val teamNumber: Int = 0
) : Serializable