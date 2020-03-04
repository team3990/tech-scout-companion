package com.team3990.techscoutcompanion.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query

import com.team3990.techscoutcompanion.R
import com.team3990.techscoutcompanion.adapter.TeamAdapter
import kotlinx.android.synthetic.main.fragment_team_list.*

class TeamListFragment : Fragment(), TeamAdapter.OnTeamSelectedListener {

    /** Properties */

    private lateinit var query: Query
    private lateinit var adapter: TeamAdapter
    private lateinit var firestore: FirebaseFirestore

    /** Fragment's lifecycle */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

        // Initialize the query
        query = firestore.collection("teams").orderBy("teamNumber")

        // Initialize the adapter
        adapter = object : TeamAdapter(query, this@TeamListFragment) {
            override fun onDataChanged() {
                progressBar.visibility = View.INVISIBLE

                // Show/hide content if the query returns empty.
                if (itemCount == 0) {
                    recyclerTeams.visibility = View.INVISIBLE
                } else {
                    recyclerTeams.visibility = View.VISIBLE
                }
            }

            override fun onError(e: FirebaseFirestoreException) {
                Log.i("TEAM LIST FRAGMENT", "An error occurred while tyring to fetch the teams", e)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_team_list, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Setup the recycler view
        setupRecyclerView()
    }

    override fun onStart() {
        super.onStart()

        // Start listening for Firestore updates
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()

        // Stop listening for Firestore updates
        adapter.stopListening()
    }

    /** Methods */

    private fun setupRecyclerView() {
        recyclerTeams.adapter = adapter
        recyclerTeams.layoutManager = LinearLayoutManager(requireContext())
        recyclerTeams.addItemDecoration(DividerItemDecoration(requireContext(), RecyclerView.VERTICAL))
    }

    override fun onTeamSelected(team: DocumentSnapshot) {
        val teamNumber = team.getLong("teamNumber")

        // Display the team's statistics
        if (teamNumber != null) {
            val teamStatisticsFragment = TeamListFragmentDirections.actionTeamListFragmentToTeamDetailFragment(teamNumber)
            findNavController().navigate(teamStatisticsFragment)
        }
    }

}
