package com.example.knet_android

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.preference.Preference
import com.chillibits.simplesettings.core.SimpleSettings
import com.chillibits.simplesettings.core.SimpleSettingsConfig
import com.chillibits.simplesettings.tool.getPrefBooleanValue
import com.chillibits.simplesettings.tool.getPrefStringSetValue
import com.chillibits.simplesettings.tool.getPrefStringValue

class SettingsActivity : AppCompatActivity(),SimpleSettingsConfig.PreferenceCallback  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val configuration = SimpleSettingsConfig.Builder()
            .setActivityTitle("Configuration")
            .setPreferenceCallback(this)
            .displayHomeAsUpEnabled(false)
            .build()


        SimpleSettings(this, configuration).show(R.xml.preferences)

    }


    override fun onPreferenceClick(context: Context, key: String): Preference.OnPreferenceClickListener? {
        return when(key) {
            "dialog_preference" -> Preference.OnPreferenceClickListener {
                navigateToMainActivity()
                true
            }
            else -> super.onPreferenceClick(context, key)
        }
    }
    fun navigateToMainActivity(){
        val intent = Intent(this, MainActivity::class.java)
        /**
         * operator
         */
        intent.putExtra("publicKey", getPrefStringValue("publicKey","pk_test_YhUjg9PNT8oDlKJ1aE2fMRz7"))
        intent.putExtra("hashStringKey", getPrefStringValue("hashStringKey","pk_test_YhUjg9PNT8oDlKJ1aE2fMRz7"))

        /**
         * order
         */
        intent.putExtra("orderIdKey", getPrefStringValue("orderIdKey",""))
        intent.putExtra("orderDescKey", getPrefStringValue("orderDescKey","test"))
        intent.putExtra("amountKey", getPrefStringValue("amountKey","1"))
        intent.putExtra("orderTransactionRefrence", getPrefStringValue("orderTransactionRefrence","test"))
        intent.putExtra("selectedCurrencyKey", getPrefStringValue("selectedCurrencyKey","test"))


        /**
         * interface
         */

        intent.putExtra("selectedcardedgeKey",if (getPrefStringValue("selectedcardedgeKey","") == "1")  "flat" else  getPrefStringValue("selectedcardedgeKey","flat"))
        intent.putExtra("selectedCardDirection", if (getPrefStringValue("selectedcardirectKey","") == "0") "ltr" else getPrefStringValue("selectedcardirectKey","dynamic"))
        intent.putExtra("selectedcolorstyleKey", getPrefStringValue("selectedcolorstyleKey","colored"))
        intent.putExtra("selectedthemeKey", if (getPrefStringValue("selectedthemeKey","") == "1") TapTheme.light.name else  getPrefStringValue("selectedthemeKey","light"))
        intent.putExtra("selectedlangKey", if (getPrefStringValue("selectedlangKey","") == "1") "en" else getPrefStringValue("selectedlangKey", default = "en"))
        intent.putExtra("loaderKey", getPrefBooleanValue("loaderKey",true))


        /**
         * posturl
         */


        intent.putExtra("posturlKey", getPrefStringValue("posturlKey",""))
        intent.putExtra("redirectUrlKey", getPrefStringValue("redirectUrlKey",""))


        /**
         * scope && transaction
         */

        intent.putExtra("scopeKey", getPrefStringValue("scopeKey","Token"))
        intent.putExtra("transactionRefrenceKey", getPrefStringValue("transactionRefrenceKey",""))
        intent.putExtra("transactionAuthroizeTypeKey", getPrefStringValue("transactionAuthroizeTypeKey",""))
        intent.putExtra("transactionAuthroizeTimeKey", getPrefStringValue("transactionAuthroizeTimeKey",""))
        intent.putExtra("buttonKey", getPrefStringValue("buttonKey",""))

        intent.putExtra("transactionSourceId", getPrefStringValue("transactionSourceId",""))
        intent.putExtra("transactionAuthenticationId", getPrefStringValue("transactionAuthenticationId",""))

        /**
         * acceptance
         */
        intent.putExtra("supportedFundSourceKey", getPrefStringSetValue("supportedFundSourceKey", emptySet()).toHashSet())
        intent.putExtra("supportedPaymentAuthenticationsKey", getPrefStringSetValue("supportedPaymentAuthenticationsKey", emptySet()).toHashSet())
        intent.putExtra("supportedSchemesKey", getPrefStringSetValue("supportedSchemesKey", emptySet()).toHashSet())
        intent.putExtra("supportedPaymentMethodKey", getPrefStringSetValue("supportedPaymentMethodKey", emptySet()).toHashSet())
        /**
         * Fields Visibility
         ***/
        intent.putExtra("cardHolder",  getPrefBooleanValue("displayHoldernameKey",true))
        intent.putExtra("cvv",getPrefBooleanValue("displayCVVKey",true))

        /**
         * Features
         ***/
        intent.putExtra("selectedCardBrand", getPrefBooleanValue("displayPymtBrndKey",true))
        intent.putExtra("saveCard", getPrefBooleanValue("displaySaveCardKey",true))
        intent.putExtra("autoSaveCard", getPrefBooleanValue("displayAutosaveCardKey",true))

        finish()
        startActivity(intent)

    }
}