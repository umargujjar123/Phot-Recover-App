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
import androidx.databinding.DataBindingUtil
import com.example.data_recovery.ScanScreen.Companion.DIRECTORY_MODEL_ARGS
import com.example.data_recovery.databinding.ImageViewBinding
import com.example.data_recovery.model.DirectoriesModel
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
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
                copyFileOrDirectory(it, Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/MyFolder/${File(it).name}")

//                copyFile(
//                    File(it), File(
//                        Environment.getExternalStorageDirectory()
//                            .getAbsolutePath() + "/MyFolder/${File(it).name}"
//                    )
//                )
                Toast.makeText(this@ImageView, "Image Recovered Successfully", Toast.LENGTH_SHORT)
                    .show()
                Log.e(
                    "TAG",
                    "onCreate: The new folder created is ${
                        File(
                            Environment.getExternalStorageDirectory()
                                .getAbsolutePath() + "/MyFolder/${File(it).name}"
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
        if (!destFile.exists()) {
            destFile.createNewFile()
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

    private fun deleteImage(path: String) {
        val fDelete = File(path)
        if (fDelete.exists()) {
            if (fDelete.delete()) {
                MediaScannerConnection.scanFile(
                    this,
                    arrayOf(Environment.getExternalStorageDirectory().toString()),
                    null
                ) { path, uri ->
                    Log.d("debug", "DONE")
                }
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
    fun copyFileOrDirectory(srcDir: String?, dstDir: String?) {
        try {
            val src = File(srcDir)
            val dst = File(dstDir, src.name)
            if (src.isDirectory) {
                val files = src.list()
                val filesLength = files.size
                for (i in 0 until filesLength) {
                    val src1 = File(src, files[i]).path
                    val dst1 = dst.path
                    copyFileOrDirectory(src1, dst1)
                }
            } else {
                copyFile(src, dst)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

}