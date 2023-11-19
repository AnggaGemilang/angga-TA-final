package com.agrapana.fertigation.ui.fragment

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.agrapana.fertigation.databinding.FragmentAddFieldBinding
import com.agrapana.fertigation.helper.OperationListener
import com.agrapana.fertigation.model.Field
import com.agrapana.fertigation.ui.activity.ScannerActivity
import com.agrapana.fertigation.viewmodel.FieldViewModel
import com.agrapana.fertigation.viewmodel.PresetViewModel
import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialogFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.zxing.integration.android.IntentIntegrator
import org.json.JSONException
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class AddFieldFragment : RoundedBottomSheetDialogFragment(), OperationListener {

    private lateinit var viewModel: FieldViewModel
    private lateinit var binding: FragmentAddFieldBinding
    private var hardwareCode: String? = null
    private var progressDialog: ProgressDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProviders.of(this)[FieldViewModel::class.java]
        viewModel.operationListener = this
        binding = FragmentAddFieldBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(arguments?.getString("status") == "update"){
            binding.title.text = "Edit Field"
            binding.btnSubmit.text = "Edit Field"
            binding.numberOfMonitorDevice.isEnabled = false
            binding.syncHardware.visibility = View.GONE
            binding.fieldName.text = Editable.Factory.getInstance().newEditable(arguments?.getString("field_name"))
            binding.fieldAddress.text = Editable.Factory.getInstance().newEditable(arguments?.getString("field_address"))
            binding.fieldArea.text = Editable.Factory.getInstance().newEditable(arguments?.getString("field_area"))
            binding.numberOfMonitorDevice.text = Editable.Factory.getInstance().newEditable(arguments?.getString("number_of_monitor_device"))
        } else {
            binding.numberOfMonitorDevice.text = Editable.Factory.getInstance().newEditable("1")
        }

        binding.open.setOnClickListener {
            openCamera()
        }

        binding.btnSubmit.setOnClickListener {
            val prefs: SharedPreferences = activity!!.getSharedPreferences("prefs",
                AppCompatActivity.MODE_PRIVATE
            )
            val userId: String = prefs.getString("client_id", "")!!

            val fieldName = binding.fieldName.text.toString().trim()
            val fieldAddress = binding.fieldAddress.text.toString().trim()
            val fieldArea = binding.fieldArea.text.toString().trim()
            val numberOfMonitorDevice = binding.numberOfMonitorDevice.text.toString().trim()
            val field = Field()
            field.name = fieldName
            field.address = fieldAddress
            field.land_area = fieldArea
            field.number_of_monitor_device = numberOfMonitorDevice.toInt()

            if(arguments?.getString("status") == "update") {
                progressDialog = ProgressDialog(requireContext())
                progressDialog!!.setTitle("Please Wait")
                progressDialog!!.setMessage("System is working . . .")
                progressDialog!!.show()
                field.id = arguments?.getString("id")!!
                field.hardware_code = arguments?.getString("hardware_code")!!
                field.created_at = arguments?.getString("created_at")!!
                viewModel.onUpdatePreset(userId, field)
            } else {
                if(hardwareCode == null){
                    Toast.makeText(context, "Fill all blanks input", Toast.LENGTH_SHORT).show()
                } else {
                    progressDialog = ProgressDialog(requireContext())
                    progressDialog!!.setTitle("Please Wait")
                    progressDialog!!.setMessage("System is working . . .")
                    progressDialog!!.show()
                    field.created_at = DateTimeFormatter
                        .ofPattern("yyyy-MM-dd HH:mm")
                        .withZone(ZoneOffset.UTC)
                        .format(Instant.now())
                    field.hardware_code = hardwareCode!!
                    viewModel.onAddPreset(userId, field)
                }
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

    override fun onSuccess() {
        progressDialog!!.dismiss()
        this.dismiss()
        if(arguments?.getString("status") == "update") {
            Toast.makeText(context, "Field has updated successfully", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Field has added successfully", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onFailure(message: String) {
        progressDialog!!.dismiss()
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

}