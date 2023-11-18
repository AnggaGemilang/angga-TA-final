package com.agrapana.fertigation.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.agrapana.fertigation.R
import com.agrapana.fertigation.model.User

@SuppressLint("NotifyDataSetChanged")
class WorkerAdapter(taskListener: TaskListener) : RecyclerView.Adapter<WorkerAdapter.MyViewHolder>() {

    private var workers = mutableListOf<User>()
    private var workerEmail = mutableListOf<String>()
    private var taskListener: TaskListener = taskListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context).inflate(R.layout.template_worker, parent, false)
        return MyViewHolder(inflater)
    }

    override fun getItemCount() = workers.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tvName.text = workers[position].name
        holder.tvEmail.text = workers[position].email
        holder.tvRole.text = workers[position].role
        holder.optionMenu.setOnClickListener {
            taskListener.onOptionClick(it, workers[position])
        }
    }

    fun setWorkers(workers: List<User>) {
        this.workers = workers as MutableList<User>
        notifyDataSetChanged()
    }

    private fun isContain(worker: User): Boolean {
        workerEmail.clear()
        for(p in workers){
            workerEmail.add(worker.email!!)
        }
        for(p in workers){
            if(p.email == worker.email){
                return true
            }
        }
        return false
    }

    fun addWorker(worker: User) {
        if (!isContain(worker)){
            workers.add(worker)
        } else {
            val index = workerEmail.indexOf(worker.email)
            workers[index] = worker
        }
        notifyDataSetChanged()
    }

    fun deleteWorker(worker: User) {
        workerEmail.clear()
        for(p in workers){
            workerEmail.add(p.email!!)
        }
        Log.d("hasil 999", workers.toString())
        Log.d("hasil 0", worker.email.toString())
        Log.d("hasil 1", workerEmail.toString())
        val index = workerEmail.indexOf(worker.email)
        Log.d("hasil 2", index.toString())
        workers.removeAt(index)
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