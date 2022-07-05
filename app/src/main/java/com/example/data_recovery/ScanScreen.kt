package com.example.data_recovery

import android.annotation.SuppressLint
import android.app.RecoverableSecurityException
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.example.data_recovery.Constants.Common
import com.example.data_recovery.adopters.DirectoriesAdopter
import com.example.data_recovery.databinding.ScanScreenBinding
import com.example.data_recovery.model.DirList
import com.example.data_recovery.model.DirectoriesModel
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.channels.FileChannel

class ScanScreen : AppCompatActivity() {
    lateinit var binding: ScanScreenBinding

    lateinit var directoryAdaptor: DirectoriesAdopter
    lateinit var imagesList: DirList
    val deletedImagesList: MutableList<DirectoriesModel> = arrayListOf()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.scan_screen)

        imagesList = intent.getSerializableExtra("dataList") as DirList

        Log.e("TAG", "The data is ${Common.imageslist}")

//        val imagesList: ArrayList<DirectoriesModel>? = data!!.getParcelable<Parcelable>("dataList") as ArrayList<DirectoriesModel>?
//        Log.e("TAG", "onCreate:  the list data is ${imagesList.toString()}", )

//
        directoryAdaptor =
            DirectoriesAdopter({ directoryModel, index -> imageItemOnClick(directoryModel, index) },
                { directoryModel, index, isSelected ->
                    selectorOnClick(
                        directoryModel,
                        index,
                        isSelected
                    )
                })

        binding.deleteBtn.setOnClickListener() {
            deletedImagesList.forEach { imageToDelete ->
                imageToDelete.image?.let { imageToDeletePath ->
                    Log.e(
                        "TAG",
                        "onCreate: The path of the file to be deleted is ${File(imageToDeletePath)}"
                    )
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                        val uris = arrayListOf<Uri?>()
                        val uriOfCurrentIndex: Uri? = getImageUri(imageToDeletePath)
                        if (uriOfCurrentIndex != null) {
                            uris.add(uriOfCurrentIndex)
                            val intentSender = when {
                                Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                                    MediaStore.createDeleteRequest(contentResolver, uris).intentSender
                                }
                                else -> null
                            }
                            intentSender?.let { sender ->
                                intentSenderLauncher.launch(
                                    IntentSenderRequest.Builder(sender).build()
                                )
                            }

                        }
                    }else{
                        File(imageToDeletePath).delete()
                        finish()
                    }

                    if (!File(imageToDeletePath).exists()) {
                        imagesList.directories.removeAt(imagesList.directories.indexOf(imagesList.directories.find { currentImage -> currentImage.image == imageToDeletePath }))
                        Log.e(
                            "TAG",
                            "onCreate: The list coming after the deletion procedure is ${imagesList}",
                        )


                    }
                }
            }

            deletedImagesList.clear()
            if (this@ScanScreen::directoryAdaptor.isInitialized) {
                directoryAdaptor.submitList(imagesList.directories)
                directoryAdaptor.notifyDataSetChanged()
            }
        }
        binding.recoverBtn.setOnClickListener() {
            imagesList.directories.filter { it.isSelected }.forEach {
                it.isSelected = false
            }
            directoryAdaptor.submitList(imagesList.directories)
            directoryAdaptor.notifyDataSetChanged()

            deletedImagesList.forEach { imageToRecover ->
                imageToRecover.image?.let { imageToRecoverPath ->
                    Log.e(
                        "TAG",
                        "onCreate: The path of the file to be deleted is ${File(imageToRecoverPath)}"
                    )
                    copyFile(
                        File(imageToRecoverPath), File(
                            Environment.getExternalStorageDirectory()
                                .getAbsolutePath() + "/Recoverd Photos/${File(imageToRecoverPath).name}"
                        )

                    )

                }
            }
            deletedImagesList.clear()
            Toast.makeText(this@ScanScreen, "Images recovered successfully", Toast.LENGTH_SHORT).show()
        }

        val layoutManager = GridLayoutManager(this@ScanScreen, 3)
        binding.directoryID2.layoutManager = layoutManager
        binding.directoryID2.adapter = directoryAdaptor

        directoryAdaptor.submitList(Common.imageslist)
        directoryAdaptor.notifyDataSetChanged()


    }


    private fun imageItemOnClick(directoriesModel: DirectoriesModel, index: Int) {
        Log.e(
            "TAG",
            "imageItemOnClick: clicked image path is --> ${directoriesModel.image} and index is $index",
        )
        val b = Bundle().apply {
            putSerializable(DIRECTORY_MODEL_ARGS, directoriesModel)
        }
        startActivityForResult(
            Intent(this, ImageView::class.java).putExtras(b).putExtra("index", index), 101
        )
    }

    private fun selectorOnClick(
        directoriesModel: DirectoriesModel,
        index: Int,
        isSelected: Boolean
    ) {
        if (isSelected) {
            deletedImagesList.add(
                DirectoriesModel(
                    name = directoriesModel.name,
                    image = directoriesModel.image,
                    isSelected = directoriesModel.isSelected
                )
            )
            Log.e(
                "TAG",
                "selectorOnClick: The images added in the model are as ${deletedImagesList}"
            )
        } else {
            deletedImagesList.remove(
                DirectoriesModel(
                    name = directoriesModel.name,
                    image = directoriesModel.image,
                    isSelected = directoriesModel.isSelected

                )
            )
            Log.e(
                "TAG",
                "selectorOnClick: The images removed in the model are as ${deletedImagesList}"
            )

        }
        Log.e(
            "TAG",
            "imageItemOnClick: clicked image index is --> $index and the selection value is $isSelected",
        )

    }

    @Deprecated("Deprecated in Java")
    @SuppressLint("NotifyDataSetChanged")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e("TAG", "onActivityResult: The value of the request code is${requestCode}")
        Log.e("TAG", "onActivityResult: The Value of the reult code is ${resultCode}")
        if (requestCode == 101) {
            val deletedIndex: Int = data?.extras?.getInt("Deleted_Index") ?: -1
            Log.e("TAG", "onActivityResult: THe coming index is ${deletedIndex}")
            if (deletedIndex > -1) {
                Log.e("TAG", "onActivityResult: The Value of the code is ${requestCode}")
                imagesList.directories.removeAt(deletedIndex)
                Log.e("TAG", "onActivityResult: The latest image list is ${imagesList}")
                if (this@ScanScreen::directoryAdaptor.isInitialized) {
                    directoryAdaptor.submitList(imagesList.directories)
                    directoryAdaptor.notifyDataSetChanged()
//                    directoryAdaptor.notifyItemChanged(deletedIndex)
                }
            }
        }
    }

    companion object {
        val DIRECTORY_MODEL_ARGS = "DIRECTORY_MODEL_ARGS"
    }

    @Throws(IOException::class)
    fun copyFile(sourceFile: File?, destFile: File) {

        if (!destFile.parentFile.exists()) destFile.parentFile.mkdirs()
//        Log.e("TAG", "copyFile: Directory created successfully ${destFile.parentFile?.mkdirs()}", )
        if (!destFile.exists()) {
            Log.e("TAG", "copyFile: The path of the destination file is ${destFile.absolutePath} ")
            destFile.createNewFile()
            Log.e("TAG", "copyFile: New Created File is ${destFile.createNewFile()}")
        }


        var source: FileChannel? = null
        var destination: FileChannel? = null
        try {
            source = FileInputStream(sourceFile).getChannel()
            destination = FileOutputStream(destFile).getChannel()
            destination.transferFrom(source, 0, source.size())
        } finally {
            if (source != null) {
                source.close()
            }
            if (destination != null) {
                destination.close()
            }
        }
    }
    fun getImageUri(
        path: String
    ): Uri? {
        try {
            val checkFile = File(path)
            ///  Timber.e("checkDelete- $checkFile")
            if (checkFile.exists()) {
                var id: Long
                val cr: ContentResolver = contentResolver!!
                val selection = MediaStore.Images.Media.DATA
                val selectionArgs = arrayOf<String>(checkFile.absolutePath)
                val projection = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA)
                val cursor = cr.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                    "$selection=?", selectionArgs, null
                )

                if (cursor != null) {
                    while (cursor.moveToNext()) {

                        val idIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                        id = cursor.getString(idIndex).toLong()
                        try {
                            val photoUri: Uri = ContentUris.withAppendedId(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                id
                            )
                            return photoUri
                        } catch (securityException: SecurityException) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                val recoverableSecurityException =
                                    securityException as? RecoverableSecurityException
                                        ?: throw securityException
                                recoverableSecurityException.userAction.actionIntent.intentSender
                            } else {
                                throw securityException
                            }
                        }


                    }
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return null
    }



    val intentSenderLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    finish()
                }
            }
        }




}