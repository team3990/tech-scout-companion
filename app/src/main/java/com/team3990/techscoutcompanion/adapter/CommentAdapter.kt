package com.team3990.techscoutcompanion.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.firebase.firestore.Query
import com.team3990.techscoutcompanion.R
import com.team3990.techscoutcompanion.view.CommentViewHolder

open class CommentAdapter(query: Query) : FirestoreAdapter<CommentViewHolder>(query) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return CommentViewHolder(inflater.inflate(R.layout.item_comment, parent, false))
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(getSnapshot(position))
    }

}