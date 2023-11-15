package com.agrapana.fertigation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.agrapana.fertigation.databinding.TemplateSuggestionBinding
import com.agrapana.fertigation.model.Suggestion

class SuggestionAdapter(val context: Context, private val suggestionList: ArrayList<Suggestion>): RecyclerView.Adapter<SuggestionAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = TemplateSuggestionBinding.inflate(inflater, parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return suggestionList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val suggestionVal: Suggestion = suggestionList[position]
        holder.binding.txtCause.text = suggestionVal.cause
        holder.binding.txtSuggestion.text = suggestionVal.suggestion
    }

    class MyViewHolder(val binding: TemplateSuggestionBinding): RecyclerView.ViewHolder(binding.root){}

}