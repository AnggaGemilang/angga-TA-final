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
import com.agrapana.fertigation.helper.OperationListener
import com.agrapana.fertigation.model.User
import com.agrapana.fertigation.model.Worker
import com.agrapana.fertigation.ui.fragment.AddWorkerFragment
import com.agrapana.fertigation.viewmodel.WorkerViewModel
import org.imaginativeworld.oopsnointernet.NoInternetDialog

class WorkerActivity : AppCompatActivity(), WorkerAdapter.TaskListener, OperationListener {

    private lateinit var binding: ActivityWorkerBinding
    private lateinit var viewModel: WorkerViewModel
    private val adapter = WorkerAdapter(this)
    private var noInternetDialog: NoInternetDialog? = null

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
        viewModel.operationListener = this
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
        menuInflater.inflate(R.menu.action_nav3, menu)
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

    override fun onOptionClick(view: View, worker: Worker) {
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
                    bundle.putString("worker_field_id", worker.fieldId)
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

    override fun onResume() {
        super.onResume()
        val builder1 = NoInternetDialog.Builder(this)
        builder1.cancelable = false // Optional
        builder1.noInternetConnectionTitle = "No Internet" // Optional
        builder1.noInternetConnectionMessage = "Check your Internet connection and try again" // Optional
        builder1.showInternetOnButtons = true // Optional
        builder1.pleaseTurnOnText = "Please turn on" // Optional
        builder1.wifiOnButtonText = "Wifi" // Optional
        builder1.mobileDataOnButtonText = "Mobile data" // Optional
        builder1.onAirplaneModeTitle = "No Internet" // Optional
        builder1.onAirplaneModeMessage = "You have turned on the airplane mode." // Optional
        builder1.pleaseTurnOffText = "Please turn off" // Optional
        builder1.airplaneModeOffButtonText = "Airplane mode" // Optional
        builder1.showAirplaneModeOffButtons = true // Optional
        noInternetDialog = builder1.build()
    }

    override fun onPause() {
        super.onPause()
        if (noInternetDialog != null) {
            noInternetDialog!!.destroy();
        }
    }

}