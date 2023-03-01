package com.annas.admobads

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.example.admobads.R
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

class NativeManager {
    companion object
    {

        @SuppressLint("StaticFieldLeak")
        private lateinit var nativeAdLayout: FrameLayout

        @SuppressLint("StaticFieldLeak")
        private lateinit var rootLayout: FrameLayout

        private lateinit var shimmer_view_container: ShimmerFrameLayout

        @SuppressLint("StaticFieldLeak")
        private lateinit var myContext: Context

        fun showNativeAds(context: Context,nativeId:String) {
            myContext = context
            shimmer_view_container = (myContext as Activity).findViewById(R.id.shimmer_view_container)
            val adLoader = AdLoader.Builder(myContext, nativeId)
                .forNativeAd { nativeAd ->
                    val nativeAdView = (myContext as Activity).layoutInflater.inflate(R.layout.lib_native_ad_layout, null) as NativeAdView
                    mapNativeAdToLayout(nativeAd, nativeAdView)
                    nativeAdLayout = (myContext as Activity).findViewById(R.id.nativeAd)
                    nativeAdLayout.removeAllViews()
                    nativeAdLayout.addView(nativeAdView)
                }
                .withAdListener(object : AdListener() {
                    override fun onAdLoaded() {
                        super.onAdLoaded()
                        nativeAdLayout.visibility = View.VISIBLE
                        shimmer_view_container.stopShimmer()
                        shimmer_view_container.visibility = View.GONE
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        super.onAdFailedToLoad(loadAdError)
                    }
                })
                .build()
            adLoader.loadAd(AdRequest.Builder().build())
        }

        private fun mapNativeAdToLayout(nativeAd: NativeAd, nativeAdView: NativeAdView) {
            val mediaView = nativeAdView.findViewById<MediaView>(R.id.nativeMedia)
            nativeAdView.mediaView = mediaView
            nativeAdView.headlineView = nativeAdView.findViewById(R.id.nativeHeadline)
            nativeAdView.advertiserView = nativeAdView.findViewById(R.id.nativeAdvertiser)
            nativeAdView.iconView = nativeAdView.findViewById(R.id.nativeIcon)
            nativeAdView.bodyView = nativeAdView.findViewById(R.id.nativeBody)
            nativeAdView.priceView = nativeAdView.findViewById(R.id.nativePrice)
            nativeAdView.storeView = nativeAdView.findViewById(R.id.nativeStore)
            nativeAdView.callToActionView = nativeAdView.findViewById(R.id.nativeAction)
            (nativeAdView.headlineView as TextView?)!!.text = nativeAd.headline
            if (nativeAd.advertiser == null) {
                nativeAdView.advertiserView!!.visibility = View.GONE
            } else {
                (nativeAdView.advertiserView as TextView?)!!.text = nativeAd.advertiser
            }
            if (nativeAd.icon == null) {
                nativeAdView.iconView!!.visibility = View.GONE
            } else {
                (nativeAdView.iconView as ImageView?)!!.setImageDrawable(
                    nativeAd.icon!!.drawable
                )
            }
            if (nativeAd.body == null) {
                nativeAdView.bodyView!!.visibility = View.GONE
            } else {
                (nativeAdView.bodyView as TextView?)!!.text = nativeAd.body
            }
            if (nativeAd.price == null) {
                nativeAdView.priceView!!.visibility = View.GONE
            } else {
                (nativeAdView.priceView as TextView?)!!.text = nativeAd.price
            }
            if (nativeAd.store == null) {
                nativeAdView.storeView!!.visibility = View.GONE
            } else {
                (nativeAdView.storeView as TextView?)!!.text = nativeAd.store
            }
            if (nativeAd.callToAction == null) {
                nativeAdView.callToActionView!!.visibility = View.GONE
            } else {
                (nativeAdView.callToActionView as Button?)!!.text = nativeAd.callToAction
            }
            nativeAdView.setNativeAd(nativeAd)
        }
    }
}