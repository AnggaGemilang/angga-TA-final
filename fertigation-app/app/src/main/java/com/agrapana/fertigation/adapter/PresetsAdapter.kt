package com.agrapana.fertigation.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.agrapana.fertigation.R
import com.agrapana.fertigation.model.Preset
import com.bumptech.glide.Glide

@SuppressLint("NotifyDataSetChanged")
class PresetsAdapter(taskListener: TaskListener) : RecyclerView.Adapter<PresetsAdapter.MyViewHolder>() {

    private var presets = mutableListOf<Preset>()
    private var presetId = mutableListOf<String>()
    private var taskListener: TaskListener = taskListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context).inflate(R.layout.item_presets, parent, false)
        return MyViewHolder(inflater)
    }

    override fun getItemCount() = presets.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tvName.text = presets[position].plantName
        holder.tvgasValve.text = "CO2 Value : " + presets[position].gasValve
        holder.tvPh.text = "Ph : " + presets[position].ph
        holder.tvNutrition.text = "Nutrition : " + presets[position].nutrition
        holder.tvGrowthLamp.text = "Growth Lamp : " + presets[position].growthLamp
        holder.tvSeedlingTime.text = "Seedling Time : " + presets[position].seedlingTime + " days"
        holder.tvGrowTime.text = "Grow Time : " + presets[position].growTime + " days"
        holder.tvTemperature.text = "Temperature : " + presets[position].temperature + "°C"
        holder.tvPump.text = "Pump : " + presets[position].pump
        Glide.with(holder.imageView.context)
            .load(presets[position].imageUrl)
            .into(holder.imageView);

        holder.optionMenu.setOnClickListener {
            taskListener.onOptionClick(it, presets[position])
        }
    }

    fun setPresets(presets: List<Preset>) {
        this.presets = presets as MutableList<Preset>
        notifyDataSetChanged()
    }

    private fun isContain(preset: Preset): Boolean {
        presetId.clear()
        for(p in presets){
            presetId.add(p.id)
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
            Log.d("preset 2", preset.toString())
            presets.add(preset)
        } else {
            Log.d("preset", preset.toString())
            val index = presetId.indexOf(preset.id)
            presets[index] = preset
        }
        notifyDataSetChanged()
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvName: TextView
        var tvgasValve: TextView
        var tvNutrition: TextView
        var tvGrowthLamp: TextView
        var tvSeedlingTime: TextView
        var tvGrowTime: TextView
        var tvTemperature: TextView
        var tvPump: TextView
        var tvPh: TextView
        var optionMenu: ImageButton
        var imageView: ImageView

        init {
            tvName = view.findViewById(R.id.tv_name)
            tvgasValve = view.findViewById(R.id.tv_gas_valve)
            tvNutrition = view.findViewById(R.id.tv_nutrition)
            tvGrowthLamp = view.findViewById(R.id.tv_growth_lamp)
            tvSeedlingTime = view.findViewById(R.id.tv_seedling_time)
            tvGrowTime = view.findViewById(R.id.tv_grow_time)
            tvTemperature = view.findViewById(R.id.tv_temperature)
            tvPump = view.findViewById(R.id.tv_pump)
            imageView = view.findViewById(R.id.thumbnail)
            tvPh = view.findViewById(R.id.tv_ph)
            optionMenu = view.findViewById(R.id.option_menu)
        }
    }

    interface TaskListener {
        fun onOptionClick(view: View, preset: Preset)
    }
}