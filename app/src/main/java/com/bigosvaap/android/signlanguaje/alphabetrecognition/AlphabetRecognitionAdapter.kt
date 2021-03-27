package com.bigosvaap.android.signlanguaje.alphabetrecognition

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bigosvaap.android.signlanguaje.databinding.RecognitionItemBinding

class AlphabetRecognitionAdapter : ListAdapter<Recognition, AlphabetRecognitionAdapter.RecognitionViewHolder>(RecognitionDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecognitionViewHolder {
        return RecognitionViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecognitionViewHolder, position: Int) {
       holder.bind(getItem(position))
    }

    class RecognitionViewHolder private constructor(private val binding: RecognitionItemBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(recognition: Recognition){
            binding.recognitionItem = recognition
        }

        companion object{
            fun from(parent: ViewGroup): RecognitionViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RecognitionItemBinding.inflate(layoutInflater, parent, false)
                return RecognitionViewHolder(binding)
            }
        }

    }

    private class RecognitionDiffUtil : DiffUtil.ItemCallback<Recognition>() {
        override fun areItemsTheSame(oldItem: Recognition, newItem: Recognition): Boolean {
            return oldItem.label == newItem.label
        }

        override fun areContentsTheSame(oldItem: Recognition, newItem: Recognition): Boolean {
            return oldItem.confidence == newItem.confidence
        }
    }

}