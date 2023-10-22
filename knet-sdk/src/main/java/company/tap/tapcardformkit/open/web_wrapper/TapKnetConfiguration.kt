package company.tap.tapcardformkit.open.web_wrapper

import Headers
import android.content.Context
import android.util.Log
import androidx.lifecycle.ProcessLifecycleOwner
import company.tap.tapcardformkit.R
import company.tap.tapcardformkit.open.AppLifecycleObserver
import company.tap.tapcardformkit.open.DataConfiguration
import company.tap.tapcardformkit.open.DataConfiguration.configurationsAsHashMap
import company.tap.tapcardformkit.open.KnetPayStatusDelegate
import company.tap.tapnetworkkit.connection.NetworkApp
import company.tap.tapnetworkkit.utils.CryptoUtil


class TapKnetConfiguration {

    companion object {


        fun configureWithKnetDictionary(
            context: Context,
            tapCardInputViewWeb: TapKnetPay?,
            tapMapConfiguration: java.util.HashMap<String, Any>,
            knetPayStatusDelegate: KnetPayStatusDelegate? = null
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
                    CardConfiguraton.MapConfigruation,
                    publickKey.toString()
                )

                DataConfiguration.addTapBenefitPayStatusDelegate(knetPayStatusDelegate)
                tapCardInputViewWeb?.init(CardConfiguraton.MapConfigruation)

            }
        }

        fun addOperatorHeaderField(
            tapCardInputViewWeb: TapKnetPay?,
            context: Context,
            modelConfiguration: CardConfiguraton,
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
                "android-benefitpay",
                true,
                encodedeky,
                null
            )
            val headers = Headers(
                application = NetworkApp.getApplicationInfo(),
                mdn = CryptoUtil.encryptJsonString(
                    "tap.KnetExample".toString(),
                    encodedeky,
                )
            )

            when (modelConfiguration) {
                CardConfiguraton.MapConfigruation -> {
                    val hashMapHeader = HashMap<String, Any>()
                    hashMapHeader[HeadersMdn] = headers.mdn.toString()
                    hashMapHeader[HeadersApplication] = headers.application.toString()
                    configurationsAsHashMap?.put(headersKey, hashMapHeader)

                }
                else -> {}
            }


        }
    }
}


