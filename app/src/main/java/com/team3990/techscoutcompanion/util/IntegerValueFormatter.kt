package com.team3990.techscoutcompanion.util

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.DecimalFormat

class IntegerValueFormatter : ValueFormatter() {

    /** Properties */

    private val format: DecimalFormat

    /**
     * Constructor
     *
     * Creates a new IntegerValueFormatter instance
     * */
    init {
        format = DecimalFormat("###,###,##0")
    }

    override fun getAxisLabel(value: Float, axis: AxisBase?): String = format.format(value)
}