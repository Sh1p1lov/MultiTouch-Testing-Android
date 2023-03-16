package com.sh1p1lov.multitouch_testing_android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.sh1p1lov.multitouch_testing_android.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding get() = _binding!!
    private val viewModel by lazy { ViewModelProvider(this)[MainActivityViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.multiTouchView.addOnCountTouchesUpdatedListener { touchCount ->
            binding.tvTouchCount.text = touchCount.toString()
            viewModel.tryUpdateMaxTouchCount(touchCount)
        }

        viewModel.maxTouchCountLiveData.observe(this) { maxTouchCount ->
            binding.tvMaxTouchCount.text = maxTouchCount.toString()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}