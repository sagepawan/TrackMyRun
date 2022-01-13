package com.pawan.sage.trackmyrun.otherpackages

import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.pawan.sage.trackmyrun.R
import com.pawan.sage.trackmyrun.databinding.MarkerViewBinding
import com.pawan.sage.trackmyrun.db.Run
import java.text.SimpleDateFormat
import java.util.*

class CustomMarkerView(
    val runs: List<Run>,
    context: Context,
    layoutId: Int
): MarkerView(context, layoutId) {
    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        super.refreshContent(e, highlight)

        var markerViewBinding: MarkerViewBinding


        if(e==null){
            return
        }

        //indices of run is mapped to bar entry i.e. x value of bar entry
        val currentRunId = e.x.toInt()
        val run = runs[currentRunId]

        val calender = Calendar.getInstance().apply {
            timeInMillis = run.timeStampRun
        }

        /*val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        dateFormat.format(calender.time).also { markerViewBinding.tvDate.text = it }

        val avgSpeed = "${run.averageSpeedKMPH} Km/h"
        holder.itemView.findViewById<TextView>(R.id.tvAvgSpeed).text = avgSpeed

        val distanceInKm = "${run.distanceRunMeters/1000} Km"
        holder.itemView.findViewById<TextView>(R.id.tvDistance).text = distanceInKm


        holder.itemView.findViewById<TextView>(R.id.tvTime).text =
            TrackingUtility.getStopWatchTimeInFormat(run.runTimeMillis)

        val caloriesSpent = "${run.caloriesSpent} Kcal"
        holder.itemView.findViewById<TextView>(R.id.tvCalories).text = caloriesSpent*/

    }

    //based on documentation from MP android chart
    override fun getOffset(): MPPointF {
        return MPPointF(-width/2f, -height.toFloat())
    }
}