package company.tap.tapWebForm.open.web_wrapper.threeDsWebView

import android.annotation.SuppressLint
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
import company.tap.tapWebForm.open.KnetDataConfiguration
import company.tap.tapWebForm.open.web_wrapper.*
import company.tap.tapWebForm.open.web_wrapper.enums.redirectKey
import company.tap.tapWebForm.open.web_wrapper.enums.urlKey
import company.tap.taplocalizationkit.LocalizationManager
import java.util.*

const val delayTime = 5000L

class ThreeDsWebViewActivityButton : AppCompatActivity() {
    lateinit var threeDsBottomsheet: BottomSheetDialogFragment
    lateinit var paymentFlow: String
    var loadedBottomSheet = false

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var tapKnetPay: TapKnetPay
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_three_ds_web_view)
        LocalizationManager.setLocale(this, Locale(KnetDataConfiguration.lanuage.toString()))
        val webView = WebView(this)
        webView.layoutParams = this.getDeviceSpecs().first.let {
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                it
            )
        }

        with(webView.settings) {
            javaScriptEnabled = true
            domStorageEnabled = true

        }
        webView.isVerticalScrollBarEnabled = true
        webView.requestFocus()
        webView.webViewClient = threeDsWebViewClient()
        val data = intent.extras
        paymentFlow = data?.getString("flow") ?: PaymentFlow.PAYMENTBUTTON.name
        when (paymentFlow) {
            PaymentFlow.PAYMENTBUTTON.name -> {
                webView.loadUrl(TapKnetPay.threeDsResponse.url)
            }

            PaymentFlow.CARDPAY.name -> {
                webView.loadUrl(TapKnetPay.threeDsResponseCardPayButtons.threeDsUrl)

            }
        }

        threeDsBottomsheet = ThreeDsBottomSheetFragmentButton(webView, onCancel = {
            when (paymentFlow) {
                PaymentFlow.PAYMENTBUTTON.name -> {
                    TapKnetPay.cancel()
                }

                PaymentFlow.CARDPAY.name -> {
                    TapKnetPay.cancel()
                  /*  tapKnetPay.init(
                        KnetConfiguration.MapConfigruation,
                        TapKnetPay.buttonTypeConfigured
                    )*/ //SToped cardpay for now

                }
            }
            KnetDataConfiguration.getTapKnetListener()?.onKnetcancel()
        })

    }

    inner class threeDsWebViewClient : WebViewClient() {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun shouldOverrideUrlLoading(
            webView: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            Log.e("3dsUrl", request?.url.toString())
            when (paymentFlow) {
                PaymentFlow.PAYMENTBUTTON.name -> {
                    webView?.loadUrl(request?.url.toString())
                    val Redirect = KnetDataConfiguration.configurationsAsHashMap?.get(redirectKey) as HashMap<*, *>
                    val redirect = Redirect.get(urlKey)
                    when (request?.url?.toString()?.contains(redirect.toString(), ignoreCase = true)) {
                        true -> {
                            threeDsBottomsheet.dialog?.dismiss()
                            val splittiedString = request.url.toString().split("?", ignoreCase = true)
                            Log.e("splittedString", splittiedString.toString())
                            try {
                                TapKnetPay.retrieve(splittiedString[1])
                            } catch (e: Exception) {
                                KnetDataConfiguration.getTapKnetListener()
                                    ?.onKnetError(e.message.toString())
                            }
                        }

                        false -> {}
                        else -> {}
                    }
                }

                PaymentFlow.CARDPAY.name -> {
                    when (request?.url?.toString()
                        ?.contains(TapKnetPay.threeDsResponseCardPayButtons.keyword)) {
                        true -> {
                            threeDsBottomsheet.dialog?.dismiss()
                            val splittiedString = request.url.toString().split("?")

                            Log.e("splitted", splittiedString.toString())
                            TapKnetPay.generateTapAuthenticate(request.url?.toString() ?: "")
                        }

                        false -> {}
                        else -> {}
                    }
                }
            }

            return true

        }

        override fun onPageFinished(view: WebView, url: String) {
            if (loadedBottomSheet) {
                return
            } else {
                doAfterSpecificTime(time = delayTime) {
                    if (!supportFragmentManager.isDestroyed){
                        threeDsBottomsheet.show(supportFragmentManager, "")
                    }
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
