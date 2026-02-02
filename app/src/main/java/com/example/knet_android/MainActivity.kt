package com.example.knet_android

import android.app.Dialog
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.Window
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.chillibits.simplesettings.tool.getPrefStringValue
import com.example.knet_android.cardSdk.model.CardResponse
import com.google.gson.Gson
import company.tap.tapWebForm.open.KnetPayStatusDelegate
import company.tap.tapWebForm.open.web_wrapper.TapKnetConfiguration
import company.tap.tapWebForm.open.web_wrapper.TapKnetPay
import company.tap.tapWebForm.open.web_wrapper.enums.ThreeDsPayButtonType
import company.tap.tapcardformkit.open.TapCardStatusDelegate
import company.tap.tapcardformkit.open.web_wrapper.TapCardConfiguration
import company.tap.tapcardformkit.open.web_wrapper.TapCardKit

class MainActivity : AppCompatActivity() ,KnetPayStatusDelegate{
    lateinit var tapKnetPay: TapKnetPay
     var authenticatedToken:String?=""
     var sourceId:String?=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        configureSdk()

//        Bundle ID	tap.KnetExample
//        Prod - Public Key	pk_live_I8aWfZkiGtw9HYsRCcAgQzS6
//        Test - Public Key	pk_test_6jdl4Qo0FYOSXmrZTR1U5EHp



