package com.example.data_recovery

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.os.storage.StorageManager
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.example.data_recovery.Constants.Common
import com.example.data_recovery.databinding.CardScreenBinding
import com.example.data_recovery.model.DirList
import com.example.data_recovery.model.DirectoriesModel
import com.example.tessst.StorageUtil
import kotlinx.coroutines.*
import java.io.File


class CardsScreen : AppCompatActivity() {


    lateinit var binding: CardScreenBinding
    var genericCounter = 0
    var imageCounter = 0
    var imagesNameList: MutableList<DirectoriesModel> = arrayListOf()



    var job: Job? = null
    private lateinit var storage: File
    private var storagePaths: List<String?> = arrayListOf()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.card_screen)
        binding.viewBtn.setOnClickListener(){
            val temp= arrayListOf<DirectoriesModel>()
            imagesNameList.forEach {
                temp.add(it)
            }
            Common.imageslist=temp
            val b = Bundle().apply {
                putSerializable ("dataList", DirList(directories = imagesNameList))
            }

            startActivity(Intent(this, ScanScreen::class.java).putExtras(b))
        }

        binding.scanBtn.setOnClickListener() {
            binding.scanBtn.visibility = View.GONE
            imageCounter = 0
            genericCounter = 0
            if (job?.isActive == true) {
                job?.cancel()
            }
            else {
                if (!checkPermissionForReadExternalStorage()) {
                    requestPermissionForReadExtertalStorage()
                    requestSDCardPermissions()
                } else {

                    scanFunction()
                }

            }
        }
    }

    fun isImage(file: File): Boolean {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        val bitmap = BitmapFactory.decodeFile(file.path, options)
        if (options.outWidth != -1 && options.outHeight != -1) {
            return true
        } else {
            return false
        }
    }

    private suspend fun searchDir(dir: File) {

        val fileList = dir.listFiles()
        if (fileList != null) {
            for (i in fileList.indices) {
                if (fileList[i].isDirectory) {
//                    if (imageCounter > 100) {
//                        return
//                    } else {
                        searchDir(fileList[i])
//                    }
                }
                else {
                    genericCounter++
                    if (isImage(fileList[i]))
                    {
                        Log.e("TAG", "searchDir: fileList[i].absolutePath", )
                         imageCounter++
                        imagesNameList.add(
                            DirectoriesModel(
                                name = fileList[i].name,
                                image = fileList[i].absolutePath
                            )
                        )
                    }
                    withContext(Dispatchers.Main) {
                        binding.imageCounter.text = imageCounter.toString()
                    }

                }
            }
        }
        else {
            Log.e("TAG", "searchDir: File list is coming null")
        }

    }


    private val WRITE_STORAGE_PERMISSION_REQUEST_CODE = 41
    private fun checkPermissionForReadExternalStorage(): Boolean {
        Log.e("TAG", "READ_STORAGE_PERMISSION_REQUEST_CODE: This function is called")
        if (SDK_INT >= Build.VERSION_CODES.M) {
            val result: Int =
                applicationContext.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            return result == PackageManager.PERMISSION_GRANTED

        }
        return false
    }

    @Throws(Exception::class)
    fun requestPermissionForReadExtertalStorage() {
        try {
            ActivityCompat.requestPermissions(
                this@CardsScreen, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                WRITE_STORAGE_PERMISSION_REQUEST_CODE,
            )
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    fun scanFunction() {

        job = lifecycleScope.launch(Dispatchers.IO) {

            storagePaths = StorageUtil.getStorageDirectories(this@CardsScreen)
            storagePaths.forEach {
                it?.let { path ->
                    storage = File(path)
                    searchDir(storage)
                }
            }
            withContext(Dispatchers.Main) {
                binding.imageCounter.text = imageCounter.toString()
                binding.viewBtn.visibility = View.VISIBLE
            }
            Log.e("TAG", "Generic Counter is: $genericCounter")
        }
    }

    private var REQ_PICK_DIRECTORY = 21
    private var REQ_SD_CARD_ACCESS = 22

    private var sdCardUri: Uri? = null

    private fun requestSDCardPermissions() {
        if (SDK_INT < 24) {
            startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT_TREE), REQ_PICK_DIRECTORY)
            return
        }
        // find removable device using getStorageVolumes
        val sm = getSystemService(Context.STORAGE_SERVICE) as StorageManager
        val sdCard = sm.storageVolumes.find { it.isRemovable }
        if (sdCard != null) {
            startActivityForResult(sdCard.createAccessIntent(null), REQ_SD_CARD_ACCESS)
        }
        if (SDK_INT >= 30) {
            if (!Environment.isExternalStorageManager()) {
//                Snackbar.make(
//                    findViewById(android.R.id.content),
//                    "Permission needed!",
//                    Snackbar.LENGTH_INDEFINITE
////                )
//                    .setAction("Settings")
//                {
                        try {
                            val uri =
                                Uri.parse("package:" + BuildConfig.APPLICATION_ID)
                            val intent =
                                Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri)
                            startActivity(intent)
                        } catch (ex: java.lang.Exception) {
                            val intent = Intent()
                            intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                            startActivity(intent)
                        }

//                    .show()
//            }
                        }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQ_SD_CARD_ACCESS || requestCode == REQ_PICK_DIRECTORY) {
            if (resultCode == RESULT_OK) {
                if (data == null) {
                    Log.e("TAG", "Error obtaining access")
                } else {
                    sdCardUri = data.data
                    Log.d("StorageAccess", "obtained access to $sdCardUri")
                    scanFunction()
                    // optionally store uri in preferences as well here { ... }
                }
            } else
                Toast.makeText(this, "access denied", Toast.LENGTH_SHORT).show()
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            when (requestCode) {
                WRITE_STORAGE_PERMISSION_REQUEST_CODE -> {
                    scanFunction()
                }
                REQ_PICK_DIRECTORY -> {
                    (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    scanFunction()
                }
                REQ_SD_CARD_ACCESS -> {
                    scanFunction()
                }
            }
        } else {
            binding.scanBtn.visibility= View.VISIBLE
            Log.e("value", "Permission Denied, You cannot use local drive .")
        }
    }


}