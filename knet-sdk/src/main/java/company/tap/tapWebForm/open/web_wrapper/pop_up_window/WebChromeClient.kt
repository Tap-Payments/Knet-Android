package company.tap.tapWebForm.open.web_wrapper.pop_up_window

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Message
import android.util.Log
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.*
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout


class WebChrome(var context : Context) :WebChromeClient(){
     private  var dialog: Dialog?=null

     override fun onCreateWindow(
        view: WebView?,
        isDialog: Boolean,
        isUserGesture: Boolean,
        resultMsg: Message?
    ): Boolean {

        val newWebView = WebView(context)
         with(newWebView.settings){
             javaScriptEnabled = true
             domStorageEnabled = true
         }
         with(newWebView){
             requestFocus(LinearLayout.FOCUS_DOWN)
             requestFocusFromTouch()
             isFocusableInTouchMode = true
         }


        val wrapper = LinearLayout(view?.context)
        val keyboardHack = EditText(view?.context)

        keyboardHack.visibility = LinearLayout.GONE
        wrapper.orientation = LinearLayout.VERTICAL
        wrapper.addView(newWebView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        wrapper.addView(keyboardHack, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
         val alert = AlertDialog.Builder(view?.context)
        alert.setView(wrapper)

         if (dialog ==null){
             dialog = alert.create()
             dialog?.setCanceledOnTouchOutside(false)
             dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
             dialog?.show()
             dialog?.window?.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);

         }
         dialog?.setOnDismissListener {
             dialog = null
         }

        val transports = resultMsg?.obj as WebView.WebViewTransport
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


         newWebView.webChromeClient = object : WebChromeClient() {
             override fun onCloseWindow(window: WebView?) {
                 super.onCloseWindow(window)
             }
         }


        return true
    }

    fun getdialog() = dialog

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
            Log.e("closedWindow","closedWindow")
            window?.destroy()
        } catch (e: Exception) {
            Log.d("Destroyed with Error ", e.stackTrace.toString())
        }
        super.onCloseWindow(window)

    }

}
