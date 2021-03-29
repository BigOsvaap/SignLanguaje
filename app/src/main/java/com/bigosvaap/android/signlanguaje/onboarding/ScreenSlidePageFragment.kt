package com.bigosvaap.android.signlanguaje.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bigosvaap.android.signlanguaje.R
import com.bigosvaap.android.signlanguaje.databinding.OnboardingItemsBinding

class ScreenSlidePageFragment(private val onBoardingItem: OnBoardingItem): Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = DataBindingUtil.inflate<OnboardingItemsBinding>(inflater, R.layout.onboarding_items, container, false)
        binding.onBoardingItem = onBoardingItem
        return binding.root
    }

}