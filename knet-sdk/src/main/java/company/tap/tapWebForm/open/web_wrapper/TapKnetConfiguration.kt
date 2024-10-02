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
import company.tap.tapWebForm.open.web_wrapper.enums.*
import company.tap.tapnetworkkit.connection.NetworkApp
import company.tap.tapnetworkkit.utils.CryptoUtil
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class TapKnetConfiguration {

    companion object {

        private val retrofit = ApiService.RetrofitClient.getClient()
        private val tapKnetSDKConfigsUrl = retrofit.create(ApiService.TapKnetSDKConfigUrls::class.java)
        private var testEncKey: String? = null
        private var prodEncKey: String? = null
        fun configureWithKnetDictionary(
            context: Context,
            tapCardInputViewWeb: TapKnetPay?,
            tapMapConfiguration: java.util.HashMap<String, Any>,
            knetPayStatusDelegate: KnetPayStatusDelegate? = null,
            buttonType: ThreeDsPayButtonType? = null

        ) {
         /*   with(tapMapConfiguration) {
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
                tapCardInputViewWeb?.init(KnetConfiguration.MapConfigruation,buttonType)

            }*/

            MainScope().launch {
                getTapSDKConfigUrls(
                    tapMapConfiguration,
                    tapCardInputViewWeb,
                    context,
                    knetPayStatusDelegate,buttonType
                )
            }

        }

        private suspend fun getTapSDKConfigUrls(
            tapMapConfiguration: HashMap<String, Any>,
            tapCardInputViewWeb: TapKnetPay?,
            context: Context,
            knetPayStatusDelegate : KnetPayStatusDelegate?, buttonType: ThreeDsPayButtonType?
        ) {

            try {
                /**
                 * request to get Tap configs
                 */

                val tapSDKConfigUrlResponse = tapKnetSDKConfigsUrl.getSDKConfigUrl()
                // BASE_URL = tapSDKConfigUrlResponse.baseURL
                testEncKey = tapSDKConfigUrlResponse.testEncKey
                prodEncKey = tapSDKConfigUrlResponse.prodEncKey
                urlWebStarter = tapSDKConfigUrlResponse.baseURL

                startSDKWithConfigs(
                    tapMapConfiguration,
                    tapCardInputViewWeb,
                    context,
                    knetPayStatusDelegate,buttonType
                )

            } catch (e: Exception) {
                //   BASE_URL = urlWebStarter
                testEncKey =  tapCardInputViewWeb?.context?.resources?.getString(R.string.enryptkeyTest)
                prodEncKey = tapCardInputViewWeb?.context?.resources?.getString(R.string.enryptkeyProduction)

                startSDKWithConfigs(
                    tapMapConfiguration,
                    tapCardInputViewWeb,
                    context,
                    knetPayStatusDelegate,
buttonType
                    )
                Log.e("error Config", e.message.toString())
            }
        }

        private fun startSDKWithConfigs(
            tapMapConfiguration: HashMap<String, Any>,
            tapCardInputViewWeb: TapKnetPay?,
            context: Context,
            knetPayStatusDelegate: KnetPayStatusDelegate? = null,
            buttonType: ThreeDsPayButtonType?

            ) {
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
                tapCardInputViewWeb?.init(KnetConfiguration.MapConfigruation,buttonType)

            }
        }

        fun addOperatorHeaderField(
            tapCardInputViewWeb: TapKnetPay?,
            context: Context,
            modelConfiguration: KnetConfiguration,
            publicKey: String?
        ) {
            val encodedeky = getPublicEncryptionKey(publicKey,tapCardInputViewWeb)

            Log.e("packagedname",context.packageName.toString())
            NetworkApp.initNetwork(
                tapCardInputViewWeb?.context ,
                publicKey ?: "",
                context.packageName,
                ApiService.BASE_URL.replace("knetpay?configurations", ""), //TODO please check if replace would be required
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
        private fun getPublicEncryptionKey(
            publicKey: String?,
            tapCardInputViewWeb: TapKnetPay?
        ): String? {
            if (!testEncKey.isNullOrBlank() && !prodEncKey.isNullOrBlank()) {
                return if (publicKey?.contains("test") == true) {
                    // println("EncKey>>>>>" + testEncKey)
                    testEncKey
                } else {
                    //  println("EncKey<<<<<<" + prodEncKey)
                    prodEncKey
                }
            } else {
                //  println("EncKey<<<<<<>>>>>>>>>" + testEncKey)
                return if (publicKey?.contains("test") == true) {
                    tapCardInputViewWeb?.context?.resources?.getString(R.string.enryptkeyTest)
                }else{
                    tapCardInputViewWeb?.context?.resources?.getString(R.string.enryptkeyProduction)
                }


            }
        }
    }


}




