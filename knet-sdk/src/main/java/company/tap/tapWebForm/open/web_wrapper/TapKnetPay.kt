package company.tap.tapWebForm.open.web_wrapper

import TapLocal
import TapTheme
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.http.SslError
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.view.*
import com.google.gson.Gson
import company.tap.tapWebForm.*
import company.tap.tapWebForm.open.DataConfiguration
import company.tap.tapWebForm.open.web_wrapper.enums.*
import company.tap.tapWebForm.open.web_wrapper.model.ThreeDsResponse
import company.tap.tapWebForm.open.web_wrapper.model.ThreeDsResponseCardPayButtons
import company.tap.tapWebForm.open.web_wrapper.pop_up_window.WebChrome
import company.tap.tapWebForm.open.web_wrapper.threeDsWebView.ThreeDsWebViewActivityButton
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.uikit.atoms.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.Headers
import okhttp3.Headers.Companion.toHeaders
import java.util.*


@SuppressLint("ViewConstructor")
class TapKnetPay : LinearLayout {
    lateinit var webviewStarterUrl: String
    lateinit var webViewScheme: String
    private lateinit var webChrome: WebChrome

    lateinit var webViewFrame: FrameLayout
    lateinit var urlToBeloaded: String
    var firstTimeOnReadyCallback = true
    var isCareemPayFirstEnterd = true

    companion object {
        lateinit var threeDsResponse: ThreeDsResponse
        lateinit var threeDsResponseCardPayButtons: ThreeDsResponseCardPayButtons

        private lateinit var knetWebView: WebView
        private lateinit var knetConfiguration: KnetConfiguration
        lateinit var buttonTypeConfigured: ThreeDsPayButtonType
        fun cancel() {
            knetWebView.loadUrl("javascript:window.cancel()")
        }

        fun generateTapAuthenticate(authIdPayerUrl: String) {
            knetWebView.loadUrl("javascript:window.loadAuthentication('$authIdPayerUrl')")
        }

        fun retrieve(value: String) {
            knetWebView.loadUrl("javascript:window.retrieve('$value')")
        }


    }

    /**
     * Simple constructor to use when creating a TapPayCardSwitch from code.
     *  @param context The Context the view is running in, through which it can
     *  access the current theme, resources, etc.
     **/
    constructor(context: Context) : super(context)

