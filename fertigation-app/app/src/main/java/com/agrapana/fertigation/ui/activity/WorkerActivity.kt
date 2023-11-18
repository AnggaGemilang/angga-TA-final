package com.agrapana.fertigation.ui.activity

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProviders
import com.agrapana.fertigation.R
import com.agrapana.fertigation.adapter.WorkerAdapter
import com.agrapana.fertigation.databinding.ActivityWorkerBinding
import com.agrapana.fertigation.helper.WorkerListener
import com.agrapana.fertigation.model.User
import com.agrapana.fertigation.ui.fragment.AddWorkerFragment
import com.agrapana.fertigation.viewmodel.WorkerViewModel

class WorkerActivity : AppCompatActivity(), WorkerAdapter.TaskListener, WorkerListener {

    private lateinit var binding: ActivityWorkerBinding
    private lateinit var viewModel: WorkerViewModel
    private val adapter = WorkerAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        val w = window
        w.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        setSupportActionBar(binding.toolbar);
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Workers"

        initViewModel()
        binding.recyclerView.adapter = adapter

        binding.fab.setOnClickListener {
            val dialog = AddWorkerFragment()
            val bundle = Bundle()
            bundle.putString("status", "tambah")
            dialog.arguments = bundle
            dialog.show(supportFragmentManager, "BottomSheetDialog")
        }

    }

    private fun initViewModel() {
        val prefs: SharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE)
        val clientId: String = prefs.getString("client_id", "")!!
        viewModel = ViewModelProviders.of(this)[WorkerViewModel::class.java]
        viewModel.fetchWorkers(clientId)
        viewModel.workerListener = this
        viewModel.getRealtimeUpdates(clientId)
        viewModel.worker.observe(this) {
            adapter.addWorker(it)
            binding.notFound.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }
        viewModel.deletedworker.observe(this) {
            adapter.deleteWorker(it)
        }
        viewModel.workers.observe(this) {
            if (it!!.isNotEmpty()) {
                binding.workerListPlaceholder.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
            } else {
                binding.workerListPlaceholder.visibility = View.GONE
                binding.recyclerView.visibility = View.GONE
                binding.notFound.visibility = View.VISIBLE
            }
            adapter.setWorkers(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.action_nav4, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.about -> {
                AlertDialog.Builder(this)
                    .setTitle("App Version")
                    .setMessage("Beta 1.0.0")
                    .setCancelable(true)
                    .setPositiveButton("OK", null)
                    .create()
                    .show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onOptionClick(view: View, worker: User) {
        val prefs: SharedPreferences = getSharedPreferences("prefs", MODE_PRIVATE)
        val clientId: String = prefs.getString("client_id", "")!!
        val contextThemeWrapper = ContextThemeWrapper(this, R.style.MyPopupMenu)
        val popupMenu = PopupMenu(contextThemeWrapper, view, Gravity.END)
        popupMenu.menuInflater.inflate(R.menu.item_popup_action, popupMenu.menu)
        popupMenu.show()
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.act_edit -> {
                    val dialog = AddWorkerFragment()
                    val bundle = Bundle()
                    bundle.putString("status", "update")
                    bundle.putString("worker_name", worker.name)
                    bundle.putString("worker_email", worker.email)
                    dialog.arguments = bundle
                    dialog.show(supportFragmentManager, "BottomSheetDialog")
                }
                R.id.act_delete -> {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Are You Sure?")
                    builder.setMessage("This can be deleted permamently")
                    builder.setPositiveButton("YES") { _, _ ->
                        viewModel.deleteWorker(clientId, worker)
                    }
                    builder.setNegativeButton("NO") { dialog, _ ->
                        dialog.dismiss()
                    }
                    val alert = builder.create()
                    alert.show()
                }
            }
            false
        }
    }

    override fun onSuccess() {
        Toast.makeText(this, "Worker is deleted", Toast.LENGTH_SHORT).show()
    }

    override fun onFailure(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}