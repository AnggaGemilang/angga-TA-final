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
import com.bumptech.glide.Glide

class FieldAdapter(val context: Context): RecyclerView.Adapter<FieldAdapter.MyViewHolder>() {

    var fieldList = mutableListOf<Field>()

    fun setFieldList(fields: List<Field>): Boolean {
        this.fieldList = fields.toMutableList()
        notifyDataSetChanged()
        return true
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = TemplateFieldBinding.inflate(inflater, parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return fieldList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        var nestedList: MutableList<String> = ArrayList()
        val model: Field = fieldList[position]
        holder.binding.tvName.text = model.name
        holder.binding.tvAddress.text = model.address
        holder.binding.tvArea.text = model.land_area

        val dateParts = model.created_at.toString().trim().split("\\s+".toRegex())
        holder.binding.tvCreated.text = dateParts[0]

        val imageParts = model.thumbnail.toString().trim().split("public/".toRegex())
        if(model.thumbnail != null){
            Glide.with(context).load("https://arnesys.agrapana.tech/storage/" + imageParts[1]).into(holder.binding.thumbnail)
        } else {
            Glide.with(context).load(R.drawable.farmland).into(holder.binding.thumbnail)
        }

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
    }

    class MyViewHolder(val binding: TemplateFieldBinding): RecyclerView.ViewHolder(binding.root){}

}
