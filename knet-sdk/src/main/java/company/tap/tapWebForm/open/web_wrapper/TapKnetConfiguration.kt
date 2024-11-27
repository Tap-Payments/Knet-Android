package company.tap.tapWebForm.open.web_wrapper

import Headers
import android.content.Context
import android.util.Log
import androidx.lifecycle.ProcessLifecycleOwner
import company.tap.tapWebForm.R
import company.tap.tapWebForm.open.AppLifecycleObserver
import company.tap.tapWebForm.open.KnetDataConfiguration
import company.tap.tapWebForm.open.KnetDataConfiguration.configurationsAsHashMap
import company.tap.tapWebForm.open.KnetPayStatusDelegate
import company.tap.tapWebForm.open.web_wrapper.ApiService.BASE_URL
import company.tap.tapWebForm.open.web_wrapper.ApiService.BASE_URL_1
import company.tap.tapWebForm.open.web_wrapper.enums.*
import company.tap.tapnetworkkit.connection.NetworkApp
import company.tap.tapnetworkkit.utils.CryptoUtil
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class TapKnetConfiguration {

    companion object {
        private val retrofit = ApiService.RetrofitClient.getClient()
        private val tapSDKConfigsUrl = retrofit.create(ApiService.TapButtonSDKConfigUrls::class.java)
        private var testEncKey: String? = null
        private var prodEncKey: String? = null

        fun configureWithKnetDictionary(
            context: Context,
            tapCardInputViewWeb: TapKnetPay?,
            tapMapConfiguration: java.util.HashMap<String, Any>,
            knetPayStatusDelegate: KnetPayStatusDelegate? = null,
            buttonType: ThreeDsPayButtonType? = null

        ) {
//ToDO test when cdn url ready
          /*  MainScope().launch {
                getTapButtonSDKConfigUrls(
                    tapMapConfiguration,
                    tapCardInputViewWeb,
                    context,
                    knetPayStatusDelegate,
                    buttonType
                )
            }*/
            with(tapMapConfiguration) {
                Log.e("map", tapMapConfiguration.toString())
                configurationsAsHashMap = tapMapConfiguration
                val operator = configurationsAsHashMap?.get(operatorKey) as HashMap<*, *>
                val publickKey = operator.get(publicKeyToGet)

                val appLifecycleObserver = AppLifecycleObserver()
                ProcessLifecycleOwner.get().lifecycle.addObserver(appLifecycleObserver)

                addOperatorHeaderField(
                    tapCardInputViewWeb,
                    context,
                    KnetConfiguration.MapConfigruation,
                    publickKey.toString()
                )

                KnetDataConfiguration.addTapBenefitPayStatusDelegate(knetPayStatusDelegate)
               // tapCardInputViewWeb?.init(KnetConfiguration.MapConfigruation,buttonType)
                tapCardInputViewWeb?.init(tapMapConfiguration,buttonType)

            }
        }

        private suspend fun getTapButtonSDKConfigUrls(tapMapConfiguration: HashMap<String, Any>, tapCardInputViewWeb: TapKnetPay?, context: Context, knetPayStatusDelegate: KnetPayStatusDelegate?, buttonType: ThreeDsPayButtonType?) {
            try {
                /**
                 * request to get Tap configs
                 */

                val tapButtonSDKConfigUrlResponse = tapSDKConfigsUrl.getButtonSDKConfigUrl()
                BASE_URL_1 = tapButtonSDKConfigUrlResponse.baseURL
                prodEncKey = tapButtonSDKConfigUrlResponse.prodEncKey
                testEncKey = tapButtonSDKConfigUrlResponse.testEncKey
              //  urlWebStarter = tapButtonSDKConfigUrlResponse.baseURL


                startWithSDKConfigs(
                    context,
                    tapCardInputViewWeb,
                    tapMapConfiguration,
                   knetPayStatusDelegate,
                    buttonType

                )

            } catch (e: Exception) {
               BASE_URL_1 = urlWebStarter //Todo what should be here
                testEncKey =  tapCardInputViewWeb?.context?.resources?.getString(R.string.enryptkeyTest)
                prodEncKey = tapCardInputViewWeb?.context?.resources?.getString(R.string.enryptkeyProduction)

                startWithSDKConfigs(
                    context,
                    tapCardInputViewWeb,
                    tapMapConfiguration,
                    knetPayStatusDelegate,
                    buttonType

                )
                Log.e("error Config", e.message.toString())
            }
        }

        fun addOperatorHeaderField(
            tapCardInputViewWeb: TapKnetPay?,
            context: Context,
            modelConfiguration: KnetConfiguration,
            publicKey: String?
        ) {
         val encodedeky = when(publicKey.toString().contains("test")){
                true->{
                    tapCardInputViewWeb?.context?.resources?.getString(R.string.enryptkeyTest)
                }
                false->{
                    tapCardInputViewWeb?.context?.resources?.getString(R.string.enryptkeyProduction)

                }
            }

            Log.e("packagedname",context.packageName.toString())
            NetworkApp.initNetwork(
                tapCardInputViewWeb?.context ,
                publicKey ?: "",
                context.packageName,
                ApiService.BASE_URL,
                "android-knet",
                true,
                encodedeky,
                null
            )
            val headers = Headers(
                application = NetworkApp.getApplicationInfo(),
                mdn = CryptoUtil.encryptJsonString(
                    context.packageName.toString(),
                    encodedeky,
                )
            )

            when (modelConfiguration) {
                KnetConfiguration.MapConfigruation -> {
                    val hashMapHeader = HashMap<String, Any>()
                    hashMapHeader[HeadersMdn] = headers.mdn.toString()
                    hashMapHeader[HeadersApplication] = headers.application.toString()
                    val redirect = HashMap<String,Any>()
                    redirect.put(urlKey, redirectValue)
                    configurationsAsHashMap?.put(headersKey, hashMapHeader)
                    configurationsAsHashMap?.put(redirectKey, redirect)


                }
                else -> {}
            }


        }
        private fun startWithSDKConfigs(context: Context,
                                        tapCardInputViewWeb: TapKnetPay?,
                                        tapMapConfiguration: java.util.HashMap<String, Any>,
                                        knetPayStatusDelegate: KnetPayStatusDelegate? = null,
                                        buttonType: ThreeDsPayButtonType? = null){
            with(tapMapConfiguration) {
                android.util.Log.e("map", tapMapConfiguration.toString())
                company.tap.tapWebForm.open.KnetDataConfiguration.configurationsAsHashMap = tapMapConfiguration
                val operator = company.tap.tapWebForm.open.KnetDataConfiguration.configurationsAsHashMap?.get(
                    company.tap.tapWebForm.open.web_wrapper.enums.operatorKey
                ) as HashMap<*, *>
                val publickKey = operator.get(company.tap.tapWebForm.open.web_wrapper.enums.publicKeyToGet)

                val appLifecycleObserver = AppLifecycleObserver()
                androidx.lifecycle.ProcessLifecycleOwner.get().lifecycle.addObserver(appLifecycleObserver)

                addOperatorHeaderField(
                    tapCardInputViewWeb,
                    context,
                    company.tap.tapWebForm.open.web_wrapper.KnetConfiguration.MapConfigruation,
                    publickKey.toString()
                )

                company.tap.tapWebForm.open.KnetDataConfiguration.addTapBenefitPayStatusDelegate(knetPayStatusDelegate)
                // tapCardInputViewWeb?.init(KnetConfiguration.MapConfigruation,buttonType)
                tapCardInputViewWeb?.init(tapMapConfiguration,buttonType)

            }
        }
    }


}


