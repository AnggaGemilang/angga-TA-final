package com.agrapana.fertigation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.agrapana.fertigation.R
import com.agrapana.fertigation.model.ParameterPreset
import com.bumptech.glide.Glide

@SuppressLint("NotifyDataSetChanged")
class PresetAdapter(taskListener: TaskListener) : RecyclerView.Adapter<PresetAdapter.MyViewHolder>() {

    private var parameterPresets = mutableListOf<ParameterPreset>()
    private var presetId = mutableListOf<String>()
    private var taskListener: TaskListener = taskListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context).inflate(R.layout.template_preset, parent, false)
        return MyViewHolder(inflater)
    }

    override fun getItemCount() = parameterPresets.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tvPresetName.text = parameterPresets[position].presetName
        holder.tvIdealMoisture.text = "Moisture: ${parameterPresets[position].idealMoisture}%"
        Glide.with(holder.imageView.context)
            .load(parameterPresets[position].imageUrl)
            .into(holder.imageView)
        holder.irrigationAge.text = "I. Age: ${parameterPresets[position].irrigationAge} HST"
        holder.fertigationAge.text = "F. Age: ${parameterPresets[position].fertigationAge} HST"
        holder.irrigationDose.text = "I. Dose: ${parameterPresets[position].irrigationDoses} mL"
        holder.fertigationDose.text = "F. Dose: ${parameterPresets[position].fertigationDoses} mL"
        holder.tvDoIrrigation.text = "I. Times: ${parameterPresets[position].irrigationTimes}/${parameterPresets[position].irrigationDays} hari"
        holder.tvDoFertigation.text = "F. Times: ${parameterPresets[position].fertigationTimes}/${parameterPresets[position].fertigationDays} hari"
        holder.optionMenu.setOnClickListener {
            taskListener.onOptionClick(it, parameterPresets[position])
        }
    }

    fun setPreset(parameterPresets: List<ParameterPreset>) {
        this.parameterPresets = parameterPresets as MutableList<ParameterPreset>
        notifyDataSetChanged()
    }

    private fun isContain(parameterPreset: ParameterPreset): Boolean {
        presetId.clear()
        for(p in parameterPresets){
            presetId.add(parameterPreset.id)
        }
        for(p in parameterPresets){
            if(p.id == parameterPreset.id){
                return true
            }
        }
        return false
    }

    fun addPreset(parameterPreset: ParameterPreset) {
        if (!isContain(parameterPreset)){
            parameterPresets.add(parameterPreset)
        } else {
            presetId.clear()
            for(p in parameterPresets){
                presetId.add(p.id)
            }
            val index = presetId.indexOf(parameterPreset.id)
            parameterPresets[index] = parameterPreset
        }
        notifyDataSetChanged()
    }

    fun deletePreset(parameterPreset: ParameterPreset) {
        presetId.clear()
        for(p in parameterPresets){
            presetId.add(p.id)
        }
        val index = presetId.indexOf(parameterPreset.id)
        parameterPresets.removeAt(index)
        notifyDataSetChanged()
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvPresetName: TextView
        var tvIdealMoisture: TextView
        var tvDoFertigation: TextView
        var tvDoIrrigation: TextView
        var fertigationDose: TextView
        var irrigationDose: TextView
        var fertigationAge: TextView
        var irrigationAge: TextView
        var optionMenu: ImageButton
        var imageView: ImageView

        init {
            tvPresetName = view.findViewById(R.id.tv_name)
            imageView = view.findViewById(R.id.thumbnail)
            tvIdealMoisture = view.findViewById(R.id.tv_ideal_moisture)
            tvDoFertigation = view.findViewById(R.id.tv_do_fertigation)
            tvDoIrrigation = view.findViewById(R.id.tv_do_irrigation)
            fertigationAge = view.findViewById(R.id.tv_fertigation_age)
            irrigationAge = view.findViewById(R.id.tv_irrigation_age)
            fertigationDose = view.findViewById(R.id.tv_fertigation_dose)
            irrigationDose = view.findViewById(R.id.tv_irrigation_dose)
            optionMenu = view.findViewById(R.id.option_menu)
        }
    }

    interface TaskListener {
        fun onOptionClick(view: View, parameterPreset: ParameterPreset)
    }
}