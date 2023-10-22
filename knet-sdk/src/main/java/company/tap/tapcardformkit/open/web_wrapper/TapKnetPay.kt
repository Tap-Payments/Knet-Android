package company.tap.tapcardformkit.open.web_wrapper

import TapLocal
import TapTheme
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Picture
import android.net.http.SslError
import android.os.Build
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.view.*
import cards.pay.paycardsrecognizer.sdk.Card
import com.google.gson.Gson
import company.tap.tapcardformkit.*
import company.tap.tapcardformkit.open.ApplicationLifecycle
import company.tap.tapcardformkit.open.DataConfiguration
import company.tap.tapcardformkit.open.web_wrapper.enums.BenefitPayStatusDelegate
import company.tap.tapcardformkit.open.web_wrapper.model.ThreeDsResponse
import company.tap.tapcardformkit.open.web_wrapper.threeDsWebView.ThreeDsBottomSheetFragment
import company.tap.tapcardformkit.open.web_wrapper.threeDsWebView.ThreeDsWebViewActivity
import company.tap.tapuilibrary.themekit.ThemeManager
import company.tap.tapuilibrary.uikit.atoms.*
import java.util.*


@SuppressLint("ViewConstructor")
class TapKnetPay : LinearLayout,ApplicationLifecycle {
    lateinit var webViewFrame: FrameLayout
    private var isBenefitPayUrlIntercepted =false
    private var isRedirected = false

    lateinit var threeDsResponse: ThreeDsResponse
    lateinit var hideableWebView: WebView

    lateinit var dialog: Dialog
     var pair =  Pair("",false)
    lateinit var linearLayout: LinearLayout
     var iSAppInForeground = true


