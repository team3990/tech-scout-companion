package com.team3990.techscoutcompanion.view

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import com.team3990.techscoutcompanion.R
import com.team3990.techscoutcompanion.adapter.TeamAdapter
import com.team3990.techscoutcompanion.model.Team
import kotlinx.android.synthetic.main.item_team.view.*

class TeamViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(
        snapshot: DocumentSnapshot,
        listener: TeamAdapter.OnTeamSelectedListener?
    ) {
        val team = snapshot.toObject(Team::class.java)
        if (team == null) {
            return
        }

        // Populate the views
        itemView.teamNumberTextView.text = team.teamNumber.toString()
        itemView.teamNameTextView.text = team.name ?: itemView.resources.getText(R.string.name_unknown)
        itemView.teamLocationTextView.text = team.location ?: itemView.resources.getText(R.string.location_unknown)

        // Assign the Click Listener
        itemView.setOnClickListener {
            listener?.onTeamSelected(snapshot)
        }
    }

}