package com.muhammad.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID

interface LocalFileProvider {
    fun saveBitmapToFile(bitmap: Bitmap, file: File): File
    fun getFileFromCache(fileName: String): File
    fun createCacheFile(fileName: String): File
    fun saveToSharedStorage(file: File, fileName: String, mimeType: String): Uri
    fun sharingUriForFile(file: File): Uri
    fun copyToInternalStorage(uri: Uri): File
    fun saveUriToSharedStorage(
        inputUri: Uri, fileName: String, mimeType: String,
    ): Uri
}

open class LocalFileProviderImp(
    private val context : Context
) : LocalFileProvider {
    override fun saveBitmapToFile(
        bitmap: Bitmap,
        file: File,
    ): File {
        var outputStream: FileOutputStream? = null
        try {
            outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            return file
        } catch (e: IOException) {
            throw e
        } finally {
            outputStream?.close()
        }
    }

    override fun getFileFromCache(fileName: String): File {
        return File(context.cacheDir, fileName)
    }

    override fun createCacheFile(fileName: String): File {
        val cacheDir = context.cacheDir
        val imageFile = File(cacheDir, fileName)
        if (!imageFile.createNewFile()) {
            throw IOException("Unable to create file : ${imageFile.absolutePath}")
        }
        return imageFile
    }

    override fun saveToSharedStorage(
        file: File,
        fileName: String,
        mimeType: String,
    ): Uri {
        val (uri, contentValues) = createSharedStorageEntry(fileName, mimeType)
        saveFileToUri(file, uri)
        contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
        context.contentResolver.update(uri, contentValues, null, null)
        return uri
    }

    override fun sharingUriForFile(file: File): Uri {
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    override fun copyToInternalStorage(uri: Uri): File {
        val uuId = UUID.randomUUID()
        val file = File(context.cacheDir, "temp_file_$uuId")
        context.contentResolver.openInputStream(uri)?.use {inputStream ->
            file.outputStream().use {outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        return file
    }

    override fun saveUriToSharedStorage(
        inputUri: Uri,
        fileName: String,
        mimeType: String,
    ): Uri {
        val (newUri, contentValues) = createSharedStorageEntry(fileName = fileName, mimeType = mimeType)
        context.contentResolver.openOutputStream(newUri)?.use {outputStream ->
            context.contentResolver.openInputStream(inputUri)?.use {inputStream ->
                val buffer = ByteArray(4 * 1024)
                var byteRead : Int
                while(inputStream.read(buffer).also { byteRead = it } != -1){
                    outputStream.write(buffer, 0, byteRead)
                }
            }
        } ?: throw IOException("Failed to open outputStream")
        contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
        context.contentResolver.update(newUri, contentValues, null, null)
        return newUri
    }

    private fun createSharedStorageEntry(
        fileName: String,
        mimeType: String,
    ): Pair<Uri, ContentValues> {
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            put(MediaStore.MediaColumns.IS_PENDING, 1)
            put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
        }
        val collection = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else{
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
        val uri = resolver.insert(collection, contentValues) ?: throw IOException("Failed to create new entry in MediaStore")
        return Pair(uri, contentValues)
    }
    private fun saveFileToUri(file : File, uri : Uri){
        context.contentResolver.openOutputStream(uri)?.use {outputStream ->
            FileInputStream(file).use {inputStream ->
                val buffer = ByteArray(4 * 1024)
                var byteRead : Int
                while(inputStream.read(buffer).also { byteRead = it } != -1){
                    outputStream.write(buffer, 0, byteRead)
                }
            }
        } ?: throw IOException("Failed to open output stream for uri : $uri")
    }
}