    companion object{
        var alreadyEvaluated = false
        var loaded = false

        lateinit var cardWebview: WebView
        lateinit var cardConfiguraton: CardConfiguraton
        var card:Card?=null

        fun cancel() {
            cardWebview.loadUrl("javascript:onCancel")
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
        cardWebview = findViewById(R.id.webview)
        webViewFrame = findViewById(R.id.webViewFrame)
         hideableWebView = findViewById(R.id.hideableWebView)
         hideableWebView.settings.javaScriptEnabled = true
         hideableWebView.webViewClient = HideableWebViewClient()
         hideableWebView.setPictureListener(MyPictureListener())

         with(cardWebview.settings){
             javaScriptEnabled=true
             domStorageEnabled=true
             cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK

         }
         cardWebview.setBackgroundColor(Color.TRANSPARENT)
         cardWebview.setLayerType(LAYER_TYPE_SOFTWARE, null)
         cardWebview.webViewClient = MyWebViewClient()


     }
    class MyPictureListener : WebView.PictureListener {
        override fun onNewPicture(view: WebView?, arg1: Picture?) {
            Log.e("done","newPic newPic")

            // put code here that needs to run when the page has finished loading and
            // a new "picture" is on the webview.
        }
    }


     fun init(configuraton: CardConfiguraton) {
         cardConfiguraton = configuraton
         DataConfiguration.addAppLifeCycle(this)
        applyTheme()
        when (configuraton) {
            CardConfiguraton.MapConfigruation -> {
                val url  = "${urlWebStarter}${encodeConfigurationMapToUrl(DataConfiguration.configurationsAsHashMap)}"
             Log.e("url",url.toString())
                cardWebview.loadUrl(url)

            }
            else -> {}
        }
    }


    private fun applyTheme() {
        /**
         * need to be refactored : mulitple copies of same code
         */
        when(cardConfiguraton){
            CardConfiguraton.MapConfigruation ->{
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

            if (request?.url.toString().startsWith(CardWebUrlPrefix, ignoreCase = true)) {
                Log.e("url",request?.url.toString())
                /**
                 * listen for states of cardWebStatus of onReady , onValidInput .. etc
                 */
                if (request?.url.toString().contains(BenefitPayStatusDelegate.onReady.name)) {
                    DataConfiguration.getTapCardStatusListener()?.onReady()
                }
                if (request?.url.toString().contains(BenefitPayStatusDelegate.onChargeCreated.name)) {
                    val data = request?.url?.getQueryParameterFromUri(keyValueName).toString()
                    val gson = Gson()
                    threeDsResponse = gson.fromJson(data, ThreeDsResponse::class.java)
                    Log.e("threeDs",threeDsResponse.toString())
                    hideableWebView.loadUrl(threeDsResponse.url)
                    doAfterSpecificTime {
                   //     navigateTo3dsActivity()
                    }
                    DataConfiguration.getTapCardStatusListener()?.onChargeCreated(request?.url?.getQueryParameterFromUri(keyValueName).toString())
                }

                if (request?.url.toString().contains(BenefitPayStatusDelegate.onOrderCreated.name)) {
                    DataConfiguration.getTapCardStatusListener()?.onOrderCreated(request?.url?.getQueryParameter(keyValueName).toString())
                }
                if (request?.url.toString().contains(BenefitPayStatusDelegate.onClick.name)) {
                    isBenefitPayUrlIntercepted=false
                    pair = Pair("",false)
                    DataConfiguration.getTapCardStatusListener()?.onClick()

                }
                if (request?.url.toString().contains(BenefitPayStatusDelegate.onCancel.name)) {
                    DataConfiguration.getTapCardStatusListener()?.onCancel()
                    if (!(pair.first.isNotEmpty() and pair.second)) {
                        dismissDialog()
                    }


                }

                if (request?.url.toString().contains(BenefitPayStatusDelegate.onError.name)) {
                    pair = Pair(request?.url?.getQueryParameterFromUri(keyValueName).toString(),true)
                    DataConfiguration.getTapCardStatusListener()?.onError(request?.url?.getQueryParameterFromUri(keyValueName).toString())

                    //closePayment()

                }

                if (request?.url.toString().contains(BenefitPayStatusDelegate.onSuccess.name)) {
                    pair = Pair(request?.url?.getQueryParameterFromUri(keyValueName).toString(),true)
                    when(iSAppInForeground) {

                        true ->{closePayment()
                            Log.e("app","one")
                        }
                        false ->{}
                    }


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

            ThreeDsBottomSheetFragment.tapKnet = this@TapKnetPay
            val intent = Intent(context, ThreeDsWebViewActivity::class.java)
            (context).startActivity(intent)


        }



        override fun shouldInterceptRequest(
            view: WebView?,
            request: WebResourceRequest?
        ): WebResourceResponse? {
            Log.e("intercepted mainwebview",request?.url.toString())

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

    private fun dismissDialog() {
        if (::dialog.isInitialized) {
            linearLayout.removeView(cardWebview)
            dialog.dismiss()
            if (cardWebview.parent == null){
                (webViewFrame as ViewGroup).addView(cardWebview)
            }
        }
    }

    override fun onEnterForeground() {
        iSAppInForeground = true
        Log.e("applifeCycle","onEnterForeground")
        closePayment()





    }

    private fun closePayment() {
        if (pair.second) {
            Log.e("app","one")
            dismissDialog()
            init(cardConfiguraton)
            DataConfiguration.getTapCardStatusListener()?.onSuccess(pair.first)
        }
    }

    override fun onEnterBackground() {
        iSAppInForeground = false
        Log.e("applifeCycle","onEnterBackground")

    }


    inner class HideableWebViewClient : WebViewClient() {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun shouldOverrideUrlLoading(
            webView: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            isRedirected = true;
            return false
        }


        override fun onPageFinished(view: WebView, url: String) {
//            if (!alreadyEvaluated) {
//                Log.e("onPagefinsihed", alreadyEvaluated.toString())
//                alreadyEvaluated = true;
//                Handler().postDelayed({
//                //    navigateTo3dsActivity()
//                }, waitDelaytime)
//            } else {
//                alreadyEvaluated = false;
//            }

            if (loaded){
                return
            }
            else{
                Log.e("onPagefinsihed", loaded.toString())
                Handler().postDelayed({
                        navigateTo3dsActivity()
                }, waitDelaytime)
            }
            loaded = true

        }

        override fun shouldInterceptRequest(
            view: WebView?,
            request: WebResourceRequest?
        ): WebResourceResponse? {
            Log.e("hidden webview",request?.url.toString())

            return super.shouldInterceptRequest(view, request)
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            isRedirected = false;

        }

        fun navigateTo3dsActivity() {
            // on below line we are creating a new bottom sheet dialog.
            /**
             * put buttomsheet in separate class
             */


            val intent = Intent(context, ThreeDsWebViewActivity::class.java)
            (context).startActivity(intent)
            ThreeDsBottomSheetFragment.tapKnet = this@TapKnetPay

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





enum class CardConfiguraton() {
    MapConfigruation
}




