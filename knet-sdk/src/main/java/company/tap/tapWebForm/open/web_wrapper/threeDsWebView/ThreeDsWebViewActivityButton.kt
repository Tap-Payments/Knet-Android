package company.tap.tapWebForm.open.web_wrapper.threeDsWebView

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import company.tap.tapWebForm.R
import company.tap.tapWebForm.doAfterSpecificTime
import company.tap.tapWebForm.getDeviceSpecs
import company.tap.tapWebForm.open.DataConfiguration
import company.tap.tapWebForm.open.web_wrapper.*
import company.tap.tapWebForm.open.web_wrapper.enums.redirectKey
import company.tap.tapWebForm.open.web_wrapper.enums.urlKey
import company.tap.taplocalizationkit.LocalizationManager
import java.util.*

class ThreeDsWebViewActivityButton : AppCompatActivity() {
    lateinit var threeDsBottomsheet: BottomSheetDialogFragment
    var loadedBottomSheet = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_three_ds_web_view)
        LocalizationManager.setLocale(this, Locale(DataConfiguration.lanuage.toString()))
        val webView = WebView(this)
        webView.layoutParams = this.getDeviceSpecs().first.let {
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                it
            )
        }

        webView.settings.javaScriptEnabled = true
        webView.isVerticalScrollBarEnabled = true
        webView.requestFocus()
        webView.webViewClient = threeDsWebViewClient()
        webView.loadUrl(TapKnetPay.threeDsResponse.url)
        threeDsBottomsheet = ThreeDsBottomSheetFragmentButton(webView, onCancel = {
            TapKnetPay.cancel()
            DataConfiguration.getTapKnetListener()?.cancel()
        })

    }

    inner class threeDsWebViewClient : WebViewClient() {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun shouldOverrideUrlLoading(
            webView: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            webView?.loadUrl(request?.url.toString())
            Log.e("3dsurl", request?.url.toString())
            val Redirect =
                DataConfiguration.configurationsAsHashMap?.get(redirectKey) as HashMap<*, *>
            val redirect = Redirect.get(urlKey)
            Log.e("redirect", redirect.toString())

            when (request?.url?.toString()?.contains(redirect.toString(), ignoreCase = true)) {
                true -> {
                    threeDsBottomsheet.dialog?.dismiss()
                    val splittiedString = request.url.toString().split(redirect.toString() + "?", ignoreCase = true)
                    Log.e("splitted", splittiedString.toString())
                    try {
                        TapKnetPay.retrieve(splittiedString[1])
                    } catch (e: Exception) {
                        DataConfiguration.getTapKnetListener()?.onError(e.message.toString())
                    }
                }
                false -> {}
                else -> {}
            }
            return true

        }

        override fun onPageFinished(view: WebView, url: String) {
            if (loadedBottomSheet) {

                return
            } else {
                doAfterSpecificTime(time = 5000) {
                    threeDsBottomsheet.show(supportFragmentManager, "")
                }
            }
            loadedBottomSheet = true
        }

        override fun onReceivedError(
            view: WebView,
            request: WebResourceRequest,
            error: WebResourceError
        ) {
            super.onReceivedError(view, request, error)
        }
    }


}
