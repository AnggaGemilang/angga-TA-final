package com.agrapana.fertigation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.agrapana.fertigation.R
import com.agrapana.fertigation.model.User

@SuppressLint("NotifyDataSetChanged")
class PresetAdapter(taskListener: TaskListener) : RecyclerView.Adapter<PresetAdapter.MyViewHolder>() {

    private var presets = mutableListOf<User>()
    private var workerEmail = mutableListOf<String>()
    private var taskListener: TaskListener = taskListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context).inflate(R.layout.template_worker, parent, false)
        return MyViewHolder(inflater)
    }

    override fun getItemCount() = presets.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tvName.text = presets[position].name
        holder.tvEmail.text = presets[position].email
        holder.tvRole.text = presets[position].role
        holder.optionMenu.setOnClickListener {
            taskListener.onOptionClick(it, presets[position])
        }
    }

    fun setWorkers(workers: List<User>) {
        this.presets = workers as MutableList<User>
        notifyDataSetChanged()
    }

    private fun isContain(worker: User): Boolean {
        workerEmail.clear()
        for(p in presets){
            workerEmail.add(worker.email!!)
        }
        for(p in presets){
            if(p.email == worker.email){
                return true
            }
        }
        return false
    }

    fun addWorker(worker: User) {
        if (!isContain(worker)){
            presets.add(worker)
        } else {
            workerEmail.clear()
            for(p in presets){
                workerEmail.add(p.email!!)
            }
            val index = workerEmail.indexOf(worker.email)
            presets[index] = worker
        }
        notifyDataSetChanged()
    }

    fun deleteWorker(worker: User) {
        workerEmail.clear()
        for(p in presets){
            workerEmail.add(p.email!!)
        }
        val index = workerEmail.indexOf(worker.email)
        presets.removeAt(index)
        notifyDataSetChanged()
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvName: TextView
        var tvEmail: TextView
        var tvRole: TextView
        var optionMenu: ImageButton

        init {
            tvName = view.findViewById(R.id.tv_name)
            tvEmail = view.findViewById(R.id.tv_email)
            tvRole = view.findViewById(R.id.tv_role)
            optionMenu = view.findViewById(R.id.option_menu)
        }
    }

    interface TaskListener {
        fun onOptionClick(view: View, worker: User)
    }
}