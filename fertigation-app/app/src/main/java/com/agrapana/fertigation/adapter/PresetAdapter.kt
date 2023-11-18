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
import com.agrapana.fertigation.model.Preset
import com.agrapana.fertigation.model.User
import com.bumptech.glide.Glide

@SuppressLint("NotifyDataSetChanged")
class PresetAdapter(taskListener: TaskListener) : RecyclerView.Adapter<PresetAdapter.MyViewHolder>() {

    private var presets = mutableListOf<Preset>()
    private var presetId = mutableListOf<String>()
    private var taskListener: TaskListener = taskListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context).inflate(R.layout.template_preset, parent, false)
        return MyViewHolder(inflater)
    }

    override fun getItemCount() = presets.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tvPresetName.text = presets[position].presetName
        holder.tvIdealMoisture.text = "Moisture: ${presets[position].idealMoisture}%"
        Glide.with(holder.imageView.context)
            .load(presets[position].imageUrl)
            .into(holder.imageView);
        holder.tvDoIrrigation.text = "Irrigation: Pukul ${presets[position].irrigationTimes}/${presets[position].irrigationDays} hari"
        holder.tvDoFertigation.text = "Fertigation: Pukul ${presets[position].fertigationTimes}/${presets[position].fertigationDays} hari"
        holder.optionMenu.setOnClickListener {
            taskListener.onOptionClick(it, presets[position])
        }
    }

    fun setPreset(presets: List<Preset>) {
        this.presets = presets as MutableList<Preset>
        notifyDataSetChanged()
    }

    private fun isContain(preset: Preset): Boolean {
        presetId.clear()
        for(p in presets){
            presetId.add(preset.id)
        }
        for(p in presets){
            if(p.id == preset.id){
                return true
            }
        }
        return false
    }

    fun addPreset(preset: Preset) {
        if (!isContain(preset)){
            presets.add(preset)
        } else {
            presetId.clear()
            for(p in presets){
                presetId.add(p.id)
            }
            val index = presetId.indexOf(preset.id)
            presets[index] = preset
        }
        notifyDataSetChanged()
    }

    fun deletePreset(preset: Preset) {
        presetId.clear()
        for(p in presets){
            presetId.add(p.id)
        }
        val index = presetId.indexOf(preset.id)
        presets.removeAt(index)
        notifyDataSetChanged()
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvPresetName: TextView
        var tvIdealMoisture: TextView
        var tvDoFertigation: TextView
        var tvDoIrrigation: TextView
        var optionMenu: ImageButton
        var imageView: ImageView

        init {
            tvPresetName = view.findViewById(R.id.tv_name)
            imageView = view.findViewById(R.id.thumbnail)
            tvIdealMoisture = view.findViewById(R.id.tv_ideal_moisture)
            tvDoFertigation = view.findViewById(R.id.tv_do_fertigation)
            tvDoIrrigation = view.findViewById(R.id.tv_do_irrigation)
            optionMenu = view.findViewById(R.id.option_menu)
        }
    }

    interface TaskListener {
        fun onOptionClick(view: View, preset: Preset)
    }
}