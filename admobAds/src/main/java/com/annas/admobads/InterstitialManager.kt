package com.annas.admobads

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.View
import android.view.Window
import com.example.admobads.R
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.coroutines.*

class InterstitialManager {

    companion object {

        private var mInterstitialAd: InterstitialAd? = null
        private var interstitialAdId = ""

        private var interactionCount = 0
        private var interactionLimit = 3

        private var requestCount = 0;
        private var requestLimit = 3;

        private var addLoading = false;
        private var shouldLoadAgain = true;

        @SuppressLint("StaticFieldLeak")
        private lateinit var clickedView: View

        @SuppressLint("StaticFieldLeak")
        private lateinit var myContext: Context

        @SuppressLint("StaticFieldLeak")
        fun createInterstitialAd(context: Context,interstitialId:String) {
            myContext = context
            interstitialAdId = interstitialId
            if (mInterstitialAd == null && !addLoading && shouldLoadAgain && requestCount < requestLimit) {
                addLoading = true
                requestCount++
                InterstitialAd.load(
                    myContext,
                    interstitialAdId,
                    AdRequest.Builder().build(),
                    object : InterstitialAdLoadCallback() {
                        override fun onAdLoaded(interstitialAd: InterstitialAd) {

                            addLoading = false
                            mInterstitialAd = interstitialAd
                            mInterstitialAd?.fullScreenContentCallback =
                                object : FullScreenContentCallback() {

                                    override fun onAdDismissedFullScreenContent() {
                                        super.onAdDismissedFullScreenContent()
                                        interactionCount = 0
                                        mInterstitialAd = null
                                        clickedView.performClick()
                                        createInterstitialAd(context,interstitialAdId)
                                    }

                                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                        super.onAdFailedToShowFullScreenContent(adError)
                                        interactionCount = 0
                                        mInterstitialAd = null
                                        clickedView.performClick()
                                        createInterstitialAd(context,interstitialAdId)

                                    }

                                    override fun onAdShowedFullScreenContent() {
                                        super.onAdShowedFullScreenContent()
                                        mInterstitialAd = null
                                    }
                                }
                        }

                        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                            mInterstitialAd = null
                            addLoading = false
                        }
                    })
            }
        }

        fun showInterstitialAd(context: Context, view: View, loadAgain:Boolean) {
            shouldLoadAgain = loadAgain
            clickedView = view
            showAd(context)
        }

        fun isInteractionCompeted():Boolean
        {
            return interactionCount >= interactionLimit
        }

        fun addInteraction()
        {
            interactionCount++
        }

        private fun showAd(context: Context)
        {
            val loadingDialog = Dialog(context)
            loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            loadingDialog.setCancelable(false)
            loadingDialog.setContentView(R.layout.lib_loading_ad)
            loadingDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            loadingDialog.show()
            CoroutineScope(Dispatchers.IO).launch {
                delay(2000)
                withContext(Dispatchers.Main) {
                    interactionCount = 0
                    mInterstitialAd?.show(context as Activity)
                    loadingDialog.cancel()
                }
            }
        }

        fun requestsLimit(limit:Int)
        {
            requestLimit = limit
        }

        fun interactionLimit(limit:Int)
        {
            requestLimit = limit
        }

        fun isAdNull():Boolean
        {
            return mInterstitialAd == null
        }
    }

}