package com.agrapana.fertigation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.agrapana.fertigation.R
import com.agrapana.fertigation.databinding.TemplateFieldBinding
import com.agrapana.fertigation.model.Field

class FieldAdapter(val context: Context, taskListener: FieldAdapter.TaskListener): RecyclerView.Adapter<FieldAdapter.MyViewHolder>() {

    var fields = mutableListOf<Field>()
    private var fieldId = mutableListOf<String>()
    private var taskListener: FieldAdapter.TaskListener = taskListener

    fun setFieldList(fields: List<Field>): Boolean {
        this.fields = fields.toMutableList()
        notifyDataSetChanged()
        return true
    }

    private fun isContain(field: Field): Boolean {
        fieldId.clear()
        for(p in fields){
            fieldId.add(field.id)
        }
        for(p in fields){
            if(p.id == field.id){
                return true
            }
        }
        return false
    }

    fun addField(field: Field) {
        if (!isContain(field)){
            fields.add(field)
        } else {
            fieldId.clear()
            for(p in fields){
                fieldId.add(p.id)
            }
            val index = fieldId.indexOf(field.id)
            fields[index] = field
        }
        notifyDataSetChanged()
    }

    fun deleteField(field: Field) {
        fieldId.clear()
        for(p in fields){
            fieldId.add(p.id)
        }
        val index = fieldId.indexOf(field.id)
        fields.removeAt(index)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = TemplateFieldBinding.inflate(inflater, parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return fields.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        var nestedList: MutableList<String> = ArrayList()
        val model: Field = fields[position]
        holder.binding.tvName.text = model.name
        holder.binding.tvAddress.text = model.address
        holder.binding.tvArea.text = "${model.land_area} hA"

        val dateParts = model.created_at.trim().split("\\s+".toRegex())
        holder.binding.tvCreated.text = dateParts[0]

        val isExpandable = model.isExpandable
        holder.binding.expandableLayout.visibility = if (isExpandable) View.VISIBLE else View.GONE

        if (isExpandable) {
            holder.binding.arroImageview.setImageResource(R.drawable.baseline_keyboard_arrow_up_24)
        } else {
            holder.binding.arroImageview.setImageResource(R.drawable.baseline_keyboard_arrow_down_24)
        }

        nestedList.add("Main Device")
        for (i in 1..model.number_of_monitor_device!!){
            nestedList.add("Monitor Device $i")
        }

        val adapter = NestedFieldAdapter(context, nestedList, model)
        holder.binding.childRv.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.binding.childRv.setHasFixedSize(true)
        holder.binding.childRv.adapter = adapter
        holder.binding.wrapper.setOnClickListener(View.OnClickListener {
            model.isExpandable = !model.isExpandable
            notifyItemChanged(holder.adapterPosition)
        })

        holder.binding.optionMenu.setOnClickListener {
            taskListener.onOptionClick(it, fields[position])
        }

    }

    class MyViewHolder(val binding: TemplateFieldBinding): RecyclerView.ViewHolder(binding.root){}

    interface TaskListener {
        fun onOptionClick(view: View, field: Field)
    }

}
