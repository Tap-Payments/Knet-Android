package company.tap.tapWebForm.open.web_wrapper

import Headers
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.http.SslError
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.view.*
import com.google.gson.Gson
import company.tap.tapWebForm.*
import company.tap.tapWebForm.open.RedirectDataConfiguration
import company.tap.tapWebForm.open.web_wrapper.enums.*
import company.tap.tapWebForm.open.web_wrapper.model.ThreeDsResponse
import company.tap.tapWebForm.open.web_wrapper.model.ThreeDsResponseCardPayButtons
import company.tap.tapWebForm.open.web_wrapper.pop_up_window.WebChrome
import company.tap.tapWebForm.open.web_wrapper.threeDsWebView.ThreeDsWebViewActivityButton
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*


@SuppressLint("ViewConstructor")
class TapRedirectPay : LinearLayout {
    lateinit var webviewStarterUrl: String

    // lateinit var webViewScheme: String
    var webViewScheme: String = "tapbuttonsdk://"
    private lateinit var webChrome: WebChrome

    lateinit var webViewFrame: FrameLayout
    lateinit var urlToBeloaded: String
    var firstTimeOnReadyCallback = true

    companion object {
        lateinit var threeDsResponse: ThreeDsResponse
        lateinit var threeDsResponseCardPayButtons: ThreeDsResponseCardPayButtons

        private lateinit var redirectWebView: WebView

        lateinit var buttonTypeConfigured: ThreeDsPayButtonType
        fun cancel() {
            redirectWebView.loadUrl("javascript:window.cancel()")
        }

        fun generateTapAuthenticate(authIdPayerUrl: String) {
            redirectWebView.loadUrl("javascript:window.loadAuthentication('$authIdPayerUrl')")
        }

        fun retrieve(value: String) {
            redirectWebView.loadUrl("javascript:window.retrieve('$value')")
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


    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        redirectWebView = findViewById(R.id.webview)
        webViewFrame = findViewById(R.id.webViewFrame)


        with(redirectWebView) {

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
        redirectWebView.setBackgroundColor(Color.TRANSPARENT)
        redirectWebView.setLayerType(LAYER_TYPE_SOFTWARE, null)
        webChrome = WebChrome(context)
        redirectWebView.webChromeClient = webChrome
        redirectWebView.webViewClient = MyWebViewClient()


    }


    private fun callIntentAPI(configuraton: java.util.HashMap<String, Any>, headers: Headers) {
        try {
            val intentObj = configuraton?.get("intent") as HashMap<*, *>
            val intentID = intentObj?.get("intent")


            val baseURL = "https://mw-sdk.dev.tap.company/v2/intent/" + intentID + "/sdk"
            val builder: OkHttpClient.Builder = OkHttpClient().newBuilder()
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            builder.addInterceptor(interceptor)
            val operator = configuraton?.get(operatorKey) as HashMap<*, *>
            val publickKey = operator.get(publicKeyToGet)

            println("publickKey>>" + publickKey)


            val jsonObject: JSONObject = JSONObject()
            try {
                jsonObject.put("type", "button-android")
                jsonObject.put("version", "2.0.0")
                jsonObject.put("mdn", headers.mdn.toString())
                jsonObject.put("application", headers.application.toString())
            } catch (e: JSONException) {
                e.printStackTrace();
            }


            val mediaType = "application/json; charset=utf-8".toMediaType()
            val body = jsonObject.toString().toRequestBody(mediaType)
            val okHttpClient: OkHttpClient = builder.build()
            val request: Request = Request.Builder()
                .url(baseURL)
                .method("PUT", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", publickKey.toString())
                .addHeader("mdn", headers.mdn.toString().trim())
                .build()
            okHttpClient.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    try {
                        var responseBody: JSONObject? =
                            response.body?.string()
                                ?.let { JSONObject(it) } // toString() is not the response body, it is a debug representation of the response body

                        if (!responseBody.toString().contains("errors")) {
                            var intentIdResponse = responseBody?.getString("id")
                            if (intentIdResponse != null) {
                                // knetWebView.loadUrl(redirectURL)
                                urlToBeloaded =
                                    "https://button.dev.tap.company/?intentId=" + intentIdResponse + "&publicKey=" + publickKey.toString() + "&mdn=" + toBase64(headers.mdn.toString()) + "&platform=mobile"

                                Handler(Looper.getMainLooper()).post {
                                    redirectWebView.loadUrl(urlToBeloaded)

                                }
                            }

                            println("ButtonURL >>"+urlToBeloaded)
                        } else {


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

    fun init(configuraton: java.util.HashMap<String, Any>, headers: Headers) {
        //  initializePaymentData(buttonType)
        /**
         * Check for data in configuration has operator and intent id
         * else sends error
         * */

        val intentObj = configuraton?.get(intentKey) as HashMap<*, *>
        val intentID = intentObj?.get(intentKey)

        val operator = configuraton?.get(operatorKey) as HashMap<*, *>
        val publickKey = operator.get(publicKeyToGet)

        if (intentID.toString().isNullOrBlank() || publickKey.toString().isNullOrBlank()) {
            RedirectDataConfiguration.getTapKnetListener()
                ?.onRedirectError("public key and intent id are required")
        } else {
            callIntentAPI(configuraton, headers)
        }



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


    inner class MyWebViewClient : WebViewClient() {


        @RequiresApi(Build.VERSION_CODES.O)
        override fun shouldOverrideUrlLoading(
            webView: WebView?,
            request: WebResourceRequest?
        ): Boolean {

            /**
             * main checker if url start with "tapCardWebSDK://"
             */
            Log.e("url Here", request?.url.toString())


            if (request?.url.toString().startsWith(careemPayUrlHandler)) {
                webViewFrame.layoutParams =
                    LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
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


                       /* if (buttonTypeConfigured == ThreeDsPayButtonType.CARD) {
                            if (firstTimeOnReadyCallback) {
                                Thread.sleep(1500)
                                firstTimeOnReadyCallback = false
                            }
                            *//**
                             *
                             *  todo enhance in a better way
                             *//*


                        }*/


                        RedirectDataConfiguration.getTapKnetListener()?.onRedirectReady()

                    }
                    if (request?.url.toString().contains(KnetStatusDelegate.onSuccess.name)) {
                        var datafromUrl = request?.url?.getQueryParameter(keyValueName).toString()
                        println("datafromUrl>>"+datafromUrl)
                        var decoded = decodeBase64(datafromUrl)
                        println("decoded>>"+decoded)
                        if (decoded != null) {
                            RedirectDataConfiguration.getTapKnetListener()?.onRedirectSuccess(
                                decoded
                            )
                        }
                    }
                    if (request?.url.toString().contains(KnetStatusDelegate.onChargeCreated.name)) {

                        val data = decodeBase64(request?.url?.getQueryParameter(keyValueName).toString())
                        Log.e("chargedData", data.toString())
                        val gson = Gson()
                        threeDsResponse = gson.fromJson(data, ThreeDsResponse::class.java)
                        when (threeDsResponse.stopRedirection) {
                            false -> navigateTo3dsActivity(PaymentFlow.PAYMENTBUTTON.name)
                            else -> {}
                        }
                        RedirectDataConfiguration.getTapKnetListener()?.onRedirectChargeCreated(
                            request?.url?.getQueryParameterFromUri(keyValueName).toString()
                        )
                    }
                    if (request?.url.toString().contains(KnetStatusDelegate.onOrderCreated.name)) {
                        val orderResponse = request?.url?.getQueryParameter(keyValueName).toString()
                        println("orderResponse>>"+orderResponse)
                        //TODO check if decode required
                        RedirectDataConfiguration.getTapKnetListener()
                                    ?.onRedirectOrderCreated(
                                        orderResponse
                                    )



                    }
                    if (request?.url.toString().contains(KnetStatusDelegate.onClick.name)) {
                        RedirectDataConfiguration.getTapKnetListener()?.onRedirectClick()

                    }
                    if (request?.url.toString().contains(KnetStatusDelegate.cancel.name)) {
                        RedirectDataConfiguration.getTapKnetListener()?.onRedirectcancel()
                    }
                    if (request?.url.toString()
                            .contains(KnetStatusDelegate.onBinIdentification.name)
                    ) {
                        RedirectDataConfiguration.getTapKnetListener()
                            ?.onRedirectBindIdentification(
                                request?.url?.getQueryParameterFromUri(keyValueName).toString()
                            )
                    }
                    if (request?.url.toString().contains(KnetStatusDelegate.onHeightChange.name)) {
                        val newHeight = request?.url?.getQueryParameter(keyValueName)
                        val params: ViewGroup.LayoutParams? = webViewFrame.layoutParams
                        params?.height =
                            webViewFrame.context.getDimensionsInDp(newHeight?.toInt() ?: 95)
                        webViewFrame.layoutParams = params

                        RedirectDataConfiguration.getTapKnetListener()
                            ?.onRedirectHeightChange(newHeight.toString())

                    }
                    if (request?.url.toString().contains(KnetStatusDelegate.on3dsRedirect.name)) {
                        /**
                         * navigate to 3ds Activity
                         */
                        val queryParams =
                            request?.url?.getQueryParameterFromUri(keyValueName).toString()
                        Log.e("data card", queryParams.toString())

                        threeDsResponseCardPayButtons = queryParams.getModelFromJson()
                        navigateTo3dsActivity(PaymentFlow.CARDPAY.name)
                        Log.e("data card", threeDsResponseCardPayButtons.toString())


                    }
                    /**
                     * for google button specifically
                     */
                    if (request?.url.toString().contains(KnetStatusDelegate.onClosePopup.name)) {
                        webChrome.getdialog()?.dismiss()

                    }

                   /* if (request?.url.toString().contains(KnetStatusDelegate.onError.name)) {

                        RedirectDataConfiguration.getTapKnetListener()
                            ?.onRedirectError(
                                request?.url?.getQueryParameterFromUri(keyValueName).toString()
                            )
                    }*/
                    if (request?.url.toString().contains(KnetStatusDelegate.onError.name)) {
                        decodeBase64(request?.url?.getQueryParameter(keyValueName).toString())?.let {
                            RedirectDataConfiguration.getTapKnetListener()
                                ?.onRedirectError(
                                    it
                                )
                        }


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
            ThreeDsWebViewActivityButton.tapRedirectPay = this@TapRedirectPay
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
        redirectWebView.destroy()
        super.onDetachedFromWindow()
    }



    fun toBase64(value: String?): String? {
        var value = value
        if (value == null) value = ""
        return Base64.encodeToString(value.trim { it <= ' ' }.toByteArray(), Base64.DEFAULT)
    }
    fun decodeBase64(base64String: String): String? {
        return try {
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            String(decodedBytes, Charsets.UTF_8) // Convert bytes to string using UTF-8
        } catch (e: IllegalArgumentException) {
            println("Invalid Base64 input: ${e.message}")
            null
        }
    }
}
enum class KnetConfiguration() {
    MapConfigruation
}

enum class PaymentFlow {
    CARDPAY, PAYMENTBUTTON
}




