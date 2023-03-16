package com.pro.stickermaker.sahredpref

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import com.pro.stickermaker.whatsappcode.dataclasses.Sticker
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

object StickerFileManager {

    fun compressAndSaveImage(
        context: Context,
        stickerUniqueIdentifier: String,
        listOfStickerOfRelatedPack: List<Sticker>,
        bitmap: Bitmap,
        addToSharedPrefCallback: (Sticker, Bitmap) -> Unit
    ) {
        if (listOfStickerOfRelatedPack.size <= 30) {

            val cw = ContextWrapper(context.applicationContext)
            val directory = cw.getDir(stickerUniqueIdentifier, Context.MODE_PRIVATE)
            val fileName =
                stickerUniqueIdentifier + listOfStickerOfRelatedPack.size.toString() + ".webp"
            val path = File(directory, fileName)
            var fos: FileOutputStream? = null
            try {
                val maxFileSize = 100 * 1024
                val outputStream = ByteArrayOutputStream(maxFileSize)
                var quality = 100
                Log.d("Size_Track_Logs", "starting compression ${outputStream.toByteArray().size}")
                do {
                    Log.d("Size_Track_Logs", "compressing ")
                    outputStream.reset()
                    quality -= 10 // Decrease compression quality by 10
                    bitmap.compress(Bitmap.CompressFormat.WEBP, quality, outputStream)
                } while (outputStream.toByteArray().size > maxFileSize && quality > 0)
//                bitmap.compress(Bitmap.CompressFormat.WEBP, 100, fos)
                fos = FileOutputStream(path)
                fos.write(outputStream.toByteArray())


            } catch (e: Exception) {
                Log.e("Emoji Save Error", e.printStackTrace().toString())
            } finally {
                try {
                    val stickerEntity = Sticker(fileName, listOf())
                    addToSharedPrefCallback(
                        stickerEntity,
                        bitmap
                    )
                    fos!!.close()
                } catch (e: Exception) {
                    e.printStackTrace();
                }
            }
        } else {
            Toast.makeText(
                context.applicationContext, "Can not be added more than 30 sticker to a package",
                Toast.LENGTH_LONG
            ).show()
        }

    }
}

