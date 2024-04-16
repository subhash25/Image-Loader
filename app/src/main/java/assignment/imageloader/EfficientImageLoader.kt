package assignment.imageloader

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class EfficientImageLoader(private val context: Context) {
    private val MEMORY_CACHE_SIZE = 4 * 1024 * 1024 // 4MB
    private val memoryCache = LruCache<String, Bitmap>(MEMORY_CACHE_SIZE)
    private val cacheDir: File

    init {
        cacheDir = File(context.cacheDir, "custom_cache_directory")
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }
    }

    suspend fun loadImage(url: String): Bitmap? {
        memoryCache.get(url)?.let {
            return it
        }

        val cachedBitmap = loadBitmapFromDiskCache(url)
        if (cachedBitmap != null) {
            memoryCache.put(url, cachedBitmap)
            return cachedBitmap
        }

        return downloadBitmap(url)
    }

    private suspend fun downloadBitmap(url: String): Bitmap? = withContext(Dispatchers.IO) {
        var bitmap: Bitmap? = null
        val connection = URL(url).openConnection() as HttpURLConnection
        try {
            connection.connect()
            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val inputStream = connection.inputStream
                bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream.close()

                memoryCache.put(url, bitmap)
                saveBitmapToDiskCache(url, bitmap)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection.disconnect()
        }
        return@withContext bitmap
    }

    private fun loadBitmapFromDiskCache(url: String): Bitmap? {
        val file = File(cacheDir, url.hashCode().toString())
        return if (file.exists()) BitmapFactory.decodeFile(file.absolutePath) else null
    }

    private fun saveBitmapToDiskCache(url: String, bitmap: Bitmap) {
        val file = File(cacheDir, url.hashCode().toString())
        FileOutputStream(file).use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            it.flush()
        }
    }
}