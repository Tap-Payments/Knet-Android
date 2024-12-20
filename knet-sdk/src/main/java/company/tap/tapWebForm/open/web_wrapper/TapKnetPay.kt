package company.tap.tapWebForm.open.web_wrapper

import TapLocal
import TapTheme
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.http.SslError
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.view.*
import com.google.gson.Gson
import company.tap.tapWebForm.*
import company.tap.tapWebForm.open.KnetDataConfiguration
import company.tap.tapWebForm.open.web_wrapper.enums.*
import company.tap.tapWebForm.open.web_wrapper.model.ThreeDsResponse
import company.tap.tapWebForm.open.web_wrapper.model.ThreeDsResponseCardPayButtons
import company.tap.tapWebForm.open.web_wrapper.pop_up_window.WebChrome
import company.tap.tapWebForm.open.web_wrapper.threeDsWebView.ThreeDsWebViewActivityButton
import company.tap.tapuilibrary.themekit.ThemeManager
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*


@SuppressLint("ViewConstructor")
class TapKnetPay : LinearLayout {
    lateinit var webviewStarterUrl: String
    lateinit var webViewScheme: String
    private lateinit var webChrome: WebChrome

    lateinit var webViewFrame: FrameLayout
    lateinit var urlToBeloaded: String
    var firstTimeOnReadyCallback = true

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

