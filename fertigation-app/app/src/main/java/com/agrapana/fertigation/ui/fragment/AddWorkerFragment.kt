package com.agrapana.fertigation.ui.fragment

import android.R
import android.app.ProgressDialog
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.agrapana.fertigation.databinding.FragmentAddWorkerBinding
import com.agrapana.fertigation.helper.OperationListener
import com.agrapana.fertigation.model.Field
import com.agrapana.fertigation.model.ParameterPreset
import com.agrapana.fertigation.viewmodel.FieldViewModel
import com.agrapana.fertigation.viewmodel.WorkerViewModel
import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior

class AddWorkerFragment : RoundedBottomSheetDialogFragment(), OperationListener {

    private lateinit var workerViewModel: WorkerViewModel
    private lateinit var fieldViewModel: FieldViewModel
    private lateinit var binding: FragmentAddWorkerBinding
    private var fields: List<Field> = emptyList()
    private lateinit var prefs: SharedPreferences
    private var fieldsName = mutableListOf<String>()
    private var fieldsId = mutableListOf<String>()
    private var progressDialog: ProgressDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fieldViewModel = ViewModelProviders.of(this)[FieldViewModel::class.java]
        workerViewModel = ViewModelProviders.of(this)[WorkerViewModel::class.java]
        workerViewModel.operationListener = this
        binding = FragmentAddWorkerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = activity!!.getSharedPreferences("prefs", AppCompatActivity.MODE_PRIVATE)
        val clientId: String = prefs.getString("client_id", "")!!

        fieldViewModel.fetchPresets(clientId)
        fieldViewModel.fields.observe(viewLifecycleOwner) {
            fieldsName.add("Choose Field")
            if (it!!.isNotEmpty()) {
                for (field in it) {
                    fieldsName.add(field.name)
                }
                for (field in it) {
                    fieldsId.add(field.id)
                }
                fields = it
            }
            val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, fieldsName)
            binding.fieldName.adapter = adapter
        }

        if(arguments?.getString("status") == "update"){
            binding.title.text = "Edit Worker"
            binding.btnSubmit.text = "Edit Worker"

            val oldFieldId = arguments?.getString("worker_field_id")!!
            binding.fieldName.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    if(position == 0){
                        parent.setSelection(fieldsId.indexOf(oldFieldId)+1)
                    }
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            binding.workerName.text = Editable.Factory.getInstance().newEditable(arguments?.getString("worker_name"))
            binding.workerEmail.text = Editable.Factory.getInstance().newEditable(arguments?.getString("worker_email"))
            binding.workerEmail.isEnabled = false
        }

        binding.workerPassword.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(p0!!.toString().length > 3 && p0.toString().contains("[a-z]".toRegex()) && p0.toString().contains("[0-9]".toRegex())
                    && p0.toString().contains("[!\"#\$%&'()*+,-./:;\\\\<=>?@\\[\\]^_`{|}~]".toRegex())){
                    binding.txtPasswordError.visibility = View.GONE
                } else {
                    binding.txtPasswordError.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        binding.btnSubmit.setOnClickListener {
            progressDialog = ProgressDialog(requireContext())
            progressDialog!!.setTitle("Please Wait")
            progressDialog!!.setMessage("System is working . . .")
            progressDialog!!.show()

            val userId: String = prefs.getString("client_id", "")!!
            val name = binding.workerName.text.toString()
            val email = binding.workerEmail.text.toString()
            val password = binding.workerPassword.text.toString()
            val fieldId = fields[fieldsName.indexOf(binding.fieldName.selectedItem.toString())-1].id

            if(arguments?.getString("status") == "update"){
                workerViewModel.onUpdateWorker(userId, name, email, password, fieldId)
            } else {
                workerViewModel.onAddWorker(userId, name, email, password, fieldId)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val behavior = BottomSheetBehavior.from(requireView().parent as View)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onSuccess() {
        progressDialog!!.dismiss()
        this.dismiss()
        if(arguments?.getString("status") == "update"){
            Toast.makeText(context, "Worker has updated successfully", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Preset has added successfully", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onFailure(message: String) {
        progressDialog!!.dismiss()
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

}