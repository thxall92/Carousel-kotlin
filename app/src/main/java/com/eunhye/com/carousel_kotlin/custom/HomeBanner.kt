package com.eunhye.com.carousel_kotlin.custom

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.eunhye.com.carousel_kotlin.R
import com.eunhye.com.carousel_kotlin.databinding.ViewCarouselBinding
import java.util.*

private val LOG_TAG = "HomeBanner"
private val DEFAULT_SLIDE_INTERVAL: Long = 3500

class HomeBanner : FrameLayout {
    lateinit var binding: ViewCarouselBinding

    internal var bannerImg = intArrayOf(R.drawable.banner_1, R.drawable.banner_2, R.drawable.banner_3)

    var Update: Runnable? = null
    var swipeTimer: Timer? = null
    private val animateOnBoundary = true

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context)
    }

    fun initView(context: Context) {
        binding =
            DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.view_carousel, this, true)
        swipeTimer = Timer()

        binding.run {
            vpBanner.adapter = object : PagerAdapter() {
                override fun getCount() = bannerImg.size

                override fun instantiateItem(container: ViewGroup, position: Int): Any {
                    val iv = ImageView(container.context)

                    iv.setOnClickListener {
                        Log.d(LOG_TAG, "click!")
                    }

                    Glide.with(context)
                        .asBitmap()
                        .apply(RequestOptions().centerCrop())
                        .load(bannerImg[position])
                        .into(iv)
                    container.addView(iv)

                    return iv
                }

                override fun isViewFromObject(view: View, `object`: Any): Boolean {
                    return view === `object`
                }

                override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
                    container.removeView(`object` as View)
                }
            }

            vpBanner.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(i: Int, v: Float, i1: Int) {

                }

                override fun onPageSelected(pos: Int) {
                    pivContent.setSelected(vpBanner.currentItem)
                }

                override fun onPageScrollStateChanged(i: Int) {

                }
            })

            Update = Runnable {
                val nextPage = (vpBanner.currentItem + 1) % bannerImg.size
                vpBanner.setCurrentItem(nextPage, 0 != nextPage || animateOnBoundary)
            }
        }

    }

    fun playCarousel() {
        Log.d(LOG_TAG, "timer start")
        swipeTimer?.schedule(object : TimerTask() {
            override fun run() {
                Log.d(LOG_TAG, "timer update")
                binding.vpBanner.post(Update)
            }
        }, DEFAULT_SLIDE_INTERVAL, DEFAULT_SLIDE_INTERVAL)

    }

    fun resetScrollTimer() {
        Log.d(LOG_TAG, "timer close")
        if (null != swipeTimer) {
            swipeTimer!!.cancel()
            swipeTimer = Timer()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        resetScrollTimer()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        playCarousel()
    }
}
