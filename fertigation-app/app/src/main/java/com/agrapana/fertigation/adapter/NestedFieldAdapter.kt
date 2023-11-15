package com.agrapana.fertigation.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.agrapana.fertigation.databinding.TemplateNestedFieldBinding
import com.agrapana.fertigation.model.Field
import com.agrapana.fertigation.ui.activity.DetailMainDeviceActivity
import com.agrapana.fertigation.ui.activity.DetailSupportDeviceActivity
import com.google.gson.Gson

class NestedFieldAdapter(val context: Context, private val fieldList: List<String>, private val fieldDetail: Field): RecyclerView.Adapter<NestedFieldAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = TemplateNestedFieldBinding.inflate(inflater, parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return fieldList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val deviceName: String = fieldList[position]
        holder.binding.nestedItemTv.text = deviceName

        val dateParts = deviceName.trim().split("\\s+".toRegex())
        holder.binding.cardView.setOnClickListener {

            if(dateParts[0] == "Main"){
                val intent = Intent(context, DetailMainDeviceActivity::class.java)
                intent.putExtra("passData", Gson().toJson(fieldDetail))
                context.startActivity(intent)
            } else {
                val intent = Intent(context, DetailSupportDeviceActivity::class.java)
                intent.putExtra("passData", Gson().toJson(fieldDetail))
                intent.putExtra("identity", deviceName)
                context.startActivity(intent)
            }
        }
    }

    class MyViewHolder(val binding: TemplateNestedFieldBinding): RecyclerView.ViewHolder(binding.root){}

}
