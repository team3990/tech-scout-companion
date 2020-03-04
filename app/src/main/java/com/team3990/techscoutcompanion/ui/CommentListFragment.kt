package com.team3990.techscoutcompanion.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query

import com.team3990.techscoutcompanion.R
import com.team3990.techscoutcompanion.adapter.CommentAdapter
import kotlinx.android.synthetic.main.fragment_comment_list.*

private const val ARG_TEAM_NUMBER = "teamNumber"

class CommentListFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance(teamNumber: Long) =
            CommentListFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_TEAM_NUMBER, teamNumber)
                }
            }
    }

    /** Properties */

    private lateinit var query: Query
    private lateinit var adapter: CommentAdapter
    private lateinit var firestore: FirebaseFirestore

    private val teamNumber: Long? by lazy { arguments?.getLong(ARG_TEAM_NUMBER) }

    /** Fragment's lifecycle */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

        // Initialize the query
        query = firestore.collection("comments").whereEqualTo("teamNumber", teamNumber)

        // Initialize the adapter
        adapter = object : CommentAdapter(query) {
            override fun onDataChanged() {
                progressBar.visibility = View.INVISIBLE

                // Show/hide content if the query returns empty.
                if (itemCount == 0) {
                    recyclerComments.visibility = View.INVISIBLE
                } else {
                    recyclerComments.visibility = View.VISIBLE
                }
            }

            override fun onError(e: FirebaseFirestoreException) {
                Log.i("COMMENT LIST FRAGMENT", "An error occurred while tyring to fetch the teams", e)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_comment_list, container, false)

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
        recyclerComments.adapter = adapter
        recyclerComments.layoutManager = LinearLayoutManager(requireContext())
        recyclerComments.addItemDecoration(DividerItemDecoration(requireContext(), RecyclerView.VERTICAL))
    }

}
