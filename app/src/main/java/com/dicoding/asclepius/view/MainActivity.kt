package com.dicoding.asclepius.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.asclepius.data.remote.response.ArticlesItem
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import com.yalantis.ucrop.UCrop
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.io.File
import java.text.NumberFormat

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var currentImageUri: Uri? = null
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        binding.rvArticles.layoutManager = LinearLayoutManager(this)

        mainViewModel.isLoading.observe(this) {
            binding.progressBar.isVisible = it
        }

        mainViewModel.newsList.observe(this) { newsList ->
            newsList?.articles?.let { articles ->
                val filteredArticles = articles.filterNotNull()
                setAdapter(filteredArticles)
            }
        }

        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnHistory.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }
        binding.btnAnalyze.setOnClickListener { analyzeImage() }
    }

    private fun startGallery() {
        launcherGallery.launch("image/*")
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
            startUCrop(uri)
        } else {
            showToast("No media selected")
        }
    }

    private fun startUCrop(sourceUri: Uri) {
        val fileName = "cropimage${System.currentTimeMillis()}.jpg"
        val destinationUri = Uri.fromFile(File(cacheDir, fileName))
        UCrop.of(sourceUri, destinationUri)
            .withAspectRatio(1f, 1f)
            .withMaxResultSize(1000, 1000)
            .start(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UCrop.REQUEST_CROP && resultCode == Activity.RESULT_OK) {
            val resultUri = UCrop.getOutput(data!!)
            currentImageUri = resultUri
            showImage()
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
        } else if (requestCode == REQUEST_CODE_RESULT_ACTIVITY && resultCode == Activity.RESULT_OK){
            currentImageUri = null
            showImage()
        }

    }

    private fun showImage() {
        currentImageUri?.let {
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun analyzeImage() {
        currentImageUri?.let {
            val imageClassifierHelper = ImageClassifierHelper(
                context = this,
                classifierListener = object : ImageClassifierHelper.ClassifierListener {
                    override fun onError(error: String) {
                        showToast(error)
                    }

                    override fun onResults(results: List<Classifications>?) {
                        results?.let { classifications ->
                            if (classifications.isNotEmpty() && classifications[0].categories.isNotEmpty()) {
                                val sortedCategories =
                                    classifications[0].categories.sortedByDescending { it?.score }
                                val prediction = sortedCategories[0].label
                                val score = NumberFormat.getPercentInstance().format(sortedCategories[0].score)

                                moveToResult(prediction, score)
                            } else {
                                showToast("No classifications found.")
                            }
                        } ?: showToast("No results found.")
                    }
                }
            )
            imageClassifierHelper.classifyImage(it)
        } ?: showToast("Select an image first.")
    }

    private fun setAdapter(articles: List<ArticlesItem>) {
        val adapter = NewsAdapter()
        adapter.submitList(articles)
        binding.rvArticles.adapter = adapter
    }

    private val REQUEST_CODE_RESULT_ACTIVITY = 101

    private fun moveToResult(prediction: String, score: String) {
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra(ResultActivity.EXTRA_IMAGE_URI, currentImageUri.toString())
        intent.putExtra(ResultActivity.EXTRA_PREDICTION, prediction)
        intent.putExtra(ResultActivity.EXTRA_SCORE, score)
        startActivityForResult(intent, REQUEST_CODE_RESULT_ACTIVITY)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
