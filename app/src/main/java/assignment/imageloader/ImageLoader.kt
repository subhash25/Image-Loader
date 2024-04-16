package assignment.imageloader

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

object ImageLoader {

    private val mainHandler = Handler(Looper.getMainLooper())

    fun loadImage(url: String, imageView: ImageView) {
        Thread {
            try {
                val bitmap = downloadBitmap(url)
                mainHandler.post {
                    imageView.setImageBitmap(bitmap)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }.start()
    }

    @Throws(IOException::class)
    private fun downloadBitmap(url: String): Bitmap {
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.connect()
        val inputStream = connection.inputStream
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream.close()
        connection.disconnect()
        return bitmap
    }
}