package com.bigosvaap.android.signlanguaje.alphabetrecognition

data class Recognition(val label: String, val confidence: Float){

    override fun toString(): String {
        return "$label / $probabilityString"
    }

    val probabilityString = String.format("%.1f%%", confidence)

}
