package com.agrapana.fertigation.ui.fragment

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModelProviders
import com.agrapana.fertigation.R
import com.agrapana.fertigation.adapter.FieldAdapter
import com.agrapana.fertigation.databinding.FragmentFieldBinding
import com.agrapana.fertigation.helper.OperationListener
import com.agrapana.fertigation.model.Field
import com.agrapana.fertigation.ui.activity.SettingActivity
import com.agrapana.fertigation.viewmodel.FieldViewModel

class FieldFragment : Fragment(), FieldAdapter.TaskListener, OperationListener {

    private lateinit var binding: FragmentFieldBinding
    private lateinit var adapter: FieldAdapter
    private lateinit var viewModel: FieldViewModel
    private lateinit var prefs: SharedPreferences
    private lateinit var window: Window

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFieldBinding.inflate(inflater, container, false)
        viewModel = ViewModelProviders.of(this)[FieldViewModel::class.java]
        viewModel.operationListener = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = this.activity?.getSharedPreferences("prefs",
            AppCompatActivity.MODE_PRIVATE
        )!!
        binding.toolbar.inflateMenu(R.menu.action_nav2)
        val role: String? = prefs.getString("client_role", "")
        if(role == "Worker"){
            binding.toolbar.menu.findItem(R.id.add).isVisible = false
        }
        binding.toolbar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.add -> {
                    val dialog = AddFieldFragment()
                    val bundle = Bundle()
                    bundle.putString("status", "tambah")
                    dialog.arguments = bundle
                    activity?.let { it1 -> dialog.show(it1.supportFragmentManager, "BottomSheetDialog") }
                }
                R.id.setting -> {
                    startActivity(Intent(context, SettingActivity::class.java))
                }
                R.id.about -> {
                    AlertDialog.Builder(requireContext())
                        .setTitle("App Version")
                        .setMessage("Beta 1.0.0")
                        .setCancelable(true)
                        .setPositiveButton("OK", null)
                        .create()
                        .show()
                }
            }
            true
        }
        initViewModel()
        binding.recyclerView.adapter = adapter

        window = requireActivity().window
        binding.scrollView.setOnScrollChangeListener(
            NestedScrollView.OnScrollChangeListener {
                    _, _, scrollY, _, _ ->
                if(scrollY > 720){
                    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                } else {
                    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE;
                }
            }
        )
    }

    private fun initViewModel() {
        val ownerId: String = prefs.getString("client_id", "")!!
        val role: String = prefs.getString("client_role", "")!!
        adapter = FieldAdapter(activity!!, this)
        if(role == "Worker"){
            val fieldId: String = prefs.getString("worker_field_id", "")!!
            viewModel.fetchFieldsByWorker(ownerId, fieldId)
            viewModel.getRealtimeUpdatesByWorker(ownerId, fieldId)
        } else {
            viewModel.fetchFields(ownerId)
            viewModel.getRealtimeUpdates(ownerId)
        }
        viewModel.field.observe(viewLifecycleOwner) {
            Log.d("sabihis", "Kadieu Teuu")
            adapter.addField(it)
            binding.notFound.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }
        viewModel.deletedField.observe(viewLifecycleOwner) {
            adapter.deleteField(it)
        }
        viewModel.fields.observe(viewLifecycleOwner) {
            if (it!!.isNotEmpty()) {
                binding.valFieldListPlaceholder.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
            } else {
                binding.valFieldListPlaceholder.visibility = View.GONE
                binding.recyclerView.visibility = View.GONE
                binding.notFound.visibility = View.VISIBLE
            }
            adapter.setFieldList(it)
        }
    }

    override fun onOptionClick(view: View, field: Field) {
        val clientId: String = prefs.getString("client_id", "")!!
        val contextThemeWrapper = ContextThemeWrapper(context, R.style.MyPopupMenu)
        val popupMenu = PopupMenu(contextThemeWrapper, view, Gravity.END)
        popupMenu.menuInflater.inflate(R.menu.item_popup_action, popupMenu.menu)
        val role: String? = prefs.getString("client_role", "")
        if(role == "Worker"){
            popupMenu.menu.findItem(R.id.act_delete).isVisible = false
        }
        popupMenu.show()
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.act_edit -> {
                    val dialog = AddFieldFragment()
                    val bundle = Bundle()
                    bundle.putString("status", "update")
                    bundle.putString("field_name", field.name)
                    bundle.putString("field_address", field.address)
                    bundle.putString("preset_id", field.presetId)
                    bundle.putString("field_area", field.landArea)
                    bundle.putString("created_at", field.createdAt)
                    bundle.putString("hardware_code", field.hardwareCode)
                    bundle.putString("number_of_monitor_device", field.numberOfMonitorDevice.toString())
                    dialog.arguments = bundle
                    activity?.let { it1 -> dialog.show(it1.supportFragmentManager, "BottomSheetDialog") }
                }
                R.id.act_delete -> {
                    val builder = AlertDialog.Builder(requireContext())
                    builder.setTitle("Are You Sure?")
                    builder.setMessage("This can be deleted permamently")
                    builder.setPositiveButton("YES") { _, _ ->
                        viewModel.onDeleteField(clientId, field)
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
        Toast.makeText(requireContext(), "Field is deleted", Toast.LENGTH_SHORT).show()
    }

    override fun onFailure(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

}