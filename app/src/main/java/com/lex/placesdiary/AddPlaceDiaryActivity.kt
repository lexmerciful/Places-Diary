package com.lex.placesdiary

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import com.github.dhaval2404.imagepicker.ImagePicker
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.lex.placesdiary.databinding.ActivityAddPlaceDiaryBinding
import java.text.SimpleDateFormat
import java.util.*

class AddPlaceDiaryActivity : AppCompatActivity(), View.OnClickListener {

    private var binding : ActivityAddPlaceDiaryBinding? = null

    private var cal = Calendar.getInstance()
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener

    companion object{
        private const val CAMERA_REQUEST_CODE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPlaceDiaryBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbarAddPlace)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding?.toolbarAddPlace?.setNavigationOnClickListener {
            onBackPressed()
        }

        dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            updateDateInView()
        }
        binding?.etDatePlace?.setOnClickListener(this)
        binding?.tvAddImage?.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when(view){
            binding?.etDatePlace ->{
                DatePickerDialog(this@AddPlaceDiaryActivity, dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
            }

            binding?.tvAddImage ->{
                val pictureDialog = AlertDialog.Builder(this)
                pictureDialog.setTitle("Select Action")
                val pictureDialogItems = arrayOf("Select photo from gallery",
                    "Capture photo from camera")
                pictureDialog.setItems(pictureDialogItems){ dialog, which ->
                    when(which){
                        0 -> choosePhotoFromGallery()
                        1 -> capturePhotoFromCamera()
                    }
                }
                pictureDialog.show()
            }
        }
    }

    /*private fun capturePhotoFromCamera() {
        Dexter.withActivity(this).withPermission(
            Manifest.permission.CAMERA
        ).withListener(object : PermissionListener {
            override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivity(intent)
            }

            override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                Toast.makeText(this@AddPlaceDiaryActivity,"Camera permission is not granted",Toast.LENGTH_SHORT).show()
            }

            override fun onPermissionRationaleShouldBeShown(
                permission: PermissionRequest?,
                token: PermissionToken?
            ) {
                //token!!.continuePermissionRequest();
                showRationaleDialogForPermission()
            }

        }).onSameThread().check()
    }*/

    private fun choosePhotoFromGallery() {
        Dexter.withActivity(this).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if (report!!.areAllPermissionsGranted()){
                    Toast.makeText(this@AddPlaceDiaryActivity,"Storage READ/WRITE granted",Toast.LENGTH_SHORT).show()
                }
            }
            override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest> , token: PermissionToken)
            {
                showRationaleDialogForPermission()
            }
        }).onSameThread().check()
    }

    private fun showRationaleDialogForPermission() {
        val dialog = AlertDialog.Builder(this)
        dialog.setMessage("It seems you've declined permission required for this feature. It can be enabled in the Application Settings")
        dialog.setPositiveButton("Go To Settings"){
            _, _ ->
            //Open Application Settings
            try {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }catch (e:ActivityNotFoundException){
                e.printStackTrace()
            }
        }
        dialog.setNegativeButton("Cancel"){dialog,_ ->
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun updateDateInView(){
        val myFormat = "dd-MM-yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        binding?.etDatePlace?.setText(sdf.format(cal.time).toString())
    }

    private fun capturePhotoFromCamera(){
        ImagePicker.with(this)
            .cameraOnly()	//User can only capture image using Camera
            .compress(2048)
            .crop()
            .setImageProviderInterceptor { imageProvider -> //Intercept ImageProvider
                Log.e("ImagePicker", "Selected ImageProvider: "+imageProvider.name)
            }
            //  Path: /storage/sdcard0/Android/data/package/files/Pictures
            .saveDir(getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!)
            .start(CAMERA_REQUEST_CODE)
    }
}