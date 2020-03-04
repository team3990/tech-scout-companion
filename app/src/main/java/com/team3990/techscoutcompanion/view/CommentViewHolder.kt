package com.team3990.techscoutcompanion.view

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import com.team3990.techscoutcompanion.R
import com.team3990.techscoutcompanion.model.Comment
import kotlinx.android.synthetic.main.item_comment.view.*

class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(snapshot: DocumentSnapshot) {
        val comment = snapshot.toObject(Comment::class.java)
        if (comment == null) {
            return
        }

        // Populate the views
        itemView.commentTextViewe.text = comment.content
        itemView.writerNameTextView.text = itemView.resources.getString(R.string.written_by, comment.writerName)
    }

}