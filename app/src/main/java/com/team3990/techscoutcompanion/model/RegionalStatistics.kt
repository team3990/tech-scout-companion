package com.team3990.techscoutcompanion.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class RegionalStatistics(
    val actionParkCount: Float = 0f,
    val actionNoneCount: Float = 0f,
    val actionClimbCount: Float = 0f,
    val rotationControlCount: Float = 0f,
    val positionControlCount: Float = 0f,
    val movesToInitiationLineCount: Float = 0f,
    val playedGamesCount: Float = 0f
) {

    val actionParkProportion: Float
        get() = (actionParkCount / playedGamesCount) * 100

    val actionNoneProportion: Float
        get() = (actionNoneCount / playedGamesCount) * 100

    val actionClimbProportion: Float
        get() = (actionClimbCount / playedGamesCount) * 100

    val rotationControlProportion: Float
        get() = (rotationControlCount / playedGamesCount) * 100

    val positionControlProportion: Float
        get() = (positionControlCount / playedGamesCount) * 100

    val movesToInitiationLineProportion: Float
        get() = (movesToInitiationLineCount / playedGamesCount) * 100

}
