package com.annas.admobads

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.view.Display
import android.view.View
import android.widget.FrameLayout
import com.example.admobads.R
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.*

class BannerManager {

    companion object
    {
        @SuppressLint("StaticFieldLeak")
        private lateinit var adaptiveBannerFrame: FrameLayout
        private var adView: AdView? = null
        private lateinit var shimmerViewContainer: ShimmerFrameLayout
        @SuppressLint("StaticFieldLeak")
        private lateinit var myContext: Context

        fun initBannerAd(context: Context,bannerId:String) {
            myContext = context
            shimmerViewContainer = (context as Activity).findViewById(R.id.shimmer_view_container)
            adaptiveBannerFrame = (context as Activity).findViewById(R.id.adaptive_banner_frame)
            adView = AdView(context)
            adView?.adUnitId = bannerId
            adaptiveBannerFrame.addView(adView)
            loadBanner()
        }

        private fun loadBanner() {
            val adRequest: AdRequest = AdRequest.Builder().build()
            val adSize: AdSize = getAdSize()
            // Step 4 - Set the adaptive ad size on the ad view.
            adView?.setAdSize(adSize)

            // Step 5 - Start loading the ad in the background.
            adView?.loadAd(adRequest)
            adView?.adListener = object: AdListener() {
                override fun onAdClicked() {
                    // Code to be executed when the user clicks on an ad.
                }

                override fun onAdClosed() {
                    // Code to be executed when the user is about to return
                    // to the app after tapping on an ad.
                }

                override fun onAdFailedToLoad(adError : LoadAdError) {
                    // Code to be executed when an ad request fails.
                }

                override fun onAdImpression() {
                    // Code to be executed when an impression is recorded
                    // for an ad.
                }

                override fun onAdLoaded() {
                    // Code to be executed when an ad finishes loading.
                    shimmerViewContainer.stopShimmer()
                    shimmerViewContainer.visibility = View.GONE
                }

                override fun onAdOpened() {
                    // Code to be executed when an ad opens an overlay that
                    // covers the screen.
                }
            }
        }

        private fun getAdSize(): AdSize {
            // Step 2 - Determine the screen width (less decorations) to use for the ad width.
            val display: Display = (myContext as Activity).windowManager.defaultDisplay
            val outMetrics = DisplayMetrics()
            display.getMetrics(outMetrics)
            val widthPixels: Int = outMetrics.widthPixels
            val density: Float = outMetrics.density
            val adWidth = (widthPixels / density).toInt()

            // Step 3 - Get adaptive ad size and return for setting on the ad view.
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(myContext, adWidth)
        }
    }
}