package assignment.imageloader

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import assignment.imageloader.databinding.ActivityMainBinding
import com.google.gson.Gson
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val imageListUrl =
        "https://acharyaprashant.org/api/v2/content/misc/media-coverages?limit=100"
    private var dataList: List<ImageDataModelItem>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setOnClickListeners()
        fetchData()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun fetchData() {
        GlobalScope.launch(Dispatchers.IO) {
            dataList = fetchUsersFromApi()
            runOnUiThread {
                dataList?.let {
                    if (it.isNotEmpty()) {
                        initList(it)
                    } else {
                        binding.tvErrorMsg.text = getString(R.string.no_data_available)
                        binding.gpRetry.isVisible = true
                        binding.progressBar.isVisible = false
                        binding.rvList.isVisible = false
                    }
                }
            }
        }
    }

    private fun setOnClickListeners() {
        binding.btnRetry.setOnClickListener {
            binding.gpRetry.isVisible = false
            binding.progressBar.isVisible = true
            fetchData()
        }
    }

    private fun initList(list: List<ImageDataModelItem>) {
        binding.progressBar.isVisible = false
        binding.rvList.isVisible = true
        val adapter = ListAdapter(list, this)
        binding.rvList.adapter = adapter
        binding.rvList.layoutManager = GridLayoutManager(this, 3)
    }

    private fun fetchUsersFromApi(): List<ImageDataModelItem>? {
        val connection = URL(imageListUrl).openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        val responseCode = connection.responseCode

        return if (responseCode == HttpURLConnection.HTTP_OK) {
            val inputStream = connection.inputStream
            val responseText = inputStream.bufferedReader().use { it.readText() }
            Gson().fromJson(responseText, Array<ImageDataModelItem>::class.java).toList()
        } else {
            runOnUiThread {
                binding.gpRetry.isVisible = true
                binding.progressBar.isVisible = false
                binding.rvList.isVisible = false
                binding.tvErrorMsg.text =
                    getString(R.string.failed_to_fetch_data_from_api_response_code, responseCode)
            }
            null
        }
    }
}