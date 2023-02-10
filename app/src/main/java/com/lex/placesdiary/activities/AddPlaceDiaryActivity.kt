package com.lex.placesdiary.activities

import android.app.Activity
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
import com.lex.placesdiary.R
import com.lex.placesdiary.database.DatabaseHelper
import com.lex.placesdiary.databinding.ActivityAddPlaceDiaryBinding
import com.lex.placesdiary.models.PlacesDiaryModel
import java.text.SimpleDateFormat
import java.util.*

class AddPlaceDiaryActivity : AppCompatActivity(), View.OnClickListener {

    private var binding : ActivityAddPlaceDiaryBinding? = null

    private var cal = Calendar.getInstance()
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener

    private var mLatitude : Double = 0.0
    private var mLongitude : Double = 0.0

    private var mPlacesDiaryDetails : PlacesDiaryModel? = null

    private var saveImageToStorageUri: Uri? = null
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

        //Check if an intent Extra has been passed and then pass as the model if true
        if (intent.hasExtra(MainActivity.EXTRA_PLACE_DETAILS)){
            mPlacesDiaryDetails = intent.getSerializableExtra(MainActivity.EXTRA_PLACE_DETAILS) as PlacesDiaryModel
        }

        dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            updateDateInView()
        }

        updateDateInView()

        //Update the addPlace fields to update fields with existing entry if not null
        if (mPlacesDiaryDetails != null){
            supportActionBar?.title = "Edit Place Diary"

            binding?.etTitlePlace?.setText(mPlacesDiaryDetails!!.title)
            binding?.etDescriptionPlace?.setText(mPlacesDiaryDetails!!.description)
            binding?.etDatePlace?.setText(mPlacesDiaryDetails!!.date)
            binding?.etLocationPlace?.setText(mPlacesDiaryDetails!!.location)
            mLatitude = mPlacesDiaryDetails!!.latitude
            mLongitude = mPlacesDiaryDetails!!.longitude

            saveImageToStorageUri = Uri.parse(mPlacesDiaryDetails!!.image)
            binding?.ivAddImage?.setImageURI(saveImageToStorageUri)

            binding?.btnSave?.text = "UPDATE"
        }

        binding?.etDatePlace?.setOnClickListener(this)
        binding?.tvAddImage?.setOnClickListener(this)
        binding?.btnSave?.setOnClickListener(this)
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
                        0 -> {
                            addPhotoFromStorageOrCamera(which)
                        }
                        1 -> {
                            addPhotoFromStorageOrCamera(which)
                        }
                    }
                }
                pictureDialog.show()
            }

            binding?.btnSave ->{
                if (binding?.etTitlePlace?.text?.isEmpty() == true){
                    Toast.makeText(this, "Please enter title", Toast.LENGTH_SHORT).show()
                }else if (binding?.etDescriptionPlace?.text?.isEmpty() == true){
                    Toast.makeText(this, "Please enter description", Toast.LENGTH_SHORT).show()
                }else if (binding?.etLocationPlace?.text?.isEmpty() == true){
                    Toast.makeText(this, "Please enter location", Toast.LENGTH_SHORT).show()
                }else if (saveImageToStorageUri == null){
                    Toast.makeText(this, "Please add image!", Toast.LENGTH_SHORT).show()
                }else{
                    val placeDiaryModel = PlacesDiaryModel(
                        if (mPlacesDiaryDetails == null) 0 else mPlacesDiaryDetails!!.id,
                        binding?.etTitlePlace?.text!!.toString(),
                        binding?.etDescriptionPlace?.text!!.toString(),
                        saveImageToStorageUri.toString(),
                        binding?.etDatePlace?.text!!.toString(),
                        binding?.etLocationPlace?.text!!.toString(),
                        mLatitude,
                        mLongitude
                    )
                    val dbHelper = DatabaseHelper(this)

                    //Check to determine if this is a update or save new request
                    if (mPlacesDiaryDetails == null){
                        val addPlaceDiary = dbHelper.addPlaceDiary(placeDiaryModel)

                        if (addPlaceDiary > 0){
                            setResult(Activity.RESULT_OK)
                            finish()
                        }else{
                            Toast.makeText(this, "The place diary details failed to saved! $addPlaceDiary", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        val updatePlaceDiary = dbHelper.updatePlaceDiary(placeDiaryModel)
                        if (updatePlaceDiary > 0){
                            setResult(Activity.RESULT_OK)
                            finish()
                        }else{
                            Toast.makeText(this, "The place diary details failed to update!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
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

    /*private fun choosePhotoFromGallery() {
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
    }*/

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

    private fun addPhotoFromStorageOrCamera(whichSelected: Int){
        val imagePicker = ImagePicker.with(this)
                if (whichSelected == 0){
                    imagePicker.galleryOnly()
                        .compress(2048)
                        .crop()
                        //  Path: /storage/sdcard0/Android/data/com.lex.placesdiary/files/Pictures
                        .saveDir(getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!)
                        .start()
                }else{
                    imagePicker.cameraOnly()	//User can only capture image using Camera
                        .compress(2048)
                        .crop()
                        //  Path: /storage/sdcard0/Android/data/com.lex.placesdiary/files/Pictures
                        .saveDir(getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!)
                        .start(CAMERA_REQUEST_CODE)
                }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                //Image Uri will not be null for RESULT_OK
                val uri: Uri = data?.data!!

                saveImageToStorageUri = uri
                Log.e("SAVED TO ", uri.toString())

                binding?.ivAddImage?.setImageURI(uri)
            }
            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }
}