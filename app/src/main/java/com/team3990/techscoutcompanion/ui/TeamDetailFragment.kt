package com.team3990.techscoutcompanion.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs

import com.team3990.techscoutcompanion.R
import kotlinx.android.synthetic.main.fragment_team_detail.*

private const val NUM_PAGES = 3
private val PAGE_TITLES_RESOURCE_IDENTIFIERS = arrayOf(R.string.analytics, R.string.comments, R.string.about_robot)

class TeamDetailFragment : Fragment() {

    /** Properties */

    private val args: TeamDetailFragmentArgs by navArgs()

    /** Fragment's lifecycle */

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_team_detail, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the pager's adapter instance.
        viewPager.adapter = TeamDetailSliderPagerAdapter(childFragmentManager, args.teamNumber)

        // Connect the tab layout with the view pager
        tabLayout.setupWithViewPager(viewPager)
    }

    /**
     * A simple pager adapter that represents two Fragments objects, in
     * sequence.
     */
    private inner class TeamDetailSliderPagerAdapter(fm: FragmentManager,
                                                     private val teamNumber: Long) : FragmentStatePagerAdapter(fm) {

        /** Methods */

        override fun getCount(): Int = NUM_PAGES

        override fun getItem(position: Int): Fragment {
            if (position == 0) {
                return TeamStatisticsFragment.newInstance(teamNumber)
            } else if (position == 1) {
                return CommentListFragment.newInstance(teamNumber)
            }
            return Fragment()
        }

        override fun getPageTitle(position: Int): CharSequence? = resources.getString(
            PAGE_TITLES_RESOURCE_IDENTIFIERS[position]
        )

    }

}
