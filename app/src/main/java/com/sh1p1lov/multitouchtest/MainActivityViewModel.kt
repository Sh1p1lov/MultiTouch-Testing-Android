package com.sh1p1lov.multitouchtest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel: ViewModel() {

    private val _maxTouchCount = MutableLiveData<Int>(0)
    val maxTouchCount: LiveData<Int> = _maxTouchCount

    fun tryUpdateMaxTouchCount(touchCount: Int) {
        if (touchCount > maxTouchCount.value!!) {
            _maxTouchCount.value = touchCount
        }
    }
}