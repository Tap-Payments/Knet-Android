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
                javaScriptCanOpenWindowsAutomatically  =true
                allowUniversalAccessFromFileURLs = true
                allowFileAccessFromFileURLs = true
                allowContentAccess = true
                allowFileAccess= true
                setSupportMultipleWindows(true)
                cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
                mixedContentMode = 0
                useWideViewPort = true
                loadWithOverviewMode = true
                builtInZoomControls = true
                displayZoomControls = true
                setSupportZoom(true)
                defaultTextEncodingName ="utf-8"
                databaseEnabled = true

                pluginState = WebSettings.PluginState.ON
            }
        }
        knetWebView.setBackgroundColor(Color.TRANSPARENT)


        knetWebView.setLayerType(LAYER_TYPE_SOFTWARE, null)
        webChrome = WebChrome(context, reinitialize = {
            webChrome.getdialog()?.dismiss()
            knetWebView.reload()
        })
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
                urlToBeloaded =
                    "${webviewStarterUrl}${encodeConfigurationMapToUrl(DataConfiguration.configurationsAsHashMap)}"
              //  urlToBeloaded = "https://button.dev.tap.company/wrapper/card?configurations=%7B%22operator%22%3A%7B%22hashString%22%3A%22%22%2C%22publicKey%22%3A%22pk_test_6jdl4Qo0FYOSXmrZTR1U5EHp%22%7D%2C%22order%22%3A%7B%22reference%22%3A%22%22%2C%22amount%22%3A%221%22%2C%22metadata%22%3A%7B%22id%22%3A%22%22%7D%2C%22description%22%3A%22%22%2C%22currency%22%3A%22BHD%22%2C%22id%22%3A%22%22%7D%2C%22customer%22%3A%7B%22contact%22%3A%7B%22phone%22%3A%7B%22number%22%3A%22011%22%2C%22countryCode%22%3A%22%2B20%22%7D%2C%22id%22%3A%22%22%2C%22email%22%3A%22test%40gmail.com%22%7D%2C%22name%22%3A%5B%7B%22middle%22%3A%22middle%22%2C%22last%22%3A%22last%22%2C%22lang%22%3A%22en%22%2C%22first%22%3A%22first%22%7D%5D%2C%22id%22%3A%22%22%7D%2C%22merchant%22%3A%7B%22id%22%3A%22%22%7D%2C%22invoice%22%3A%7B%22id%22%3A%22%22%7D%2C%22interface%22%3A%7B%22loader%22%3Atrue%2C%22edges%22%3A%22flat%22%2C%22colorStyle%22%3A%22colored%22%2C%22theme%22%3A%22dynamic%22%2C%22locale%22%3A%22dynamic%22%7D%2C%22post%22%3A%7B%22url%22%3A%22%22%7D%2C%22redirect%22%3A%7B%22url%22%3A%22onTapRedirect%3A%2F%2F%22%7D%2C%22scope%22%3A%22charge%22%2C%22transaction%22%3A%7B%22reference%22%3A%22trx%22%2C%22metadata%22%3A%7B%22id%22%3A%22%22%7D%2C%22authenticate%22%3A%7B%22id%22%3A%22%22%2C%22required%22%3Atrue%7D%2C%22source%22%3A%7B%22id%22%3A%22%22%7D%2C%22authorize%22%3A%7B%22time%22%3A%2212%22%2C%22type%22%3A%22VOID%22%7D%2C%22authentication%22%3Atrue%7D%2C%22acceptance%22%3A%7B%22supportedSchemes%22%3A%5B%5D%2C%22supportedFundSource%22%3A%5B%5D%2C%22supportedPaymentAuthentications%22%3A%5B%5D%7D%2C%22fieldVisibility%22%3A%7B%22card%22%3A%7B%22cvv%22%3Atrue%2C%22cardHolder%22%3Atrue%7D%7D%2C%22features%22%3A%7B%22customerCards%22%3A%7B%22saveCard%22%3Atrue%2C%22autoSaveCard%22%3Atrue%7D%2C%22acceptanceBadge%22%3Atrue%7D%2C%22headers%22%3A%7B%22application%22%3A%22cu%5Cu003dsOLo20mRNqYHIqAbUMxZysoEGztHUYPBQXRuvBO0M%2FBt2kp%2BgGxy3X7m0oR1fpKLmCF8zyedhf96%2Fg7%2F0Ct7SeP%2BG0lGs3SPIBwGvPfZnTfuounipC2tDtByRjiVxmC49hFsF0xV6gp2au5lVFG9jEjW7G9JOE0XVWfdei111VI%5Cu003d%7Caid%5Cu003djOQaSWA3VD%2FwmmOGDrAFHghykUbe12Ic5rqTAVDYuSZnvg8aiM4zc7hw3pBabXOnUNimd1bCJ6lmeiZpaegfx37%2B%2BhxFbyWPBjtbquRKmZVXa8%2Bw7z9RMGWz5rJaNxLN16pLK4K1Jgv34NlhpNGwRpx7RGCSMBp9MXhFBmpEY3U%5Cu003d%7Cat%5Cu003dDobihCKS4zdl5k6eAL6I09b%2BZ6mzmFW4ncrk9bTsNbwqCqp%2BpaKpAm00ahg3iC03Sx4Axt4uzAYTUa%2F8wqKwbpMDHLK1HPLaVzY%2BANkZjQyQmJBGC1qCWxq2QMONESxin9200sS3ujhrCzMCrnqGKtf%2BEx61pzLQt41GpuiQvFc%5Cu003d%7Cav%5Cu003diNMQNw6A5CgV%2BVbrp73op0SB0W16qVIdZfXVd9rjyh1KmutzcKEE32TPgWT1LdAqHWr3MOta0NYTq3jn2MyF6e4azysRtUmYHsv49aIQj1f0enVggtr7ZswTVkwfpwDmGiC9UwVrPsDYCoLgl6oBTYgjW6B2Ry23StEoUT8Ojzw%5Cu003d%7Crn%5Cu003dE5rlhw0vSohysxDlfFciChEbKQfMYwv11BkcPjbengwZWsFKhiavJYaMOjM0LCP80xamUwxYZuAqrIA4Mm9fFLQzqM4SdGBIbupB%2BBlcqAQxnmo6fDl%2Bet5uAvvb7ZkMdAQuwxI%2BLXeeFHQVDZ0WRVKX2pF0Jzqtrl9GQghoedE%5Cu003d%7Crt%5Cu003dW2E%2F%2BxzEJT0f7TiWwV6k9UMJgG3cXMYThi%2FGsCFiipcznmLUgB92I6md7Os%2FcUdZ%2BsusZIkrdC5%2BAmLW4vCb74mCNM%2F8%2FOqVnjwTTVeAj%2Bl4V90mJ45oAZHUII%2BbIsqcBGAjNRlkD%2B4%2F6UQellfhW2EJzjxY77ZWi%2FslnMmqFWU%5Cu003d%7Crb%5Cu003daCYQRxYZ5Ov518R0PeFpWtqgVpUmQaXEb%2Fw96g0BHaKhcBS0sffol%2BnUtvYEW84ju9VQuVX28%2Fpsc5r5%2FeugW%2B4F2hYI9WGWn2D5TaHbnDQT2ydhhNQ5CxbYSLAaXKIsH7sdmmBgHMcmFQFvohho8zqcjgeuJarL0ckGkvIimA8%5Cu003d%7Crm%5Cu003dmwoEkupM6VEX9y34TxZwXdQpQNLAX%2BV9LiOdD9RGkPoLprE8F1BmA2OVnLKwt5bgvHmzxrei%2BdU5ZooXCN4%2BdGOSZVcISSpXRotwWc7jUHnIBjN02vwMxmL4QVnT9IqYWC4cAmc1e9XQvBpYFngbLNKCcyeN6hKIXQmjwaYE9OE%5Cu003d%7Cro%5Cu003dTQRZOWbswjpAJBnGxI%2F77CvCIo80R1nCiHFApP%2F1rfN9pRg4yw0gKLxROsq3yt%2B5X%2B57RFbENhKJsKu53DgL6ZwiK8r52Xsa4aoicetjnSyrLZZy1Ubi9ILCPzSQHRwIxSad3OVhJM%2B47Du4zYl0%2BHNFScPmD0aH1ZECoIz1JYk%5Cu003d%7Crov%5Cu003dNpOJKlTM6h8jgTulxDAOYPjlB%2BpchInFJkios3ofQ2i%2F05SkPeyvRJwPBCCczMPdtqelluahQM1tjmdKK8ZMe1qeiArIpI82p%2FL4bDsAJuu6dU3e%2BwKb6lcy26bV0MtlDPlnPOURYAJAddM1op9pXgcz5A23u2IwJjGOVAsGMtA%5Cu003d%7Cal%5Cu003dY0mHdVESvJClv3snUkMMggzByMZdjhClcX6fwtnxBa64wXO9ZPnP1w%2BC4N5oaXFNArkbdKXSi0mpvQkEkq8BP83jpKuT5oUkURMDybd2u9D%2B7jjgHO67SYLlqx9o6MwVNEn4ZsJW4D8F7s%2Br%2FRY5MFE0Xi6Xmcrz6rAY%2BDieQ3U%5Cu003d%22%2C%22mdn%22%3A%22maV%2FqIUchJ1RpTjyg9sqlBwjRXvJTvIwMf9xAcvZl%2Fkh0weWJFl1mFREeuNt4uRXtJa9DirsmQ8rhBvZczs2uGZsPn3wk2N45YqwME0PzFIJ%2FsbrTIyaIFmmc%2FWsAB5gK%2Fw61FKoxhmvmZ1ko%2F861yAERW1jlhcZSjltCAsTH4s%5Cu003d%22%7D%7D"

                knetWebView.loadUrl(urlToBeloaded, getHeaders().toMap())
                knetWebView.requestFocus()
             //   knetWebView.loadData("")
                /*  val isFirstTime = Pref.getValue(context, "firstRun", "true").toString()
                  if (isFirstTime == "true") {
                  knetWebView?.visibility= View.GONE
                  }else knetWebView?.visibility= View.VISIBLE*/
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

    private fun getHeaders(): Headers {
        val extraHeaders: MutableMap<String, String> = HashMap()
        extraHeaders["Cache-Control"] = "no-cache"
        extraHeaders["Pragma"] = "no-cache"
        extraHeaders["Accept-Encoding"] = "gzip, deflate, br"
        extraHeaders["Accept"] = "application/json, text/plain, */*"

        return extraHeaders.toHeaders()
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

            if (request?.url.toString().startsWith(webViewScheme, ignoreCase = true)) {
                if (request != null) {
//                    handleIntercept(request)

                    // Log.e("url headers", request.requestHeaders.toString())

                }
                Log.e("url are", request?.url.toString())
                /**
                 * listen for states of cardWebStatus of onReady , onValidInput .. etc
                 */

                if (request?.url.toString().contains(KnetStatusDelegate.onReady.name)) {


                       if(buttonTypeConfigured ==ThreeDsPayButtonType.CARD) {
                           if (firstTimeOnReadyCallback){
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
                    Log.e("data",data.toString())
                    val gson = Gson()
                    threeDsResponse = gson.fromJson(data, ThreeDsResponse::class.java)
                    when (threeDsResponse.stopRedirection) {
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

                return true

            } else {
                return false
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

//        private fun handleIntercept(request: WebResourceRequest?): WebResourceResponse? {
//            val okHttpClient = OkHttpClient()
//            val call = okHttpClient.newCall(
//                Request.Builder().url(request?.url.toString()).method(
//                    request?.method.toString(), null
//                ).headers(
//                    getHeaders()
//                ).build()
//            )
//
//            return try {
//                var webResourceResponse: WebResourceResponse? = null
//                val response = call.enqueue(object : Callback {
//                    override fun onFailure(call: Call, e: IOException) {
//                        Log.e("headers error", e.toString())
//
//                    }
//
//                    override fun onResponse(call: Call, response: Response) {
//                        response.run {
//                            webResourceResponse = WebResourceResponse(
//                                header(
//                                    "content-type",
//                                    "text/plain"
//                                ), // You can set something other as default content-type
//                                header(
//                                    "content-encoding",
//                                    "utf-8"
//                                ),  // you can set another encoding as default
//                              null
//                            )
//                        }
//
//                    }
//
//
//                })
//                webResourceResponse
//
//            } catch (e: IOException) {
//                e.printStackTrace()
//                null
//            }
//        }


        override fun shouldInterceptRequest(
            view: WebView?,
            request: WebResourceRequest?
        ): WebResourceResponse? {
            val webResourceResponse = super.shouldInterceptRequest(view, request)
            Log.e("request",request?.method.toString())
            Log.e("request",request?.requestHeaders.toString())
            Log.e("request",request?.url.toString())

            return webResourceResponse
        }

        override fun onReceivedError(
            view: WebView,
            request: WebResourceRequest,
            error: WebResourceError
        ) {
            super.onReceivedError(view, request, error)

        }
    }

    private fun initWeb() {

        MainScope().launch {
            val url =
                "${webviewStarterUrl}${encodeConfigurationMapToUrl(DataConfiguration.configurationsAsHashMap)}"
            Log.e("url here", url)
            knetWebView.post {
                knetWebView.stopLoading()
                knetWebView.loadUrl(url)
            }

        }
    }


}


enum class KnetConfiguration() {
    MapConfigruation
}

enum class PaymentFlow {
    CARDPAY, PAYMENTBUTTON
}




