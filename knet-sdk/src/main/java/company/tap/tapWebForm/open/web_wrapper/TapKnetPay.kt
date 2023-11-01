package company.tap.tapWebForm.open.web_wrapper

import TapLocal
import TapTheme
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.http.SslError
import android.os.Build
import android.os.Message
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.webkit.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.view.*
import com.google.gson.Gson
import company.tap.tapWebForm.*
import company.tap.tapWebForm.open.ApplicationLifecycle
import company.tap.tapWebForm.open.DataConfiguration
import company.tap.tapWebForm.open.web_wrapper.enums.*
import company.tap.tapWebForm.open.web_wrapper.model.ThreeDsResponse
import company.tap.tapWebForm.open.web_wrapper.threeDsWebView.ThreeDsWebViewActivity
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.uikit.atoms.*
import java.util.*


@SuppressLint("ViewConstructor")
class TapKnetPay : LinearLayout,ApplicationLifecycle {
    lateinit var webviewStarterUrl:String
    lateinit var webViewScheme:String
    private lateinit var alert:AlertDialog.Builder
    private lateinit var dialog: Dialog
    val USER_AGENT =
        "Mozilla/5.0 (Linux; Android 4.1.1; Galaxy Nexus Build/JRO03C) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Mobile Safari/535.19"

    lateinit var webViewFrame: FrameLayout