    /**
     *  @param context The Context the view is running in, through which it can
     *  access the current theme, resources, etc.
     *  @param attrs The attributes of the XML Button tag being used to inflate the view.
     *
     */
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)


    init {
        LayoutInflater.from(context).inflate(R.layout.activity_button_web_wrapper, this)
        initWebView()

    }


    private fun initWebView() {
        knetWebView = findViewById(R.id.webview)
        webViewFrame = findViewById(R.id.webViewFrame)

        with(knetWebView) {

            with(settings) {
                javaScriptEnabled = true
                domStorageEnabled = true
                javaScriptCanOpenWindowsAutomatically = true
                setSupportMultipleWindows(true)
                cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
                useWideViewPort = true
                loadWithOverviewMode = true

            }
        }
        knetWebView.setBackgroundColor(Color.TRANSPARENT)
        knetWebView.setLayerType(LAYER_TYPE_SOFTWARE, null)
        webChrome = WebChrome(context)
        knetWebView.webChromeClient = webChrome
        knetWebView.webViewClient = MyWebViewClient()


    }

    fun init(configuraton: KnetConfiguration, buttonType: ThreeDsPayButtonType?) {
        initializePaymentData(buttonType)
        if (buttonType != null) {
            buttonTypeConfigured = buttonType
        }
        knetConfiguration = configuraton
        applyTheme()
        when (configuraton) {
            KnetConfiguration.MapConfigruation -> {
                urlToBeloaded = "${webviewStarterUrl}${encodeConfigurationMapToUrl(DataConfiguration.configurationsAsHashMap)}"
                Log.e("url",urlToBeloaded.toString())

                knetWebView.loadUrl(urlToBeloaded)
            }
        }


    }

    private fun initializePaymentData(buttonType: ThreeDsPayButtonType?) {
        when (buttonType) {
            ThreeDsPayButtonType.KNET -> applySchemes(SCHEMES.KNET)
            ThreeDsPayButtonType.BENEFIT -> applySchemes(SCHEMES.BENEFIT)
            ThreeDsPayButtonType.FAWRY -> applySchemes(SCHEMES.FAWRY)
            ThreeDsPayButtonType.PAYPAL -> applySchemes(SCHEMES.PAYPAL)
            ThreeDsPayButtonType.TABBY -> applySchemes(SCHEMES.TABBY)
            ThreeDsPayButtonType.GOOGLEPAY -> applySchemes(SCHEMES.GOOGLE)
            ThreeDsPayButtonType.CAREEMPAY -> applySchemes(SCHEMES.CAREEMPAY)
            ThreeDsPayButtonType.VISA -> applySchemes(SCHEMES.VISA)
            ThreeDsPayButtonType.AMERICANEXPRESS -> applySchemes(SCHEMES.AMERICANEXPRESS)
            ThreeDsPayButtonType.MADA -> applySchemes(SCHEMES.MADA)
            ThreeDsPayButtonType.MASTERCARD -> applySchemes(SCHEMES.MASTERCARD)
            ThreeDsPayButtonType.CARD -> applySchemes(SCHEMES.CARD)


            else -> {}
        }
    }

    private fun applySchemes(scheme: SCHEMES) {
        webviewStarterUrl = scheme.value.first
        webViewScheme = scheme.value.second
    }


    private fun applyTheme() {
        /**
         * need to be refactored : mulitple copies of same code
         */
        when (knetConfiguration) {
            KnetConfiguration.MapConfigruation -> {
                val tapInterface =
                    DataConfiguration.configurationsAsHashMap?.get("interface") as? Map<*, *>
                setTapThemeAndLanguage(
                    this.context,
                    TapLocal.valueOf(tapInterface?.get("locale")?.toString() ?: TapLocal.en.name),
                    TapTheme.valueOf(tapInterface?.get("theme")?.toString() ?: TapTheme.light.name)
                )
            }
        }


    }

    private fun setTapThemeAndLanguage(
        context: Context,
        language: TapLocal?,
        themeMode: TapTheme?
    ) {
        when (themeMode) {
            TapTheme.light -> {
                DataConfiguration.setTheme(
                    context, context.resources, null,
                    R.raw.defaultlighttheme, TapTheme.light.name
                )
                ThemeManager.currentThemeName = TapTheme.light.name
            }

            TapTheme.dynamic -> {
                DataConfiguration.setTheme(
                    context, context.resources, null,
                    R.raw.defaultlighttheme, TapTheme.light.name
                )
                ThemeManager.currentThemeName = TapTheme.light.name
            }

            TapTheme.dark -> {
                DataConfiguration.setTheme(
                    context, context.resources, null,
                    R.raw.defaultdarktheme, TapTheme.dark.name
                )
                ThemeManager.currentThemeName = TapTheme.dark.name
            }

            else -> {}
        }
        when (language) {
            TapLocal.dynamic -> {
                /**
                 * needed to be dynamic
                 */
                DataConfiguration.setLocale(
                    this.context,
                    Locale.getDefault().language,
                    null,
                    this@TapKnetPay.context.resources,
                    R.raw.lang
                )
            }

            else -> {
                DataConfiguration.setLocale(
                    this.context,
                    language?.name ?: "en",
                    null,
                    this@TapKnetPay.context.resources,
                    R.raw.lang
                )
            }
        }


    }


    inner class MyWebViewClient : WebViewClient() {


        @RequiresApi(Build.VERSION_CODES.O)
        override fun shouldOverrideUrlLoading(
            webView: WebView?,
            request: WebResourceRequest?
        ): Boolean {

            /**
             * main checker if url start with "tapCardWebSDK://"
             */
            Log.e("url", request?.url.toString())

            /**
             * @TODO : make careempay open 3ds one time
             */

            if (request?.url.toString().startsWith(careemPayUrlHandler)) {
                threeDsResponse = ThreeDsResponse(
                    id = "",
                    url = request?.url.toString(),
                    powered = true
                )
                navigateTo3dsActivity(PaymentFlow.PAYMENTBUTTON.name)
                return true
            } else {
                if (request?.url.toString().startsWith(webViewScheme, ignoreCase = true)) {
                    if (request?.url.toString().contains(KnetStatusDelegate.onReady.name)) {


                        if (buttonTypeConfigured == ThreeDsPayButtonType.CARD) {
                            if (firstTimeOnReadyCallback) {
                                Thread.sleep(1500)
                                firstTimeOnReadyCallback = false
                            }
                            /**
                             *
                             *  todo enhance in a better way
                             */

                            //   val isFirstTime = Pref.getValue(context, "firstRun", "true").toString()
                            // if (isFirstTime == "true") {
                            //   webView?.clearView()
                            // knetWebView?.visibility= View.GONE
                            //  webView?.removeAllViews()
//                                Handler(Looper.getMainLooper()).postDelayed({
//                                    init(knetConfiguration,ThreeDsPayButtonType.CARD)
//                                                                            },
//                                    5000)
                            //  Pref.setValue(context, "firstRun", "false")
                        }


                        DataConfiguration.getTapKnetListener()?.onReady()

                    }
                    if (request?.url.toString().contains(KnetStatusDelegate.onSuccess.name)) {
                        DataConfiguration.getTapKnetListener()?.onSuccess(
                            request?.url?.getQueryParameterFromUri(keyValueName).toString()
                        )
                    }
                    if (request?.url.toString().contains(KnetStatusDelegate.onChargeCreated.name)) {

                        val data = request?.url?.getQueryParameterFromUri(keyValueName).toString()
                        Log.e("chargedData", data.toString())
                        val gson = Gson()
                        threeDsResponse = gson.fromJson(data, ThreeDsResponse::class.java)
                        if (buttonTypeConfigured == ThreeDsPayButtonType.CAREEMPAY){
                            threeDsResponse.stopRedirection = true
                        }
                        Log.e("threeDS", threeDsResponse.toString())


                        when (threeDsResponse.stopRedirection  ) {
                            false -> navigateTo3dsActivity(PaymentFlow.PAYMENTBUTTON.name)
                            else -> {}
                        }
                        DataConfiguration.getTapKnetListener()?.onChargeCreated(
                            request?.url?.getQueryParameterFromUri(keyValueName).toString()
                        )
                    }
                    if (request?.url.toString().contains(KnetStatusDelegate.onOrderCreated.name)) {
                        DataConfiguration.getTapKnetListener()
                            ?.onOrderCreated(
                                request?.url?.getQueryParameter(keyValueName).toString()
                            )
                    }
                    if (request?.url.toString().contains(KnetStatusDelegate.onClick.name)) {
                        DataConfiguration.getTapKnetListener()?.onClick()

                    }
                    if (request?.url.toString().contains(KnetStatusDelegate.cancel.name)) {
                        DataConfiguration.getTapKnetListener()?.cancel()
                    }
                    if (request?.url.toString()
                            .contains(KnetStatusDelegate.onBinIdentification.name)
                    ) {
                        DataConfiguration.getTapKnetListener()
                            ?.onBindIdentification(
                                request?.url?.getQueryParameterFromUri(keyValueName).toString()
                            )
                    }
                    if (request?.url.toString().contains(KnetStatusDelegate.onHeightChange.name)) {
                        val newHeight = request?.url?.getQueryParameter(keyValueName)
                        val params: ViewGroup.LayoutParams? = webViewFrame.layoutParams
                        params?.height =
                            webViewFrame.context.getDimensionsInDp(newHeight?.toInt() ?: 95)
                        webViewFrame.layoutParams = params

                        DataConfiguration.getTapKnetListener()?.onHeightChange(newHeight.toString())

                    }
                    if (request?.url.toString().contains(KnetStatusDelegate.on3dsRedirect.name)) {
                        /**
                         * navigate to 3ds Activity
                         */
                        val queryParams =
                            request?.url?.getQueryParameterFromUri(keyValueName).toString()
                        Log.e("data", queryParams.toString())

                        threeDsResponseCardPayButtons = queryParams.getModelFromJson()
                        navigateTo3dsActivity(PaymentFlow.CARDPAY.name)
                        Log.e("data", threeDsResponseCardPayButtons.toString())


                    }
                    /**
                     * for google button specifically
                     */
                    if (request?.url.toString().contains(KnetStatusDelegate.onClosePopup.name)) {
                        webChrome.getdialog()?.dismiss()

                    }

                    if (request?.url.toString().contains(KnetStatusDelegate.onError.name)) {
                        DataConfiguration.getTapKnetListener()
                            ?.onError(
                                request?.url?.getQueryParameterFromUri(keyValueName).toString()
                            )
                    }
                    if (request?.url.toString().contains(KnetStatusDelegate.onError.name)) {
                        DataConfiguration.getTapKnetListener()
                            ?.onError(
                                request?.url?.getQueryParameterFromUri(keyValueName).toString()
                            )
                    }


                    return true
                } else {
                    return false
                }
            }
        }

        override fun onReceivedSslError(
            view: WebView?,
            handler: SslErrorHandler?,
            error: SslError?
        ) {
            super.onReceivedSslError(view, handler, error)
        }


        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)


        }

        fun navigateTo3dsActivity(paymentbutton: String) {
            val intent = Intent(context, ThreeDsWebViewActivityButton()::class.java)
            ThreeDsWebViewActivityButton.tapKnetPay = this@TapKnetPay
            intent.putExtra("flow", paymentbutton)
            (context).startActivity(intent)
        }


        override fun shouldInterceptRequest(
            view: WebView?,
            request: WebResourceRequest?
        ): WebResourceResponse? {
            return super.shouldInterceptRequest(view, request)
        }

        override fun onReceivedError(
            view: WebView,
            request: WebResourceRequest,
            error: WebResourceError
        ) {
            super.onReceivedError(view, request, error)

        }
    }


    override fun onDetachedFromWindow() {
        knetWebView.destroy()
        super.onDetachedFromWindow()
    }
}


enum class KnetConfiguration() {
    MapConfigruation
}

enum class PaymentFlow {
    CARDPAY, PAYMENTBUTTON
}