    private fun callConfigAPI(configuraton: java.util.HashMap<String, Any>) {
        try {
            val baseURL = "https://mw-sdk.dev.tap.company/v2/button/config "
            val builder: OkHttpClient.Builder = OkHttpClient().newBuilder()
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            builder.addInterceptor(interceptor)

            val body = (configuraton as Map<*, *>?)?.let { JSONObject(it).toString().toRequestBody("application/json".toMediaTypeOrNull()) }
            val okHttpClient: OkHttpClient = builder.build()
            val request: Request = Request.Builder()
                .url(baseURL )
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer sk_test_bNgRpokWMylX3CBJ6FOresTq")
                .build()
            okHttpClient.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    try {
                        var responseBody: JSONObject? =
                            response.body?.string()?.let { JSONObject(it) } // toString() is not the response body, it is a debug representation of the response body

                       if(!responseBody.toString().contains("errors")){
                           var redirectURL = responseBody?.getString("redirect_url")
                            if (redirectURL != null) {
                               // knetWebView.loadUrl(redirectURL)
                                urlToBeloaded = redirectURL
                                Handler(Looper.getMainLooper()).post {
                                    knetWebView.loadUrl(redirectURL)

                                }
                            }
                        }else{


                        }

                        } catch (ex: JSONException) {
                            throw RuntimeException(ex)
                        } catch (ex: IOException) {
                            throw RuntimeException(ex)
                        }

                }

                override fun onFailure(call: Call, e: IOException) {}
            })
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

    }

   // fun init(configuraton: KnetConfiguration, buttonType: ThreeDsPayButtonType?) {
    fun init(configuraton: java.util.HashMap<String, Any>, buttonType: ThreeDsPayButtonType?) {
        initializePaymentData(buttonType)
        if (buttonType != null) {
            buttonTypeConfigured = buttonType
        }
       // knetConfiguration = configuraton
     //   applyTheme()
       callConfigAPI(configuraton)


        when (configuraton) {

           // KnetConfiguration.MapConfigruation -> {

               /* urlToBeloaded =
                    "${webviewStarterUrl}${encodeConfigurationMapToUrl(KnetDataConfiguration.configurationsAsHashMap)}"*/
               // knetWebView.loadUrl(urlToBeloaded)
           // }


        }
    //    Log.e("urlToBeloaded",urlToBeloaded)

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
            ThreeDsPayButtonType.SAMSUNGPAY -> applySchemes(SCHEMES.SAMSUNGPAY)
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
                    KnetDataConfiguration.configurationsAsHashMap?.get("interface") as? Map<*, *>
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
                KnetDataConfiguration.setTheme(
                    context, context.resources, null,
                    R.raw.defaultlighttheme, TapTheme.light.name
                )
                ThemeManager.currentThemeName = TapTheme.light.name
            }

            TapTheme.dynamic -> {
                KnetDataConfiguration.setTheme(
                    context, context.resources, null,
                    R.raw.defaultlighttheme, TapTheme.light.name
                )
                      ThemeManager.currentThemeName = TapTheme.light.name
            }

            TapTheme.dark -> {
                KnetDataConfiguration.setTheme(
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
                KnetDataConfiguration.setLocale(
                    this.context,
                    Locale.getDefault().language,
                    null,
                    this@TapKnetPay.context.resources,
                    R.raw.lang
                )
            }

            else -> {
                KnetDataConfiguration.setLocale(
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


            if (request?.url.toString().startsWith(careemPayUrlHandler)) {
                webViewFrame.layoutParams = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT)
                threeDsResponse = ThreeDsResponse(
                    id = "",
                    url = request?.url.toString(),
                    powered = true,
                    stopRedirection = false
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


                        KnetDataConfiguration.getTapKnetListener()?.onKnetReady()

                    }
                    if (request?.url.toString().contains(KnetStatusDelegate.onSuccess.name)) {
                        KnetDataConfiguration.getTapKnetListener()?.onKnetSuccess(
                            request?.url?.getQueryParameterFromUri(keyValueName).toString()
                        )
                    }
                    if (request?.url.toString().contains(KnetStatusDelegate.onChargeCreated.name)) {

                        val data = request?.url?.getQueryParameterFromUri(keyValueName).toString()
                        Log.e("chargedData", data.toString())
                        val gson = Gson()
                        threeDsResponse = gson.fromJson(data, ThreeDsResponse::class.java)
                        when (threeDsResponse.stopRedirection) {
                            false -> navigateTo3dsActivity(PaymentFlow.PAYMENTBUTTON.name)
                            else -> {}
                        }
                        KnetDataConfiguration.getTapKnetListener()?.onKnetChargeCreated(
                            request?.url?.getQueryParameterFromUri(keyValueName).toString()
                        )
                    }
                    if (request?.url.toString().contains(KnetStatusDelegate.onOrderCreated.name)) {
                        KnetDataConfiguration.getTapKnetListener()
                            ?.onKnetOrderCreated(
                                request?.url?.getQueryParameter(keyValueName).toString()
                            )
                    }
                    if (request?.url.toString().contains(KnetStatusDelegate.onClick.name)) {
                        KnetDataConfiguration.getTapKnetListener()?.onKnetClick()

                    }
                    if (request?.url.toString().contains(KnetStatusDelegate.cancel.name)) {
                        KnetDataConfiguration.getTapKnetListener()?.onKnetcancel()
                    }
                    if (request?.url.toString()
                            .contains(KnetStatusDelegate.onBinIdentification.name)
                    ) {
                        KnetDataConfiguration.getTapKnetListener()
                            ?.onKnetBindIdentification(
                                request?.url?.getQueryParameterFromUri(keyValueName).toString()
                            )
                    }
                    if (request?.url.toString().contains(KnetStatusDelegate.onHeightChange.name)) {
                        val newHeight = request?.url?.getQueryParameter(keyValueName)
                        val params: ViewGroup.LayoutParams? = webViewFrame.layoutParams
                        params?.height =
                            webViewFrame.context.getDimensionsInDp(newHeight?.toInt() ?: 95)
                        webViewFrame.layoutParams = params

                        KnetDataConfiguration.getTapKnetListener()?.onKnetHeightChange(newHeight.toString())

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
                        KnetDataConfiguration.getTapKnetListener()
                            ?.onKnetError(
                                request?.url?.getQueryParameterFromUri(keyValueName).toString()
                            )
                    }
                    if (request?.url.toString().contains(KnetStatusDelegate.onError.name)) {
                        KnetDataConfiguration.getTapKnetListener()
                            ?.onKnetError(
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




