package com.ead.lib.cloudflare_bypass

import android.graphics.Bitmap
import android.webkit.WebView
import android.webkit.WebViewClient
import com.ead.lib.cloudflare_bypass.core.Scripts
import com.ead.lib.cloudflare_bypass.core.system.extensions.evaluateJavascript
import com.ead.lib.cloudflare_bypass.core.system.extensions.isCloudFlareByPassTitle
import com.ead.lib.cloudflare_bypass.util.Thread
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Base Client to setup by default the CloudFlare Bypass
 */
open class BaseClient : WebViewClient() {

    /**
     * Scope to launch coroutines to hold the bypass response
     */
    protected val coroutineScope : CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())



    /**
     * flag to know if the page has started and initialize the bypass
     */
    private var onPageStartedPassed = false



    /**
     * CountDownLatch to hold the bypass response in the thread that called it
     */
    protected val latch = CountDownLatch(1)



    /**
     * Interface to react to the bypass response
     */
    private val `interface` = CloudFlareByPassInterface(latch)

    /**
     * Deprecated used onPageStartedPassed instead
     *
     */
    @Deprecated("Deprecated used onPageStartedPassed instead", ReplaceWith("onPageStartedPassed"))
    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        /**
         * initialize the bypass only once
         */
        initializeByPass(view)

        /**
         * called by the override function to react to the bypass response
         */
        onPageStartedPassed(view, url, favicon)
    }


    /**
     * initializer
     */
    private fun initializeByPass(view: WebView?) {

        /**
         * check the boolean to know if the page has started
         */
        if (!onPageStartedPassed) {

            /**
             * set the flag to true
             */
            onPageStartedPassed = true


            /**
             * add the interface to the web view
             */
            view?.
                addJavascriptInterface(
                    `interface`,
                    "CloudFlareByPassInterface"
                )
        }
    }

    /**
     * override functions to replace onPageStarted
     */
    open fun onPageStartedPassed(view: WebView?, url: String?, favicon: Bitmap?) {}

    /**
     * override functions to react to the bypass response
     */
    open fun onPageFinishedByPassed(view: WebView?, url: String?) {}
}



/**
 * Base Client to setup the CloudFlare Bypass
 */
open class ByPassClient : BaseClient() {

    /**
     * Client that automatically by pass
     * the cloudflare challenge in case
     * is running
     */


    @Deprecated("Deprecated, use onPageFinishedByPassed instead", ReplaceWith("onPageFinishedByPassed"))
    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)

        /**
         * check if the page is a cloudflare challenge
         */
        when(view?.title?.isCloudFlareByPassTitle()) {


            /**
             * if the page is a cloudflare challenge, start the bypass
             */
            true -> {


                /**
                 * evaluate the javascript to start the bypass
                 */
                view.evaluateJavascript(Scripts.CLOUDFLARE_BYPASS)



                /**
                 * launch a coroutine to wait for the bypass response
                 */
                coroutineScope.launch(Dispatchers.IO) {


                    /**
                     * wait or block the thread for the bypass response for 15 seconds
                     * or bypass the challenge
                     */
                    latch.await(15, TimeUnit.SECONDS)



                    /**
                     * call the onPageFinishedPassed function on the ui thread
                     */
                    Thread.onUi { onPageFinishedByPassed(view, url) }
                }
            }


            /**
             * if the page is not a cloudflare challenge, call the onPageFinishedPassed function
             */
            else -> { onPageFinishedByPassed(view, url) }
        }
    }
}