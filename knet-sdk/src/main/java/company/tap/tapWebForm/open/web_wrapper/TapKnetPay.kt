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
import company.tap.tapWebForm.open.web_wrapper.pop_up_window.WebChrome
import company.tap.tapWebForm.open.web_wrapper.threeDsWebView.ThreeDsWebViewActivity
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.uikit.atoms.*
import java.util.*


@SuppressLint("ViewConstructor")
class TapKnetPay : LinearLayout {
    lateinit var webviewStarterUrl:String
    lateinit var webViewScheme:String
    private lateinit var webChrome: WebChrome

    lateinit var webViewFrame: FrameLayout

    companion object{
          lateinit var threeDsResponse: ThreeDsResponse
         lateinit var knetWebView: WebView
        private lateinit var knetConfiguration: KnetConfiguration
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
             domStorageEnabled = true
             setSupportMultipleWindows(true)
             javaScriptCanOpenWindowsAutomatically = true
         }
         knetWebView.setBackgroundColor(Color.TRANSPARENT)
         knetWebView.setLayerType(LAYER_TYPE_SOFTWARE, null)
         webChrome = WebChrome(context)
         knetWebView.webChromeClient = webChrome
         knetWebView.webViewClient = MyWebViewClient()


     }
     fun init(configuraton: KnetConfiguration, buttonType: ThreeDsPayButtonType?) {
         initializePaymentData(buttonType)
         knetConfiguration = configuraton
        applyTheme()
        when (configuraton) {
            KnetConfiguration.MapConfigruation -> {
                val url  = "${webviewStarterUrl}${encodeConfigurationMapToUrl(DataConfiguration.configurationsAsHashMap)}"
                knetWebView.loadUrl(url)
            }
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
                /**
                 * for google button
                 */
                if (request?.url.toString().contains(KnetStatusDelegate.onClosePopup.name)) {
                    webChrome.getdialog()?.dismiss()

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
            super.onReceivedError(view, request, error)
        }
    }

}





enum class KnetConfiguration() {
    MapConfigruation
}