    companion object{
         lateinit var threeDsResponse: ThreeDsResponse
        lateinit var knetWebView: WebView
        lateinit var knetConfiguration: KnetConfiguration
        fun cancel() {
            knetWebView.loadUrl("javascript:window.cancel()")
        }
        fun retrieve(value:String) {
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
        LayoutInflater.from(context).inflate(R.layout.activity_card_web_wrapper, this)
        initWebView()

    }


     private fun initWebView() {
        knetWebView = findViewById(R.id.webview)
         webViewFrame = findViewById(R.id.webViewFrame)

         with(knetWebView.settings){
             javaScriptEnabled=true
             domStorageEnabled=true
             cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
             userAgentString = USER_AGENT + "com.example.knet_android"

             setSupportMultipleWindows(true)
             javaScriptCanOpenWindowsAutomatically = true


         }
         knetWebView.setBackgroundColor(Color.TRANSPARENT)
         knetWebView.setLayerType(LAYER_TYPE_SOFTWARE, null)
        knetWebView.webChromeClient = webClient()
         knetWebView.webViewClient = MyWebViewClient()


     }
     fun init(configuraton: KnetConfiguration, buttonType: ThreeDsPayButtonType?) {
         initializePaymentData(buttonType)
         knetConfiguration = configuraton
         DataConfiguration.addAppLifeCycle(this)
        applyTheme()
        when (configuraton) {
            KnetConfiguration.MapConfigruation -> {
                val url  = "${webviewStarterUrl}${encodeConfigurationMapToUrl(DataConfiguration.configurationsAsHashMap)}"
                knetWebView.loadUrl(url)

            }
            else -> {}
        }
    }

    private fun initializePaymentData(buttonType: ThreeDsPayButtonType?) {
        when(buttonType){
            ThreeDsPayButtonType.KNET->{
                webviewStarterUrl = SCHEMES.KNET.value.first
                webViewScheme = SCHEMES.KNET.value.second
            }
            ThreeDsPayButtonType.BENEFIT ->{
                webviewStarterUrl = SCHEMES.BENEFIT.value.first
                webViewScheme = SCHEMES.BENEFIT.value.second
            }
            ThreeDsPayButtonType.FAWRY ->{
                webviewStarterUrl = SCHEMES.FAWRY.value.first
                webViewScheme = SCHEMES.FAWRY.value.second
            }
            ThreeDsPayButtonType.PAYPAL ->{
                webviewStarterUrl = SCHEMES.PAYPAL.value.first
                webViewScheme = SCHEMES.PAYPAL.value.second
            }
            ThreeDsPayButtonType.TABBY ->{
                webviewStarterUrl = SCHEMES.TABBY.value.first
                webViewScheme = SCHEMES.TABBY.value.second
            }
            ThreeDsPayButtonType.GOOGLE ->{
                webviewStarterUrl = SCHEMES.GOOGLE.value.first
                webViewScheme = SCHEMES.GOOGLE.value.second

            }
            else -> {}
        }
    }


    private fun applyTheme() {
        /**
         * need to be refactored : mulitple copies of same code
         */
        when(knetConfiguration){
            KnetConfiguration.MapConfigruation ->{
                val tapInterface = DataConfiguration.configurationsAsHashMap?.get("interface") as? Map<*, *>
              setTapThemeAndLanguage(
                    this.context,
                    TapLocal.valueOf(tapInterface?.get("locale")?.toString() ?: TapLocal.en.name),
                  TapTheme.valueOf(tapInterface?.get("theme")?.toString() ?: TapTheme.light.name))
            }
            else -> {}
        }


    }

    private fun setTapThemeAndLanguage(context: Context, language: TapLocal?, themeMode: TapTheme?) {
        when (themeMode) {
            TapTheme.light -> {
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
        DataConfiguration.setLocale(this.context, language?.name ?:"en", null, this@TapKnetPay.context.resources, R.raw.lang)

    }


    inner class webClient :WebChromeClient(){
    override fun onCreateWindow(
        view: WebView?,
        isDialog: Boolean,
        isUserGesture: Boolean,
        resultMsg: Message?
    ): Boolean {

        val newWebView = WebView(view!!.context)
        newWebView.settings.javaScriptEnabled = true
        newWebView.settings.setSupportZoom(true)
        newWebView.isFocusable = true
        newWebView.onCheckIsTextEditor()
        // Enable Cookies
        // Enable Cookies
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        if (Build.VERSION.SDK_INT >= 21) {
            cookieManager.setAcceptThirdPartyCookies(knetWebView, true)
            cookieManager.setAcceptThirdPartyCookies(newWebView, true)
        }
        newWebView.settings.saveFormData = true;
        newWebView.settings.setEnableSmoothTransition(true);
        newWebView.requestFocus(FOCUS_DOWN)
        newWebView.requestFocusFromTouch()
        newWebView.settings.userAgentString = USER_AGENT + "com.example.knet_android"
        newWebView.isFocusableInTouchMode = true
        newWebView.settings.setSupportMultipleWindows(true)
        newWebView.settings.javaScriptCanOpenWindowsAutomatically = true

        newWebView.webChromeClient = webClient2()


        alert = AlertDialog.Builder(view.context)
        val wrapper = LinearLayout(view.context)
        val keyboardHack = EditText(view.context)

        keyboardHack.visibility = GONE
        wrapper.orientation = VERTICAL
        wrapper.addView(newWebView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        wrapper.addView(keyboardHack, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)

        alert.setView(wrapper)
        alert.setNegativeButton("Close", DialogInterface.OnClickListener { dialog, id ->
            init(knetConfiguration,ThreeDsPayButtonType.GOOGLE)
            dialog.dismiss()
        })
        dialog = alert.create()
        dialog.show()

        val transports = resultMsg!!.obj as WebView.WebViewTransport
        transports.webView = newWebView
        resultMsg.sendToTarget()
        newWebView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                Log.e("should overide url", request.url.toString())
                view.loadUrl(request.url.toString())
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
                Log.e("closing url", url.toString())

                super.onPageFinished(view, url)
            }

            override fun shouldInterceptRequest(
                view: WebView,
                request: WebResourceRequest
            ): WebResourceResponse? {
                Log.e("request url", request.url.toString())
                return super.shouldInterceptRequest(view, request)
            }
        }


        return true
    }

        override fun onJsAlert(
            view: WebView?,
            url: String?,
            message: String?,
            result: JsResult?
        ): Boolean {
            return super.onJsAlert(view, url, message, result)
        }

        override fun onCloseWindow(window: WebView?) {
            try {
                window?.destroy()
            } catch (e: Exception) {
                Log.d("Destroyed with Error ", e.stackTrace.toString())
            }
            super.onCloseWindow(window)

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
            Log.e("url",request?.url.toString())

            if (request?.url.toString().startsWith(webViewScheme, ignoreCase = true)) {
                Log.e("url",request?.url.toString())
                /**
                 * listen for states of cardWebStatus of onReady , onValidInput .. etc
                 */
                if (request?.url.toString().contains(KnetStatusDelegate.onReady.name)) {
                    DataConfiguration.getTapKnetListener()?.onReady()
                }

                if (request?.url.toString().contains(KnetStatusDelegate.onSuccess.name)) {
                    DataConfiguration.getTapKnetListener()?.onSuccess(request?.url?.getQueryParameterFromUri(keyValueName).toString())
                }
                if (request?.url.toString().contains(KnetStatusDelegate.onChargeCreated.name)) {
                    val data = request?.url?.getQueryParameterFromUri(keyValueName).toString()
                    val gson = Gson()
                    threeDsResponse = gson.fromJson(data, ThreeDsResponse::class.java)
                    when(threeDsResponse.stopRedirection){
                        false->navigateTo3dsActivity()
                        else->{}
                    }
                    DataConfiguration.getTapKnetListener()?.onChargeCreated(request?.url?.getQueryParameterFromUri(keyValueName).toString())
                }

                if (request?.url.toString().contains(KnetStatusDelegate.onOrderCreated.name)) {
                    DataConfiguration.getTapKnetListener()?.onOrderCreated(request?.url?.getQueryParameter(keyValueName).toString())
                }
                if (request?.url.toString().contains(KnetStatusDelegate.onClick.name)) {
                    DataConfiguration.getTapKnetListener()?.onClick()

                }
                if (request?.url.toString().contains(KnetStatusDelegate.cancel.name)) {
                    DataConfiguration.getTapKnetListener()?.cancel()
                }

                if (request?.url.toString().contains(KnetStatusDelegate.onError.name)) {
                    DataConfiguration.getTapKnetListener()?.onError(request?.url?.getQueryParameterFromUri(keyValueName).toString())
                }

                return true

            }

            return false

        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)

        }

        fun navigateTo3dsActivity() {
            // on below line we are creating a new bottom sheet dialog.
            /**
             * put buttomsheet in separate class
             */

            val intent = Intent(context, ThreeDsWebViewActivity::class.java)
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
            Log.e("error",error.toString())
            Log.e("error",request.toString())

            super.onReceivedError(view, request, error)
        }

        override fun onReceivedSslError(
            view: WebView?,
            handler: SslErrorHandler?,
            error: SslError?
        ) {
            handler?.proceed()
        }
    }



    inner class webClient2 :WebChromeClient(){
        override fun onCreateWindow(
            view: WebView?,
            isDialog: Boolean,
            isUserGesture: Boolean,
            resultMsg: Message?
        ): Boolean {

            val newWebView = WebView(view!!.context)
            newWebView.settings.javaScriptEnabled = true
            newWebView.settings.setSupportZoom(true)
            newWebView.isFocusable = true
            newWebView.onCheckIsTextEditor()
            newWebView.requestFocus(FOCUS_DOWN)
            newWebView.requestFocusFromTouch()
            newWebView.settings.userAgentString = "knet-app"

            newWebView.isFocusableInTouchMode = true
            newWebView.settings.setSupportMultipleWindows(true)
            newWebView.webChromeClient = WebChromeClient()


            alert = AlertDialog.Builder(view.context)
            val wrapper = LinearLayout(view.context)
            val keyboardHack = EditText(view.context)

            keyboardHack.visibility = GONE
            wrapper.orientation = VERTICAL
            wrapper.addView(newWebView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            wrapper.addView(keyboardHack, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)

            alert.setView(wrapper)
            alert.setNegativeButton("Close", DialogInterface.OnClickListener { dialog, id ->
                init(knetConfiguration,ThreeDsPayButtonType.GOOGLE)
                dialog.dismiss()
            })
            dialog = alert.create()
            dialog.show()

            val transports = resultMsg!!.obj as WebView.WebViewTransport
            transports.webView = newWebView
            resultMsg.sendToTarget()
            newWebView.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView,
                    request: WebResourceRequest
                ): Boolean {
                    Log.e("should overide url", request.url.toString())
                    view.loadUrl(request.url.toString())
                    return true
                }

                override fun onPageFinished(view: WebView, url: String) {
                    Log.e("closing url", url.toString())

                    super.onPageFinished(view, url)
                }

                override fun shouldInterceptRequest(
                    view: WebView,
                    request: WebResourceRequest
                ): WebResourceResponse? {
                    Log.e("request url", request.url.toString())
                    return super.shouldInterceptRequest(view, request)
                }
            }


            return true
        }

        override fun onJsAlert(
            view: WebView?,
            url: String?,
            message: String?,
            result: JsResult?
        ): Boolean {
            return super.onJsAlert(view, url, message, result)
        }

        override fun onCloseWindow(window: WebView?) {
            try {
                window?.destroy()
            } catch (e: Exception) {
                Log.d("Destroyed with Error ", e.stackTrace.toString())
            }
            super.onCloseWindow(window)

        }

    }



    override fun onEnterForeground() {

    }


    override fun onEnterBackground() {
        Log.e("applifeCycle","onEnterBackground")

    }


}





enum class KnetConfiguration() {
    MapConfigruation
}




