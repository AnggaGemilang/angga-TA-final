package com.agrapana.fertigation.ui.fragment

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.agrapana.fertigation.databinding.FragmentAddFieldBinding
import com.agrapana.fertigation.model.Field
import com.agrapana.fertigation.ui.activity.ScannerActivity
import com.agrapana.fertigation.viewmodel.FieldViewModel
import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.zxing.integration.android.IntentIntegrator
import org.json.JSONException

class AddFieldFragment : RoundedBottomSheetDialogFragment() {

    private lateinit var viewModel: FieldViewModel
    private lateinit var binding: FragmentAddFieldBinding
    private var hardwareCode: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProviders.of(this)[FieldViewModel::class.java]
        binding = FragmentAddFieldBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(arguments?.getString("status") == "update"){
            binding.title.text = "Edit Field"
            binding.btnSubmit.text = "Edit Field"
            binding.fieldName.text = Editable.Factory.getInstance().newEditable(arguments?.getString("field_name"))
            binding.fieldAddress.text = Editable.Factory.getInstance().newEditable(arguments?.getString("field_address"))
            binding.fieldArea.text = Editable.Factory.getInstance().newEditable(arguments?.getString("field_area"))
            binding.numberOfMonitorDevice.text = Editable.Factory.getInstance().newEditable(arguments?.getString("number_of_monitor_device"))
        }

        binding.open.setOnClickListener {
            openCamera()
        }

        binding.btnSubmit.setOnClickListener {
            val progressDialog = ProgressDialog(requireContext())
            progressDialog.setTitle("Please Wait")
            progressDialog.setMessage("System is working . . .")
            progressDialog.show()

            if(arguments?.getString("status") == "update") {
                val fieldName = binding.fieldName.text.toString().trim()
                val fieldAddress = binding.fieldAddress.text.toString().trim()
                val fieldArea = binding.fieldArea.text.toString().trim()
                val numberOfMonitorDevice = binding.numberOfMonitorDevice.text.toString().trim()
                val field = Field()
                field.id = arguments?.getString("id")!!
                field.name = fieldName
                field.address = fieldAddress
                field.land_area = fieldArea
                field.number_of_monitor_device = numberOfMonitorDevice.toInt()
            } else {

            }
        }
    }

    private fun openCamera() {
        val qrScan = IntentIntegrator.forSupportFragment(this)
        qrScan.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        qrScan.setPrompt("Scan a QR Code")
        qrScan.captureActivity = ScannerActivity::class.java
        qrScan.setOrientationLocked(false)
        qrScan.setBeepEnabled(false)
        qrScan.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null)
        {
            if (result.contents == null){
                Toast.makeText(requireContext(), "QR Code Isn't Valid", Toast.LENGTH_LONG).show()
            } else {
                try {
                    val contents = result.contents
                    val separatedContents = contents.split("&").toTypedArray()
                    if(separatedContents.isEmpty() || separatedContents.size > 2 || separatedContents[0] != "fertigation_kota203"){
                        Toast.makeText(requireContext(), "QR Code Isn't Valid", Toast.LENGTH_LONG).show()
                    } else {
                        binding.txtFilename.text = if(separatedContents[1].length > 15) separatedContents[1].substring(0, 15) + "..." else separatedContents[1]
                        hardwareCode = separatedContents[1]
                    }
                }
                catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(requireContext(), result.contents, Toast.LENGTH_LONG).show()
                }
            }
        } else {
            Toast.makeText(requireContext(), "QR Code Isn't Valid", Toast.LENGTH_LONG).show()
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onStart() {
        super.onStart()
        val behavior = BottomSheetBehavior.from(requireView().parent as View)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

}