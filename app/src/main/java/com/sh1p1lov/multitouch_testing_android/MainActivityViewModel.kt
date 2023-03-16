package com.sh1p1lov.multitouch_testing_android

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewModel: ViewModel() {

    private val maxTouchCountMutableLiveData = MutableLiveData<Int>(0)
    val maxTouchCountLiveData: LiveData<Int> = maxTouchCountMutableLiveData

    fun tryUpdateMaxTouchCount(touchCount: Int) {
        if (touchCount > maxTouchCountLiveData.value!!) {
            maxTouchCountMutableLiveData.value = touchCount
        }
    }
}