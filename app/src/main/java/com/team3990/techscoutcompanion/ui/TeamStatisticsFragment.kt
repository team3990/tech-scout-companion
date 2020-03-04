package com.team3990.techscoutcompanion.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.team3990.techscoutcompanion.R
import com.team3990.techscoutcompanion.model.GameClimbDurationStatistics
import com.team3990.techscoutcompanion.model.GamePowerCellStatistics
import com.team3990.techscoutcompanion.model.RegionalStatistics
import kotlinx.android.synthetic.main.fragment_team_statistics.*

private const val ARG_TEAM_NUMBER = "teamNumber"

class TeamStatisticsFragment : Fragment() {

    companion object {
        const val QUERY_FIELD_TEAM_NUMBER = "teamNumber"
        const val ORDER_FIELD_MATCH_NUMBER = "matchNumber"

        @JvmStatic
        fun newInstance(teamNumber: Long) =
            TeamStatisticsFragment().apply {
                arguments = Bundle().apply { putLong(ARG_TEAM_NUMBER, teamNumber) }
            }
    }

    /** Properties */

    private lateinit var firestore: FirebaseFirestore
    private val teamNumber: Long? by lazy { arguments?.getLong(ARG_TEAM_NUMBER) }

    /** Fragment's lifecycle */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_team_statistics, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Hide the card layout
        cardsLayout.visibility = View.INVISIBLE
    }

    override fun onStart() {
        super.onStart()

        // Fetch the team's statistics and Power Cells statistics
        val fetchTeamStatisticsTask = firestore.collection("teamStatistics").whereEqualTo(QUERY_FIELD_TEAM_NUMBER, teamNumber).limit(1).get()
        val fetchTeamPowerCellStatisticsTask = firestore.collection("powerCellStatistics").whereEqualTo(QUERY_FIELD_TEAM_NUMBER, teamNumber).orderBy(ORDER_FIELD_MATCH_NUMBER).get()
        val fetchTeamClimbDurationStatisticsTask = firestore.collection("climbDurationStatistics").whereEqualTo(QUERY_FIELD_TEAM_NUMBER, teamNumber).orderBy(ORDER_FIELD_MATCH_NUMBER).get()

        // Handle tasks completion
        Tasks.whenAll(fetchTeamStatisticsTask, fetchTeamPowerCellStatisticsTask, fetchTeamClimbDurationStatisticsTask)
            .addOnSuccessListener {
                val teamStatisticsQueryResult = fetchTeamStatisticsTask.getResult()
                val teamPowerCellStatisticsResult = fetchTeamPowerCellStatisticsTask.getResult()
                val teamClimbDurationStatisticsResult = fetchTeamClimbDurationStatisticsTask.getResult()
                if (teamStatisticsQueryResult != null) {
                    onTeamStatisticsLoaded(teamStatisticsQueryResult)
                }
                if (teamPowerCellStatisticsResult != null) {
                    onTeamPowerCellStatisticsLoaded(teamPowerCellStatisticsResult)
                }
                if (teamClimbDurationStatisticsResult != null) {
                    onTeamClimbDurationStatisticsLoaded(teamClimbDurationStatisticsResult)
                }
            }.addOnFailureListener {
                Log.e("TEAM STATS.", "An error occurred while trying to fetch the team's stats.", it)
            }
    }

    /** Methods */

    private fun populateCharts(stats: RegionalStatistics) {
        populatePositionControlPieChart(stats.positionControlProportion)
        populateRotationControlPieChart(stats.rotationControlProportion)
        populateMoveToInitiationLineBarChart(stats.movesToInitiationLineProportion)
        populateEndgameActionPieChart(stats.actionNoneProportion, stats.actionParkProportion, stats.actionClimbProportion)
    }

    private fun onTeamStatisticsLoaded(querySnapshot: QuerySnapshot) {
        progressBar.visibility = View.INVISIBLE
        cardsLayout.visibility = View.VISIBLE

        // Obtain the first document returned by the query
        val teamStats = querySnapshot.documents.first().toObject(RegionalStatistics::class.java)

        // Populate the charts using the stats object
        if (teamStats != null) {
            populateCharts(teamStats)
        }
    }

    private fun onTeamPowerCellStatisticsLoaded(querySnapshot: QuerySnapshot) {
        val autonomousPowerCellEntries = arrayListOf<Entry>()
        val teleoperatedPowerCellEntries = arrayListOf<Entry>()

        querySnapshot.documents.forEach {
            val powerCellStatistics = it.toObject(GamePowerCellStatistics::class.java) ?: return
            // Create Line Chart entries from the instantiated Power Cell Statistics
            // object
            autonomousPowerCellEntries.add(Entry(powerCellStatistics.matchNumber.toFloat(), powerCellStatistics.totalAutonomousPowerCells.toFloat()))
            teleoperatedPowerCellEntries.add(Entry(powerCellStatistics.matchNumber.toFloat(), powerCellStatistics.totalTeleopPowerCells.toFloat()))
        }

        populatePowerCellsLineChart(autonomousPowerCellEntries.toList(), teleoperatedPowerCellEntries.toList())
    }

    private fun onTeamClimbDurationStatisticsLoaded(querySnapshot: QuerySnapshot) {
        val climbDurationEntries = arrayListOf<Entry>()

        querySnapshot.documents.forEach {
            val climbDurationStatistics = it.toObject(GameClimbDurationStatistics::class.java)
            if (climbDurationStatistics?.climbDuration == null) {
                return
            }
            // Create Line Chart entries from the instantiated Climb Duration Statistics
            // object
            climbDurationEntries.add(Entry(climbDurationStatistics.matchNumber.toFloat(), climbDurationStatistics.climbDurationLong!!.toFloat()))
        }

        populateClimbDurationLineChart(climbDurationEntries)
    }

    /**
     * Populates the Line Chart visualizing the amount of time it takes for the robot to climb.
     *
     * @param climbDurationEntries The list of climb durations.
     * */
    private fun populateClimbDurationLineChart(climbDurationEntries: List<Entry>) {
        val climbDurationDataSet = LineDataSet(climbDurationEntries, "Climb Duration")

        // Configure the data set
        climbDurationDataSet.color = ContextCompat.getColor(requireContext(), R.color.lightGray)
        climbDurationDataSet.lineWidth = 2f
        climbDurationDataSet.setCircleColor(climbDurationDataSet.color)
        climbDurationDataSet.setDrawCircles(climbDurationEntries.size == 1)
        climbDurationDataSet.setDrawFilled(true)
        climbDurationDataSet.fillColor = ContextCompat.getColor(requireContext(), R.color.lightGray30)

        // Configure the Line Chart
        climbDurationLineChart.legend.isEnabled = false
        climbDurationLineChart.axisRight.isEnabled = false
        climbDurationLineChart.description.isEnabled = false
        climbDurationLineChart.xAxis.setDrawGridLines(false)
        climbDurationLineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM

        // Populate the line chart
        climbDurationLineChart.data = LineData(climbDurationDataSet)
        climbDurationLineChart.invalidate()
    }

    /**
     * Populates the Line Chart visualizing the amount of Power Cells scored during each match for the autonomous and
     * teleoperated modes.
     *
     * @param autonomousPowerCellEntries    The list of Power Cells scored during the autonomous period across the
     *                                      regional's games.
     * @param teleoperatedPowerCellEntries The list of Power Cells scored during the teleoperated period across the
     *                                      regional's games.
     * */
    private fun populatePowerCellsLineChart(autonomousPowerCellEntries: List<Entry>, teleoperatedPowerCellEntries: List<Entry>) {
        val autonomousDataSet = LineDataSet(autonomousPowerCellEntries, "Autonomous Mode")
        val teleoperatedDataSet = LineDataSet(teleoperatedPowerCellEntries, "Teleoperated Mode")

        // Configure the data sets
        autonomousDataSet.color = ContextCompat.getColor(requireContext(), R.color.lightGray)
        autonomousDataSet.lineWidth = 2f
        autonomousDataSet.setDrawCircles(false)

        teleoperatedDataSet.color = ContextCompat.getColor(requireContext(), R.color.orange)
        teleoperatedDataSet.lineWidth = 2f
        teleoperatedDataSet.setDrawCircles(false)

        // Configure the line data object used to draw the line graph
        val dataSets = arrayListOf<ILineDataSet>(autonomousDataSet, teleoperatedDataSet)
        val lineData = LineData(dataSets)

        // Configure the Line Chart
        scoredPowerCellsLineChart.axisRight.isEnabled = false
        scoredPowerCellsLineChart.description.isEnabled = false
        scoredPowerCellsLineChart.xAxis.setDrawGridLines(false)
        scoredPowerCellsLineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM

        // Populate the line chart
        scoredPowerCellsLineChart.data = lineData
        scoredPowerCellsLineChart.invalidate()
    }

    /**
     * Populates the Pie Chart visualizing the amount of time the robot completed the position control
     * proportionally to the amount of played games.
     *
     * @param positionControlProportion The amount of times the robot completed the position control proportionally to the
     *                                  amount of played games.
     * */
    private fun populatePositionControlPieChart(positionControlProportion: Float) {
        val notPositionControlProportion = 100 - positionControlProportion

        // Initialize the entries.
        val positionControlEntries = listOf<PieEntry>(
            PieEntry(positionControlProportion, resources.getString(R.string.yes)),
            PieEntry(notPositionControlProportion, resources.getString(R.string.no))
        )

        // Configure the PieDataSetObject
        val pieDataSet = PieDataSet(positionControlEntries, "")
        pieDataSet.setDrawValues(false)
        pieDataSet.setColors(ContextCompat.getColor(requireContext(), R.color.lightGray), ContextCompat.getColor(requireContext(), R.color.orange))

        // Configure the PieData object
        val pieData = PieData(pieDataSet)

        // Configure the Pie Chart
        positionControlPieChart.setDrawEntryLabels(false)
        positionControlPieChart.description.isEnabled = false
        positionControlPieChart.centerText = resources.getString(R.string.completes_position_control)

        // Populate the Pie Chart
        positionControlPieChart.data = pieData
        positionControlPieChart.invalidate()
    }

    /**
     * Populates the Pie Chart visualizing the amount of time the robot completed the rotation control
     * proportionally to the amount of played games.
     *
     * @param rotationControlProportion The amount of times the robot completed the rotation control proportionally to the
     *                                  amount of played games.
     * */
    private fun populateRotationControlPieChart(rotationControlProportion: Float) {
        val notRotationControlProportion = 100 - rotationControlProportion

        // Initialize the entries
        val rotationControlEntries = listOf<PieEntry>(
            PieEntry(rotationControlProportion, resources.getString(R.string.yes)),
            PieEntry(notRotationControlProportion, resources.getString(R.string.no))
        )

        // Configure the PieDataSetObject
        val pieDataSet = PieDataSet(rotationControlEntries, "")
        pieDataSet.setDrawValues(false)
        pieDataSet.setColors(ContextCompat.getColor(requireContext(), R.color.lightGray), ContextCompat.getColor(requireContext(), R.color.orange))

        // Configure the PieData object
        val pieData = PieData(pieDataSet)

        // Configure the Pie Chart
        rotationControlPieChart.setDrawEntryLabels(false)
        rotationControlPieChart.description.isEnabled = false
        rotationControlPieChart.centerText = resources.getString(R.string.completes_rotation_control)

        // Populate the Pie Chart
        rotationControlPieChart.data = pieData
        rotationControlPieChart.invalidate()
    }

    /**
     * Populates the Pie Chart visualizing the amount of time the robot moved to the initiation line during the autonomous period
     * proportionally to the amount of played games.
     *
     * @param movesToInitiationLineProportion The amount of times the robot moved to the initiation line during the autonomous period
     *                                        proportionally to the amount of played games.
     * */
    private fun populateMoveToInitiationLineBarChart(movesToInitiationLineProportion: Float) {
        val notMovingToInitiationLineProportion = 100 - movesToInitiationLineProportion

        // Initialize the entries
        val movesToInitiationLineEntries = listOf<PieEntry>(
            PieEntry(movesToInitiationLineProportion, resources.getString(R.string.yes)),
            PieEntry(notMovingToInitiationLineProportion, resources.getString(R.string.no))
        )

        // Configure the PieDataSetObject
        val pieDataSet = PieDataSet(movesToInitiationLineEntries, "")
        pieDataSet.setDrawValues(false)
        pieDataSet.setColors(ContextCompat.getColor(requireContext(), R.color.lightGray), ContextCompat.getColor(requireContext(), R.color.orange))

        // Configure the PieData object
        val pieData = PieData(pieDataSet)

        // Configure the Pie Chart
        movesToInitiationlinePieChart.setDrawEntryLabels(false)
        movesToInitiationlinePieChart.description.isEnabled = false
        movesToInitiationlinePieChart.centerText = resources.getString(R.string.distribution_across_played_games)

        // Populate the Pie Chart
        movesToInitiationlinePieChart.data = pieData
        movesToInitiationlinePieChart.invalidate()
    }

    /**
     * Populates the Pie Chart visualizing the proportion of each endgame action the robot performs. Relative to the amount of played
     * games.
     *
     * @param actionNoneProportion  The amount of time the robot did nothing. Relative to the amount of played games.
     * @param actionParkProportion  The amount of time the robot parked. Relative to the amount of played games.
     * @param actionClimbProportion The amount of time the robot climbed. Relative to the amount of played games.
     * */
    private fun populateEndgameActionPieChart(actionNoneProportion: Float, actionParkProportion: Float, actionClimbProportion: Float) {
        val endgameActionEntries = listOf<PieEntry>(
            PieEntry(actionNoneProportion, resources.getString(R.string.none)),
            PieEntry(actionParkProportion, resources.getString(R.string.parks)),
            PieEntry(actionClimbProportion, resources.getString(R.string.climbs))
        )

        // Configure the PieDataSetObject
        val pieDataSet = PieDataSet(endgameActionEntries, "")
        pieDataSet.setDrawValues(false)
        pieDataSet.setColors(ContextCompat.getColor(requireContext(), R.color.lightGray), ContextCompat.getColor(requireContext(), R.color.orange), ContextCompat.getColor(requireContext(), R.color.lightYellow))

        // Configure the PieData object
        val pieData = PieData(pieDataSet)

        // Configure the Pie Chart
        endgameActionPieChart.setDrawEntryLabels(false)
        endgameActionPieChart.centerText = resources.getString(R.string.distribution_across_played_games)
        endgameActionPieChart.description.isEnabled = false

        // Populate the Pie Chart
        endgameActionPieChart.data = pieData
        endgameActionPieChart.invalidate()
    }

}
