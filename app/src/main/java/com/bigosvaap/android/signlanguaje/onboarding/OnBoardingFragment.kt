package com.bigosvaap.android.signlanguaje.onboarding

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bigosvaap.android.signlanguaje.R
import com.bigosvaap.android.signlanguaje.databinding.OnBoardingFragmentBinding


class OnBoardingFragment : Fragment(R.layout.on_boarding_fragment) {

    private lateinit var indicator: LinearLayout
    private lateinit var actionButton: Button

    private val onBoardingItems by lazy { setupOnBoardingItems() }
    private val onBoardingAdapter by lazy { ScreenSlidePagerAdapter(onBoardingItems, this@OnBoardingFragment) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = OnBoardingFragmentBinding.bind(view)

        binding.apply {
            indicator = layoutOnBoardingIndicator

            onBoardingViewPager.adapter = onBoardingAdapter
            onBoardingViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
                override fun onPageSelected(position: Int) {
                    setCurrentOnBoardingIndicator(position)
                }
            })

            actionButton = onBoardingActionButton
            actionButton.setOnClickListener {
                findNavController().navigate(OnBoardingFragmentDirections.actionOnBoardingFragmentToPermissionsFragment())
            }

        }

        setupOnBoardingIndicator()

    }

    private fun setupOnBoardingItems(): List<OnBoardingItem>{
        val welcomeItem = OnBoardingItem(0, "Welcome!", "Hello")
        val featureItem = OnBoardingItem(0, "asdsa!", "Hellsado")
        return listOf(welcomeItem, featureItem)
    }

    private fun setupOnBoardingIndicator() {
        val indicators = arrayOfNulls<ImageView>(onBoardingItems.size)
        val layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(8, 0, 8, 0)
        for (i in indicators.indices) {
            indicators[i] = ImageView(requireContext())
            indicators[i]!!.setImageDrawable(ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.onboarding_indicator_inactive
            ))
            indicators[i]!!.layoutParams = layoutParams
            indicator.addView(indicators[i])
        }
    }

    private fun setCurrentOnBoardingIndicator(index: Int) {
        val childCount: Int = indicator.childCount
        for (i in 0 until childCount) {
            val imageView = indicator.getChildAt(i) as ImageView
            if (i == index) {
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(requireContext(), R.drawable.onboarding_indicator_active)
                )
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        requireContext(), R.drawable.onboarding_indicator_inactive))
            }
        }
        if (index == onBoardingAdapter.itemCount - 1) {
            actionButton.text = getString(R.string.understood)
            actionButton.visibility = View.VISIBLE
        }else{
            actionButton.visibility = View.GONE
        }
    }

    private inner class ScreenSlidePagerAdapter(private val items: List<OnBoardingItem>, fragment: Fragment) : FragmentStateAdapter(fragment) {

        override fun getItemCount() = items.size

        override fun createFragment(position: Int) = ScreenSlidePageFragment(items[position])

    }

}