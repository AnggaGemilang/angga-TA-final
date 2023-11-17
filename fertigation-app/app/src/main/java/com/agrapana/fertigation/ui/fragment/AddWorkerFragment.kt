package com.agrapana.fertigation.ui.fragment

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
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
import androidx.lifecycle.ViewModelProviders
import com.agrapana.fertigation.databinding.FragmentAddPresetBinding
import com.agrapana.fertigation.databinding.FragmentAddWorkerBinding
import com.agrapana.fertigation.model.Preset
import com.agrapana.fertigation.model.User
import com.agrapana.fertigation.viewmodel.PresetViewModel
import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class AddWorkerFragment : RoundedBottomSheetDialogFragment() {

    private lateinit var viewModel: PresetViewModel
    private lateinit var binding: FragmentAddWorkerBinding
    private var linkImage: Uri? = null
    private val GALLERY_REQUEST_CODE = 999

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProviders.of(this)[PresetViewModel::class.java]
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
            val progressDialog = ProgressDialog(requireContext())
            progressDialog.setTitle("Please Wait")
            progressDialog.setMessage("System is working . . .")
            progressDialog.show()
        }
    }

    override fun onStart() {
        super.onStart()
        val behavior = BottomSheetBehavior.from(requireView().parent as View)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

}