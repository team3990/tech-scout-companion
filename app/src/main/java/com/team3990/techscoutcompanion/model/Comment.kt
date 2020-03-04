package com.team3990.techscoutcompanion.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Comment(
    val content: String = String(),
    val writerName: String = String()
)