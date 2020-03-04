package com.team3990.techscoutcompanion.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.team3990.techscoutcompanion.R
import com.team3990.techscoutcompanion.view.TeamViewHolder

open class TeamAdapter(query: Query,
                       private val listener: OnTeamSelectedListener
) : FirestoreAdapter<TeamViewHolder>(query) {

    interface OnTeamSelectedListener {
        fun onTeamSelected(team: DocumentSnapshot)
    }

    /** Methods */

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return TeamViewHolder(inflater.inflate(R.layout.item_team, parent, false))
    }

    override fun onBindViewHolder(holder: TeamViewHolder, position: Int) {
        holder.bind(getSnapshot(position), listener)
    }

}