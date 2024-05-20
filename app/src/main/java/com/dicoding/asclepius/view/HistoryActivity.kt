package com.dicoding.asclepius.view

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityHistoryBinding
import com.dicoding.asclepius.helper.ViewModelFactory

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding

    private val historyViewModel by viewModels<HistoryViewModel>{
        ViewModelFactory.getInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val layoutManager = LinearLayoutManager(this)
        binding.recycleView.layoutManager = layoutManager

        val adapter = HistoryAdapter()
        binding.recycleView.adapter = adapter

        historyViewModel.isLoading.observe(this) {
            binding.progressBar.isVisible = it
        }

        historyViewModel.history.observe(this) { historyList ->
            adapter.submitList(historyList)
        }

       historyViewModel.isEmpty.observe(this) { isEmpty ->
            binding.empty.isVisible = isEmpty
        }

        historyViewModel.getAllHistory()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.history_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete_all -> {
                historyViewModel.deleteAllHistory()
                showToast("History Deleted")
                true
            }
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show()
    }
}