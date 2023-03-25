package com.sh1p1lov.multitouchtest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup.MarginLayoutParams
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.ViewModelProvider
import com.sh1p1lov.multitouchtest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding get() = _binding!!
    private val viewModel by lazy { ViewModelProvider(this)[MainActivityViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.tvMaxTouchCount) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            Log.d("MainActivityLog", insets.top.toString())
            view.updateLayoutParams<MarginLayoutParams> { topMargin = insets.top }
            WindowInsetsCompat.CONSUMED
        }

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