package com.sophoun.testcompose

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ScrollView
import androidx.core.view.marginTop
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.sophoun.testcompose.databinding.ActivitySwipePageBinding
import com.sophoun.testcompose.databinding.FragmentSwapPageSecondBinding
import com.sophoun.testcompose.databinding.FragmentSwipePageMainBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.reflect.Field
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.roundToInt


class SwipePageActivity: FragmentActivity() {

    lateinit var binding: ActivitySwipePageBinding

    @SuppressLint("MissingInflatedId", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySwipePageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val viewPager = binding.pager
        val fragments = arrayOf(SwipePageSecondFragment(), SwipePageMainFragment())
        viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = fragments.size
            override fun createFragment(position: Int): Fragment = fragments[position]
        }
        viewPager.currentItem = 1
        viewPager.orientation = ViewPager2.ORIENTATION_VERTICAL
        viewPager.isUserInputEnabled = false
        viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                viewPager.isUserInputEnabled = position == 0
            }
        })
    }
}


/**
 * Main fragment
 */
class SwipePageMainFragment : Fragment() {
    lateinit var binding: FragmentSwipePageMainBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSwipePageMainBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var isScrollReachTop = false
        var touchPoint = 0f
        binding.scrollview.setOnTouchListener { _, event ->
            when(event.action) {
                MotionEvent.ACTION_DOWN -> {
                    touchPoint = event.y
                    isScrollReachTop = binding.scrollview.scrollY == 0
                    resetIndicatorSize()
                }
                MotionEvent.ACTION_MOVE -> {
                    val moveYDistance = (event.y - touchPoint).roundToInt()
                    if(isScrollReachTop && moveYDistance > 0) {
                        if(moveYDistance > 500) {
                            moveTopFirstPage()
                        } else {
                            setIndicatorIconSize(moveYDistance / 2)
                            return@setOnTouchListener true
                        }
                    }
                }
                MotionEvent.ACTION_UP -> {
                    resetIndicatorSize()
                }
            }
            false
        }
    }

    /**
     * Reset indicator icon size
     */
    private fun resetIndicatorSize() {
        binding.indicator.layoutParams.apply {
            height = 0
            width = 0
        }
        binding.indicator.requestLayout()
    }

    /**
     * Set indicator icon size
     */
    private fun setIndicatorIconSize(size: Int) {
        binding.indicator.layoutParams.apply {
            height = size
            width = size
        }
        binding.indicator.requestLayout()
    }

    /**
     * Move to first page
     */
    private fun moveTopFirstPage() {
        (activity as SwipePageActivity).binding.pager.apply {
            currentItem = 0
        }
    }
}

/**
 * Second fragment
 */
class SwipePageSecondFragment : Fragment() {
    lateinit var binding: FragmentSwapPageSecondBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSwapPageSecondBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.view.setOnClickListener {
            (activity as SwipePageActivity).binding.pager.currentItem = 1
        }
    }
}