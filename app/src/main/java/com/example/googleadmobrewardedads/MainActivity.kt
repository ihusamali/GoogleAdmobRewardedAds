package com.example.googleadmobrewardedads

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class MainActivity : AppCompatActivity() {

    private var rewardedAd: RewardedAd? = null
    private final var TAG = "MainActivity"

    lateinit var button: Button

    lateinit var handler: Handler
    lateinit var runnable: java.lang.Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button = findViewById(R.id.button123)



        loadAd()

        button.setOnClickListener {
            showAd()

        }
        showAd()
    }

    private fun waitForAdToLoadAndThenMoveNext() {
//        Toast.makeText(this, "wait fun", Toast.LENGTH_SHORT).show()

        handler = Handler(Looper.getMainLooper())
//        Toast.makeText(this, "handler", Toast.LENGTH_SHORT).show()

        runnable = kotlinx.coroutines.Runnable {
//            Toast.makeText(this, "runnable", Toast.LENGTH_SHORT).show()

//            progressBar.progress = progressBar.progress.plus(1)
            if (rewardedAd == null) {
                runnable.let { handler.postDelayed(it, 1) }
//                Toast.makeText(this, "null", Toast.LENGTH_SHORT).show()
            } else {
                //ad not loading, check if ad is loaded or ad load failed and handle the cases accordingly

                if (rewardedAd != null) {
//                    Toast.makeText(this, "show ad", Toast.LENGTH_SHORT).show()

                    showAd()
                } else {
                    Toast.makeText(this, "waiting to load", Toast.LENGTH_SHORT).show()


                }


            }
        }
        handler.postDelayed(runnable, 1)

    }

    private fun showAd() {
        rewardedAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
            override fun onAdClicked() {
                // Called when a click is recorded for an ad.
                Log.d(TAG, "Ad was clicked.")
            }

            override fun onAdDismissedFullScreenContent() {
                // Called when ad is dismissed.
                // Set the ad reference to null so you don't show the ad a second time.
                Log.d(TAG, "Ad dismissed fullscreen content.")
                rewardedAd = null
            }

            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                // Called when ad fails to show.
                Log.e(TAG, "Ad failed to show fullscreen content.")
                rewardedAd = null
            }

            override fun onAdImpression() {
                // Called when an impression is recorded for an ad.
                Log.d(TAG, "Ad recorded an impression.")
            }

            override fun onAdShowedFullScreenContent() {
                // Called when ad is shown.
                Log.d(TAG, "Ad showed fullscreen content.")
            }
        }

        rewardedAd?.let { ad ->
            ad.show(this, OnUserEarnedRewardListener { rewardItem ->
                // Handle the reward.
                val rewardAmount = rewardItem.amount
                val rewardType = rewardItem.type
                Log.d(TAG, "User earned the reward.")
            })
        } ?: run {
            Log.d(TAG, "The rewarded ad wasn't ready yet.")
        }
    }

    private fun loadAd() {
        var adRequest = AdRequest.Builder().build()

        RewardedAd.load(this,"ca-app-pub-3940256099942544/5224354917", adRequest, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                adError.toString().let { Log.d(TAG, it) }
                rewardedAd = null
            }

            override fun onAdLoaded(ad: RewardedAd) {
                Log.d(TAG, "Ad was loaded.")
                rewardedAd = ad
            }
        })
    }
}