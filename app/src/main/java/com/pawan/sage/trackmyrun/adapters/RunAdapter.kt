package com.pawan.sage.trackmyrun.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pawan.sage.trackmyrun.R
import com.pawan.sage.trackmyrun.databinding.ItemRunBinding
import com.pawan.sage.trackmyrun.db.Run
import com.pawan.sage.trackmyrun.otherpackages.TrackingUtility
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.*

class RunAdapter: RecyclerView.Adapter<RunAdapter.RunViewHolder>() {

    inner class RunViewHolder(itemRunBinding: ItemRunBinding): RecyclerView.ViewHolder(itemRunBinding.root)

    //implementing list differ to add new items only to the list
    val diffCallback = object: DiffUtil.ItemCallback<Run>() {
        override fun areItemsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Run, newItem: Run): Boolean {
            //check the unique hash value for each item to compare the contents
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(list: List<Run>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunViewHolder {
        val itemRunBinding = ItemRunBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RunViewHolder(itemRunBinding)
    }

    //TODO View bind list item into viewholder instead of using resptive IDs

    override fun onBindViewHolder(holder: RunViewHolder, position: Int) {
        val run = differ.currentList[position]

        holder.itemView.apply {
            Glide.with(this).load(run.img).into(holder.itemView.findViewById(R.id.ivRunImage))

            val calender = Calendar.getInstance().apply {
                timeInMillis = run.timeStampRun
            }

            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            holder.itemView.findViewById<TextView>(R.id.tvDate).text =
                dateFormat.format(calender.time)

            val avgSpeed = "${run.averageSpeedKMPH} Km/h"
            holder.itemView.findViewById<TextView>(R.id.tvAvgSpeed).text = avgSpeed

            val distanceInKm = "${run.distanceRunMeters/1000} Km"
            holder.itemView.findViewById<TextView>(R.id.tvDistance).text = distanceInKm


            holder.itemView.findViewById<TextView>(R.id.tvTime).text =
                TrackingUtility.getStopWatchTimeInFormat(run.runTimeMillis)

            val caloriesSpent = "${run.caloriesSpent} Kcal"
            holder.itemView.findViewById<TextView>(R.id.tvCalories).text = caloriesSpent
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    //val itemBinding = ItemRunBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    //        return RunViewHolder(itemBinding)
}