        findViewById<TextView>(R.id.auth_token).setOnClickListener {
            createDialogAndConfigureCardSdk()
        }
    }

    private fun createDialogAndConfigureCardSdk() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.alert_card_sdk)
        val tapCard = dialog.findViewById<TapCardKit>(R.id.tapCardForm)
        dialog.show()
        val window: Window? = dialog.window
        window?.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        /**
         * operator
         */
        val operator = HashMap<String, Any>()
        operator.put("publicKey", "pk_test_6jdl4Qo0FYOSXmrZTR1U5EHp")
        /**
         * order
         */
        val order = HashMap<String, Any>()
        order.put("id", "")
        order.put("amount", 1)
        order.put("currency", "KWD")
        order.put("description", "")
        order.put("reference", "")


        /**
         * phone
         */
        val phone = HashMap<String, Any>()
        phone.put("countryCode", "+20")
        phone.put("number", "011")

        /**
         * contact
         */
        val contact = HashMap<String, Any>()
        contact.put("email", "test@gmail.com")
        contact.put("phone", phone)
        /**
         * name
         */
        val name = HashMap<String, Any>()
        name.put("lang", "en")
        name.put("first", "Tap")
        name.put("middle", "")
        name.put("last", "Payment")

        /**
         * customer
         */
        val customer = HashMap<String, Any>()
        customer.put("nameOnCard", "")
        customer.put("editable", true)
        customer.put("contact", contact)
        customer.put("name", listOf(name))

        /**
         * features
         */
        val features = HashMap<String,Any>()
        features.put("acceptanceBadge",true)

        /**
         * configuration
         */
        val configuration = LinkedHashMap<String, Any>()

        configuration.put("operator", operator)
        configuration.put("scope", "AuthenticatedToken")
        configuration.put("order", order)
        configuration.put("customer", customer)
        configuration.put("features",features)





        TapCardConfiguration.configureWithTapCardDictionaryConfiguration(
            context = this,
            cardNumber = "4111111111111111",
            cardExpiry = "10/24",
            tapCardInputViewWeb = tapCard,
            tapMapConfiguration = configuration,
            tapCardStatusDelegate = object : TapCardStatusDelegate {
                override fun onCardError(error: String) {
                    Log.e("error", error.toString())
                }

                override fun onCardSuccess(data: String) {
                   // authenticateID = data
                    val gson = Gson()
                    val neededData = gson.fromJson(data, CardResponse::class.java)
                    authenticatedToken = neededData.id
                    sourceId = neededData.source.id
                    Log.e("authToken", neededData.id.toString())
                    Log.e("sourceID", sourceId.toString())
                    dialog.dismiss()
                    configureSdk(authenticatedToken,sourceId)


                }

                override fun onValidInput(isValid: String) {
                    Log.e("isValid", isValid.toString())
                   tapCard.generateTapToken()

                }

            })
    }

    fun configureSdk(authenticatedToken: String?="", sourceId: String?="") {


        /**
         * operator
         */
        val publicKey = intent.getStringExtra("publicKey")
        val hashStringKey = intent.getStringExtra("hashStringKey")
        val buttonKey = intent.getStringExtra("buttonKey")

        val operator = HashMap<String,Any>()

        operator.put("publicKey","pk_test_6jdl4Qo0FYOSXmrZTR1U5EHp")
        operator.put("hashString",hashStringKey.toString())
        Log.e("orderData","pbulc" + publicKey.toString() + " \nhash" + hashStringKey.toString())
        Log.e("buttonKey","buttonKey" + buttonKey.toString())

        /**
         * metadata
         */
        val metada = HashMap<String,Any>()
        metada.put("id","")

        /**
         * order
         */
        val ordrId =  intent.getStringExtra("orderIdKey")
        val orderDescription =  intent.getStringExtra("orderDescKey")
        val orderAmount =  intent.getStringExtra("amountKey")
        val orderRefrence =  intent.getStringExtra("orderTransactionRefrence")
        val selectedCurrency: String = intent.getStringExtra("selectedCurrencyKey").toString()

        val order = HashMap<String,Any>()
        order.put("id",ordrId.toString())
        order.put("amount",  if (orderAmount?.isEmpty() == true)"1" else orderAmount.toString() )
        order.put("currency",selectedCurrency)
       // order.put("description",orderDescription ?: "")
       // order.put("reference",orderRefrence ?: "")
      //  order.put("metadata",metada)
        Log.e("orderData","id" + ordrId.toString() + "  \n dest" + orderDescription.toString() +" \n orderamount " + orderAmount.toString() +"  \n orderRef" + orderRefrence.toString() + "  \n currency " + selectedCurrency.toString())

        /**
         * merchant
         */
        val merchant = HashMap<String,Any>()
        merchant.put("id", "")

        /**
         * invoice
         */
        val invoice = HashMap<String,Any>()
        invoice.put("id","")


        /**
         * phone
         */
        val phone = java.util.HashMap<String,Any>()
        phone.put("countryCode","+965")
        phone.put("number","6617")


        /**
         * contact
         */
        val contact = java.util.HashMap<String,Any>()
        contact.put("email","test@gmail.com")
        contact.put("phone",phone)
        /**
         * name
         */
        val name = java.util.HashMap<String,Any>()
        name.put("lang","en")
        name.put("first", "first")
        name.put("middle", "middle")
        name.put("last","last")

        /**
         * customer
         */
        val customer = java.util.HashMap<String,Any>()
        customer.put("id", "")
        customer.put("contact",contact)
        customer.put("name", listOf(name))

        /**
         * interface
         */

        val selectedLanguage: String? =  intent.getStringExtra("selectedlangKey")
        val selectedTheme: String? = intent.getStringExtra("selectedthemeKey")
        val selectedCardEdge = intent.getStringExtra("selectedcardedgeKey")
        val selectedColorStylee = intent.getStringExtra("selectedcolorstyleKey")
        val loader = intent.getBooleanExtra("loaderKey",false)

        Log.e("interfaceData",selectedTheme.toString() + "language" + selectedLanguage.toString() + "cardedge " + selectedCardEdge.toString() +" loader" + loader.toString() + "selectedColorStylee " + selectedColorStylee.toString())
        val interfacee = HashMap<String,Any>()
        interfacee.put("edges",selectedCardEdge ?: "curved")
        interfacee.put("locale",selectedLanguage ?: "en")
        interfacee.put("theme",selectedTheme ?: "light")
        interfacee.put("colorStyle",selectedColorStylee ?:"colored")
        interfacee.put("loader",loader)

        val posturl =  intent.getStringExtra("posturlKey")
        val redirectUrl =  intent.getStringExtra("redirectUrlKey")


        val post = HashMap<String,Any>()
        post.put("url",posturl?: "")

//        val redirect = HashMap<String,Any>()
//        redirect.put("url","onTapKnetRedirect://")
        val configuration = LinkedHashMap<String,Any>()


        /**
         * transaction && scope
         */

        intent.putExtra("scopeKey", getPrefStringValue("scopeKey","Token"))
        intent.putExtra("transactionRefrenceKey", getPrefStringValue("transactionRefrenceKey",""))
        intent.putExtra("transactionAuthroizeTypeKey", getPrefStringValue("transactionAuthroizeTypeKey",""))
        intent.putExtra("transactionAuthroizeTimeKey", getPrefStringValue("transactionAuthroizeTimeKey",""))

        val transaction = HashMap<String,Any>()
        val scopeKey = intent.getStringExtra("scopeKey")
        val transactionRefrenceKey = intent.getStringExtra("transactionRefrenceKey")
        val transactionAuthroizeTypeKey = intent.getStringExtra("transactionAuthroizeTypeKey")
        val transactionAuthroizeTimeKey = intent.getStringExtra("transactionAuthroizeTimeKey")
        val transactionSourceId = intent.getStringExtra("transactionSourceId")
        val transactionAuthenticationId = intent.getStringExtra("transactionAuthenticationId")
        this.authenticatedToken = transactionAuthenticationId
        this.sourceId = transactionSourceId

        Log.e("scope","scope is : " + scopeKey.toString() + " transactionRefrenceKey : " +  " " + transactionRefrenceKey.toString() +  " transactionAuthroizeTypeKey : " + transactionAuthroizeTypeKey.toString() + " transactionAuthroizeTimeKey : " + transactionAuthroizeTimeKey.toString())
        val authorize = HashMap<String,Any>()
        authorize.put("type",transactionAuthroizeTypeKey ?:"")
        authorize.put("time",transactionAuthroizeTimeKey ?:"")
        val paymentAgreement = HashMap<String,Any>()
        val contract = HashMap<String,Any>()
        contact.put("id","")
        paymentAgreement.put("id","")
        paymentAgreement.put("contract",contract)

        /**
         * authenticate for Card buttons
         */
        val authenticate = HashMap<String,Any>()
        authenticate.put("id", this.authenticatedToken ?: "")
        authenticate.put("required",true)
        /**
         * source for Card buttons
         */
        val source = HashMap<String,Any>()
        source.put("id", this.sourceId ?: "")

     //   transaction.put("reference",transactionRefrenceKey?: "")  NOT FOR NOW
      //  transaction.put("authorize",authorize?: "")  NOT FOR NOW

       // transaction.put("paymentAgreement",paymentAgreement)
      //  transaction.put("metadata",metada) NOT FOR NOW
        transaction.put("authenticate",authenticate)
        transaction.put("source",source)
        transaction.put("authentication",true)



        /**
         * acceptance
         */


        val supportedFund =  intent.getSerializableExtra("supportedFundSourceKey") as HashSet<*>

        val supportedPaymentAuthentications =  intent.getSerializableExtra("supportedPaymentAuthenticationsKey") as HashSet<*>
        val supportedSchemes =  intent.getSerializableExtra("supportedSchemesKey") as HashSet<*>
        val supportedPaymentMethods =  intent.getSerializableExtra("supportedPaymentMethodKey") as HashSet<*>
        Log.e("acceptance",
            "suppored fund is : $supportedFund supportedPaymentAuthentications :  $supportedPaymentAuthentications supportedSchemes : $supportedSchemes"
        )
        val acceptance = HashMap<String,Any>()
      /* acceptance.put("supportedFundSource",supportedFund)
        acceptance.put("supportedPaymentAuthentications",supportedPaymentAuthentications)
        acceptance.put("supportedSchemes",supportedSchemes) */

        acceptance.put("supportedPaymentMethod",buttonKey?.toLowerCase().toString()) //TODO check what has to be passed dynamic
        acceptance.put("supportedSchemes",supportedSchemes)
        /**
         * fields
         */
        val fieldVisibility = HashMap<String,Any>()

        /**
         * Fields visibility **/
        val cardHolder =  intent.getBooleanExtra("cardHolder",true)
        val cvv =  intent.getBooleanExtra("cvv",true)
        /**
         * card
         */
        val card = HashMap<String,Any>()
        card.put("cvv",cvv)
        card.put("cardHolder",cardHolder)
        fieldVisibility.put("card",card)
        /**
         * customerCards
         */
        val customerCards = HashMap<String,Any>()
        val saveCard =  intent.getBooleanExtra("saveCard",true)
        val autoSaveCard =  intent.getBooleanExtra("autoSaveCard",true)
        customerCards.put("saveCard",saveCard)
        customerCards.put("autoSaveCard",autoSaveCard)

        /**
         * features
         */
        val features = HashMap<String,Any>()
        val showCardBrands: Boolean = intent.getBooleanExtra("selectedCardBrand", true)
        features.put("acceptanceBadge",showCardBrands)
        features.put("customerCards",customerCards)


        /**
         * configuration
         */




        configuration.put("scope",scopeKey.toString())
        configuration.put("operator",operator)
        configuration.put("acceptance",acceptance)
        configuration.put("debug",true)
        configuration.put("merchant",merchant)
        configuration.put("order",order)
        configuration.put("transaction",transaction)
        configuration.put("customer",customer)
        configuration.put("interface",interfacee)
        configuration.put("features",features)
        configuration.put("redirect_url","https://demo.dev.tap.company/v2/sdk/button?paymentMethod=knet") // TODO what will be in this
        configuration.put("data-testid","TapButton")
        configuration.put("platform","mobile")
        configuration.put("language",selectedLanguage ?: "dynamic")
        configuration.put("themeMode",selectedTheme ?:"dynamic")
        configuration.put("edges",selectedCardEdge ?:"dynamic")
        configuration.put("paymentMethod",buttonKey?.toLowerCase().toString()) //TODO what has to be sent here

      //  configuration.put("invoice",invoice)

      //  configuration.put("post",post)



       // configuration.put("fieldVisibility",fieldVisibility)





        TapKnetConfiguration.configureWithKnetDictionary(
            this,
            findViewById(R.id.knet_pay),
            configuration,
           this,ThreeDsPayButtonType.valueOf(buttonKey.toString()))


    }

    override fun onKnetReady() {
        findViewById<TextView>(R.id.text).text = ""
        findViewById<TextView>(R.id.text).text = "onReady"
        Toast.makeText(this, "onReady", Toast.LENGTH_SHORT).show()
    }

    override fun onKnetSuccess(data: String) {
        Log.i("onSuccess",data)
        findViewById<TextView>(R.id.text).text = ""
        findViewById<TextView>(R.id.text).text = "onSuccess $data"
        findViewById<TextView>(R.id.text).movementMethod = ScrollingMovementMethod()

        Toast.makeText(this, "onSuccess $data", Toast.LENGTH_SHORT).show()

    }

    override fun onKnetClick() {
        Toast.makeText(this, "onClick", Toast.LENGTH_SHORT).show()
        findViewById<TextView>(R.id.text).text = ""
        findViewById<TextView>(R.id.text).text = "onClick "

    }

    override fun onKnetBindIdentification(data: String) {
        Toast.makeText(this, "onBindIdentification", Toast.LENGTH_SHORT).show()
        findViewById<TextView>(R.id.text).text = ""
        findViewById<TextView>(R.id.text).text = "onBindIdentification $data "
    }

    override fun onKnetChargeCreated(data: String) {
        Log.e("data",data.toString())
        findViewById<TextView>(R.id.text).text = ""
        findViewById<TextView>(R.id.text).text = "onChargeCreated $data"
        Toast.makeText(this, "onChargeCreated $data", Toast.LENGTH_SHORT).show()

    }

    override fun onKnetOrderCreated(data: String) {
        findViewById<TextView>(R.id.text).text = ""
        findViewById<TextView>(R.id.text).text = "onOrderCreated $data"
        Toast.makeText(this, "onOrderCreated $data", Toast.LENGTH_SHORT).show()
    }

    override fun onKnetcancel() {
        Toast.makeText(this, "Cancel ", Toast.LENGTH_SHORT).show()
    }

    override fun onKnetError(error: String) {
        Log.e("error",error.toString())
        findViewById<TextView>(R.id.text).text = ""
        findViewById<TextView>(R.id.text).text = "onError $error"
        Toast.makeText(this, "onError $error ", Toast.LENGTH_SHORT).show()

    }

}