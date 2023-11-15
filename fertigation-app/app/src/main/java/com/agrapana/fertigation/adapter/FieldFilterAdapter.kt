package com.agrapana.fertigation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.agrapana.fertigation.R
import com.agrapana.fertigation.databinding.TemplateFilterFieldBinding
import com.agrapana.fertigation.helper.ChangeFieldListener
import com.agrapana.fertigation.model.Field

class FieldFilterAdapter(val context: Context): RecyclerView.Adapter<FieldFilterAdapter.MyViewHolder>() {

    var fieldList = mutableListOf<Field>()
    var changeFieldListener: ChangeFieldListener? = null
    var cardViewList: MutableList<CardView> = ArrayList()
    var textViewList: MutableList<TextView> = ArrayList()

    fun setFieldList(fields: List<Field>): Boolean {
        this.fieldList = fields.toMutableList()
        notifyDataSetChanged()
        return true
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = TemplateFilterFieldBinding.inflate(inflater, parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return fieldList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        cardViewList.add(holder.binding.cardViewFilter)
        textViewList.add(holder.binding.txtName)

        cardViewList[0].background.setTint(ContextCompat.getColor(context, R.color.green_20))
        textViewList[0].setTextColor(ContextCompat.getColor(context, R.color.white))

        if(position == 0){
            changeFieldListener?.onChangeField(fieldList[position].id)
        }

        if(cardViewList.size == fieldList.size){
            val layoutParams = holder.binding.cardViewFilter.layoutParams as LinearLayout.LayoutParams
            layoutParams.setMargins(0, 0, 115, 0)
            holder.binding.cardViewFilter.layoutParams = layoutParams
        }

        holder.binding.txtName.text = fieldList[position].plant_type
        holder.binding.cardViewFilter.setOnClickListener {
            for (cardView in cardViewList) {
                cardView.background.setTint(ContextCompat.getColor(context, R.color.black_20))
                for (textView in textViewList) {
                    textView.setTextColor(ContextCompat.getColor(context, R.color.black_40))
                }
            }
            holder.binding.txtName.setTextColor(ContextCompat.getColor(context, R.color.white))
            holder.binding.cardViewFilter.background.setTint(ContextCompat.getColor(context, R.color.green_20))
            changeFieldListener?.onChangeField(fieldList[position].id)
        }
    }

    class MyViewHolder(val binding: TemplateFilterFieldBinding): RecyclerView.ViewHolder(binding.root){}

}
