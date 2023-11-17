package com.agrapana.fertigation.ui.fragment

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.agrapana.fertigation.databinding.FragmentAddPresetBinding
import com.agrapana.fertigation.databinding.FragmentAddWorkerBinding
import com.agrapana.fertigation.helper.AuthListener
import com.agrapana.fertigation.helper.WorkerListener
import com.agrapana.fertigation.model.AuthResponse
import com.agrapana.fertigation.model.Preset
import com.agrapana.fertigation.model.User
import com.agrapana.fertigation.viewmodel.AuthViewModel
import com.agrapana.fertigation.viewmodel.PresetViewModel
import com.agrapana.fertigation.viewmodel.WorkerViewModel
import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class AddWorkerFragment : RoundedBottomSheetDialogFragment(), WorkerListener {

    private lateinit var viewModel: WorkerViewModel
    private lateinit var binding: FragmentAddWorkerBinding
    private var progressDialog: ProgressDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProviders.of(this)[WorkerViewModel::class.java]
        viewModel.workerListener = this
        binding = FragmentAddWorkerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(arguments?.getString("status") == "update"){
            binding.title.text = "Edit Worker"
            binding.btnSubmit.text = "Edit Worker"
            binding.workerName.text = Editable.Factory.getInstance().newEditable(arguments?.getString("plantName"))
            binding.workerEmail.text = Editable.Factory.getInstance().newEditable(arguments?.getString("temperature"))
            binding.workerPassword.text = Editable.Factory.getInstance().newEditable(arguments?.getString("seedlingTime"))
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

            val prefs: SharedPreferences = activity!!.getSharedPreferences("prefs",
                AppCompatActivity.MODE_PRIVATE
            )
            val userId: String = prefs.getString("client_id", "")!!
            val name = binding.workerName.text.toString()
            val email = binding.workerEmail.text.toString()
            val password = binding.workerPassword.text.toString()
            viewModel.onAddWorker(userId, name, email, password)
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