package com.example.data_recovery

import android.app.RecoverableSecurityException
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Intent
import android.media.MediaScannerConnection
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
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import com.example.data_recovery.ScanScreen.Companion.DIRECTORY_MODEL_ARGS
import com.example.data_recovery.databinding.ImageViewBinding
import com.example.data_recovery.model.DirectoriesModel
import java.io.*
import java.nio.channels.FileChannel


class ImageView : AppCompatActivity() {

    lateinit var binding: ImageViewBinding
    var deletedIndex = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.image_view)
        deletedIndex = intent.getIntExtra("index", 0)
        val imageData1: DirectoriesModel =
            intent.getSerializableExtra(DIRECTORY_MODEL_ARGS) as DirectoriesModel
        Log.e("TAG", "onCreate: Image that gets is $imageData1")

        imageData1.image?.let {
            binding.image.setImageURI(Uri.fromFile(File(it)))
        }
        binding.deleteBtn.setOnClickListener() {
            imageData1.image?.let {
                Log.e("TAG", "onCreate: The path of the image is ${it}")

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val uris = arrayListOf<Uri?>()
                    val uriOfCurrentIndex: Uri? = getImageUri(it)
                    if (uriOfCurrentIndex != null) {
                        uris.add(uriOfCurrentIndex)
                        var intentSender = when {
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

                } else {
                    File(it).delete()
                    setResult(RESULT_OK, Intent().putExtra("Deleted_Index", deletedIndex))
                    finish()

                }
            }
        }
        binding.recoverBtn.setOnClickListener() {


            imageData1.image?.let {
                var output = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                        .getAbsolutePath() + "/Recoverd Photos/${File(it).name}"
                )

                    if (!output.parentFile.exists()) output.parentFile.mkdirs()
//        Log.e("TAG", "copyFile: Directory created successfully ${destFile.parentFile?.mkdirs()}", )
                    if (!output.exists()) {
                        Log.e("TAG", "copyFile: The path of the destination file is ${output.absolutePath} ")
                        //    output.createNewFile()
                        //   Log.e("TAG", "copyFile: New Created File is ${output.createNewFile()}")
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

                        if (!output.exists()) {
                            try {
                                try {
                                    val inputStream: InputStream = getContentResolver()?.openInputStream(
                                        Uri.fromFile(File(it))
                                    )!!
                                    val outputStream = FileOutputStream(output)
                                    var read = 0
                                    val bufferSize = 1024
                                    val buffers = ByteArray(bufferSize)
                                    while (inputStream.read(buffers)
                                            .also { read = it } != -1
                                    ) {
                                        outputStream.write(buffers, 0, read)
                                    }
                                    var uri = FileProvider.getUriForFile(
                                        application,
                                        application.packageName + ".provider",
                                        output
                                    );

                                    inputStream.close()
                                    outputStream.close()
                                } catch (e: java.lang.Exception) {
                                    Log.e("Exception", e.message!!)
                                }
                            } catch (ex: java.lang.Exception) {
                                ex.printStackTrace()
                            }
                            Toast.makeText(this@ImageView, "Image Recovered to the Folder ${output}", Toast.LENGTH_SHORT).show()
                        }

                    }else{
                    copyFile(File(it), File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Recoverd Photos/${File(it).name}"))
                        Toast.makeText(this@ImageView, "Image Recovered to the ${File(Environment.getExternalStorageDirectory()
                            .getAbsolutePath() + "/Recoverd Photos/${File(it).name}")}", Toast.LENGTH_SHORT)
                            .show()
                }

//                copyFile(
//                    File(it), File(
//                        Environment.getExternalStorageDirectory()
//                            .getAbsolutePath() + "/MyFolder/${File(it).name}"
//                    )
//                )

                Log.e(
                    "TAG",
                    "onCreate: The new folder created is ${
                        File(
                            Environment.getExternalStorageDirectory()
                                .getAbsolutePath() + "/Recovered Images/${File(it).name}"
                        )
                    }"
                )
                Log.e("TAG", "onCreate: The path of the source file is ${File(it)}")
            }
        }
    }


    @Throws(IOException::class)
    fun copyFile(sourceFile: File?, destFile: File) {

        if (!destFile.parentFile.exists()) destFile.parentFile.mkdirs()
//        Log.e("TAG", "copyFile: Directory created successfully ${destFile.parentFile?.mkdirs()}", )
        if (!destFile.exists()) {
            Log.e("TAG", "copyFile: The path of the destination file is ${destFile.absolutePath} ", )
            destFile.createNewFile()
            Log.e("TAG", "copyFile: New Created File is ${destFile.createNewFile()}", )
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
                var id: Long = 0
                val cr: ContentResolver = contentResolver!!
                val selection = MediaStore.Images.Media.DATA
                val selectionArgs = arrayOf<String>(checkFile.absolutePath)
                val projection = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA)
                val sortOrder = MediaStore.Images.Media.TITLE + " ASC"
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
                    setResult(RESULT_OK, Intent().putExtra("Deleted_Index", deletedIndex))
                    finish()
                }
            }
        }

}