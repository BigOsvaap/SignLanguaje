package com.bigosvaap.android.signlanguaje.alphabetrecognition

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AlphabetRecognitionViewModel : ViewModel() {

    private val _recognitionList = MutableLiveData<List<Recognition>>()
    val recognitionList: LiveData<List<Recognition>> = _recognitionList

    fun updateData(recognitions: List<Recognition>){
        _recognitionList.postValue(recognitions)
    }

}