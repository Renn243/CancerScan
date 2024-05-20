package com.dicoding.asclepius.view

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.asclepius.data.local.History
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.dicoding.asclepius.helper.ViewModelFactory

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding
    private val resultViewModel: ResultViewModel by viewModels { ViewModelFactory.getInstance(application) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageUri = intent.getStringExtra(EXTRA_IMAGE_URI)
        val prediction = intent.getStringExtra(EXTRA_PREDICTION)
        val score = intent.getStringExtra(EXTRA_SCORE)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        imageUri?.let {
            binding.resultImage.setImageURI(Uri.parse(it))
        }

        val predictionScoreText = "$prediction, $score"
        binding.tvPrediction.text = predictionScoreText

        binding.btnSave.setOnClickListener {
            if (score != null) {
                val newHistory = History(
                    imageUri = imageUri ?: "",
                    label = prediction ?: "",
                    score = score
                )
                resultViewModel.insert(newHistory)
                Toast.makeText(this, "Data Saved", Toast.LENGTH_SHORT).show()

                setResult(Activity.RESULT_OK)

                finish()
            } else {
                Toast.makeText(this, "Invalid score", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
        const val EXTRA_PREDICTION = "extra_prediction"
        const val EXTRA_SCORE = "extra_score"
    }
}