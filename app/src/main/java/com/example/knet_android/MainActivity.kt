package com.example.knet_android

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import company.tap.tapWebForm.open.RedirectPayStatusDelegate
import company.tap.tapWebForm.open.web_wrapper.TapRedirectConfiguration
import company.tap.tapWebForm.open.web_wrapper.TapRedirectPay
import company.tap.tapWebForm.open.web_wrapper.enums.ThreeDsPayButtonType

import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() ,RedirectPayStatusDelegate{
    lateinit var tapRedirectPay: TapRedirectPay
     var authenticatedToken:String?=""
     var sourceId:String?=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        callIntentAPI()
        configureSdk()
//        Test - Secret Key	sk_test_bNgRpokWMylX3CBJ6FOresTq


      /*  findViewById<TextView>(R.id.auth_token).setOnClickListener {
            createDialogAndConfigureCardSdk()
        }*/
    }

   /* private fun createDialogAndConfigureCardSdk() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.alert_card_sdk)
        val tapCard = dialog.findViewById<TapCardKit>(R.id.tapCardForm)
        dialog.show()
        val window: Window? = dialog.window
        window?.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        *//**
         * operator
         *//*
        val operator = HashMap<String, Any>()
        operator.put("publicKey", "pk_test_6jdl4Qo0FYOSXmrZTR1U5EHp")
        *//**
         * order
         *//*
        val order = HashMap<String, Any>()
        order.put("id", "")
        order.put("amount", 1)
        order.put("currency", "KWD")
        order.put("description", "")
        order.put("reference", "")


        *//**
         * phone
         *//*
        val phone = HashMap<String, Any>()
        phone.put("countryCode", "+20")
        phone.put("number", "011")

        *//**
         * contact
         *//*
        val contact = HashMap<String, Any>()
        contact.put("email", "test@gmail.com")
        contact.put("phone", phone)
        *//**
         * name
         *//*
        val name = HashMap<String, Any>()
        name.put("lang", "en")
        name.put("first", "Tap")
        name.put("middle", "")
        name.put("last", "Payment")

        *//**
         * customer
         *//*
        val customer = HashMap<String, Any>()
        customer.put("nameOnCard", "")
        customer.put("editable", true)
        customer.put("contact", contact)
        customer.put("name", listOf(name))

        *//**
         * features
         *//*
        val features = HashMap<String,Any>()
        features.put("acceptanceBadge",true)

        *//**
         * configuration
         *//*
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
    }*/

    fun configureSdk() {

      //  val publicKey = intent.getStringExtra("publicKey")

       // val hashStringKey = intent.getStringExtra("hashStringKey")
        val publicKey = "pk_test_J2OSkKAFxu4jghc9zeRfQ0da"

        val hashStringKey = "intent_PJ4l925129XLYf77g0H27" // googlepay
       // val hashStringKey = "intent_6VIv5325653R81D8Pa0p703" // benefitpay
      //  val hashStringKey = "intent_oDrk56251043pfkS72G0l934" // knet




        /**
         * operator
         */
        val operator = HashMap<String,Any>()

        operator.put("publicKey",publicKey.toString())

        /**
         * intent
         */
        val intent = HashMap<String,Any>()
        intent.put("intent",hashStringKey.toString())
        /**
         * configuration
         */


        val configuration = LinkedHashMap<String,Any>()

        configuration.put("operator",operator)
        configuration.put("intent",intent)






        TapRedirectConfiguration.configureWithRedirectDictionary(
            this,
            findViewById(R.id.redirect_pay),
            configuration,
           this)


    }

    override fun onRedirectReady() {
        findViewById<TextView>(R.id.text).text = ""
        findViewById<TextView>(R.id.text).text = "onReady"
        Toast.makeText(this, "onReady", Toast.LENGTH_SHORT).show()
    }

    override fun onRedirectSuccess(data: String) {
        Log.i("onSuccess",data)
        findViewById<TextView>(R.id.text).text = ""
        findViewById<TextView>(R.id.text).text = "onSuccess>>>> $data"
        findViewById<TextView>(R.id.text).movementMethod = ScrollingMovementMethod()

        Toast.makeText(this, "onSuccess $data", Toast.LENGTH_SHORT).show()

    }

    override fun onRedirectClick() {
        Toast.makeText(this, "onClick", Toast.LENGTH_SHORT).show()
        findViewById<TextView>(R.id.text).text = ""
        findViewById<TextView>(R.id.text).text = "onClick "

    }

    override fun onRedirectBindIdentification(data: String) {
        Toast.makeText(this, "onBindIdentification", Toast.LENGTH_SHORT).show()
        findViewById<TextView>(R.id.text).text = ""
        findViewById<TextView>(R.id.text).text = "onBindIdentification $data "
    }

    override fun onRedirectChargeCreated(data: String) {
        Log.e("data",data.toString())
        findViewById<TextView>(R.id.text).text = ""
        findViewById<TextView>(R.id.text).text = "onChargeCreated $data"
        Toast.makeText(this, "onChargeCreated $data", Toast.LENGTH_SHORT).show()

    }

    override fun onRedirectOrderCreated(data: String) {
        findViewById<TextView>(R.id.text).text = ""
        findViewById<TextView>(R.id.text).text = "onOrderCreated >> $data"
        Log.e("mainactv", "onRedirectOrderCreated: "+data )
        Toast.makeText(this, "onOrderCreated $data", Toast.LENGTH_SHORT).show()
    }

    override fun onRedirectcancel() {
        Toast.makeText(this, "Cancel ", Toast.LENGTH_SHORT).show()
    }

    override fun onRedirectError(error: String) {
        Log.e("error",error.toString())
        findViewById<TextView>(R.id.text).text = ""
        findViewById<TextView>(R.id.text).text = "onError $error"
        Toast.makeText(this, "onError $error ", Toast.LENGTH_SHORT).show()

    }

    private fun callIntentAPI(){
        val builder: OkHttpClient.Builder = OkHttpClient().newBuilder()
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        builder.addInterceptor(interceptor)

        val okHttpClient: OkHttpClient = builder.build()
        val mediaType = "application/json".toMediaType()
        val scope =intent.getStringExtra("scopeKey")
        val charge =intent.getStringExtra("scopeKey")



        val jsonObject = JSONObject()
        jsonObject.put("scope", scope)
        jsonObject.put("purpose", charge)
        jsonObject.put("statement_descriptor ", "statement_descriptor")
        jsonObject.put("description ", "sd")
        jsonObject.put("reference ","uuid_testabcdfgkgdgd121992")
        jsonObject.put("customer_initiated ", false)
        jsonObject.put("idempotent ", " ")


      val merchant = JSONObject()
      val terminal = JSONObject()
        terminal.put("id","")
      val terminaldevice = JSONObject()
        terminaldevice.put("id","")

        val operator = JSONObject()
        operator.put("id","")
        val device= JSONObject()
        device.put("id","")
        operator.put("device",device)
        merchant.put("id","")
        merchant.put("terminal",terminal)
        merchant.put("terminal_device",terminaldevice)
        merchant.put("operator",operator)
        val paymentprovider = JSONObject()
        val technology= JSONObject()
        technology.put("id","")

        val institution= JSONObject()
        institution.put("id","")

        paymentprovider.put("technology",technology)
        paymentprovider.put("institution",institution)

        val ddevelopmenthouse= JSONObject()
        ddevelopmenthouse.put("id","")

        val platform= JSONObject()
        platform.put("id","")

        merchant.put("payment_provider",paymentprovider)
        merchant.put("institution",institution)
        merchant.put("development_house",ddevelopmenthouse)
        merchant.put("platform",platform)

        jsonObject.put("merchant", merchant)

        val authenticate = JSONObject()
        authenticate.put("id","")
        authenticate.put("required",true)

        jsonObject.put("authenticate", authenticate)
        val transaction = JSONObject()
        val cardholderlogin = JSONObject()
        cardholderlogin.put( "type ", "GUEST ")
        cardholderlogin.put(  "timestamp ", "123213213")

        val payment_agreement  = JSONObject()

        val contract = JSONObject()
        contract.put("id","")
        payment_agreement.put("id","")
        payment_agreement.put("contract",contract)

        transaction.put("card_holder_login",cardholderlogin)
        transaction.put("metadata","")
        transaction.put("reference","")
        transaction.put("payment_agreement",payment_agreement)
        jsonObject.put("transaction", transaction)

        val invoice = JSONObject()
        invoice.put("id","")
        jsonObject.put("invoice", invoice)


        val descriptiom =JSONObject()
        descriptiom.put("text", "name ")
        descriptiom.put( "lang ", "en ")

        val reference = JSONObject()
        reference.put("sku ", "stock keeping unit ")
        reference.put( "gtin ", "global trade item number ")
        reference.put("code ", "00dfd ")
        reference.put("financial_code ", "0022343 ")


        val product = JSONObject()
        product.put("id","")
        product.put("amount",2)
        product.put("name","")
        product.put("description",descriptiom)
        product.put("metadata","descriptiom")
        product.put("category","PHYSICAL_GOODS")
        product.put("reference",reference)
        val  itemsList = JSONObject()
        itemsList.put(  "id ", " ")
        itemsList.put(  "quantity ", 1)
        itemsList.put(    "pickup ", false)
        itemsList.put(  "product ", product)

        val itemsArry =  JSONArray()
        itemsArry.put(itemsList)
        val items = JSONObject()
        items.put("count",1)
        items.put("list",itemsArry)



        val order = JSONObject()
        order.put("amount",intent.getStringExtra("amountKey"))
        order.put("currency",intent.getStringExtra("orderCurrencyKey"))
        order.put("description",descriptiom)
        order.put("reference",intent.getStringExtra("orderRefrenceKey"))
        order.put("items",items)
        order.put("tax","")
        order.put("discount","")
        order.put("shipping","")
        order.put("metadata","")


        jsonObject.put("order",order)

        val name = JSONObject()
        name.put( "first ", "OSAMA ")
        name.put(  "last ", "Ahmed ")
        name.put(   "middle ", " ")
        name.put(  "title ","MR ")

        val nameList = JSONArray()
        nameList.put(name)

        val nameCard = JSONObject()
        nameCard.put(  "content ", "OSAMA AHMED ")
        nameCard.put(   "editable ",true)

        val customer = JSONObject()
        customer.put("id",intent.getStringExtra("customerIdKey"))
        customer.put("name",nameList)
        customer.put("name_on_card",nameCard)
        customer.put("contact","")
        customer.put("address","")


        val receipt = JSONObject()
        receipt.put("email", false)
        receipt.put("sms", false)

        jsonObject.put("receipt", receipt)


        val configOb = JSONObject()
        configOb.put("initiator","MERCHANT")
        configOb.put("type","BUTTON")
        val features = JSONObject()
        features.put("acceptance_badge",true)
        features.put("order",true)
        features.put("multiple_currencies",true)

        val currency_conversions = JSONObject()
        currency_conversions.put("dynamic", true)
        currency_conversions.put("location ", true)
        currency_conversions.put( "payment ", true)
        currency_conversions.put( "cobadge ", true)

        val alternative_card_inputs = JSONObject()
        alternative_card_inputs.put("card_scanner", true)
        alternative_card_inputs.put("card_nfc ", true)

        val customer_cards = JSONObject()
        customer_cards.put("save_card", true)
        customer_cards.put("auto_save_card ", true)
        customer_cards.put("display_saved_cards ", true)


        val payments = JSONObject()
        payments.put("card", true)
        payments.put("device ", true)
        payments.put( "wallet ", true)

        features.put("currency_conversions",currency_conversions)
        features.put("payments",payments)
        features.put("alternative_card_inputs",alternative_card_inputs)
        features.put("customer_cards",customer_cards)

        val acceptance = JSONObject()
        val supported_regions = JSONArray()
        supported_regions.put("LOCAL")
        supported_regions.put("REGIONAL")
        supported_regions.put("GLOBAL")

        val supported_countries = JSONArray()
        supported_countries.put("AE ")
        supported_countries.put(  "SA ")
        supported_countries.put(  "KW ")
        supported_countries.put( "EG ")

        val supported_payment_types = JSONArray()
        supported_payment_types.put("KWD" )
        supported_payment_types.put(        "SAR ")
            supported_payment_types.put("AED ")
                supported_payment_types.put(   "OMR ")
                    supported_payment_types.put(    "QAR ")
                        supported_payment_types.put(    "BHD ")
                            supported_payment_types.put(   "EGP ")
                                supported_payment_types.put(   "GBP ")
                                    supported_payment_types.put(   "USD ")
                                        supported_payment_types.put(  "EUR ")
                                            supported_payment_types.put(  "AED ")

        val supported_payment_methods = JSONArray()
            supported_payment_methods.put ("CARD")
        supported_payment_methods.put("DEVICE")
        supported_payment_methods.put("WEB")

        val supported_schemes = JSONArray()
        supported_schemes.put("CARD ")
        supported_schemes.put( "MADA ")
        supported_schemes.put("OMANNET ")
        supported_schemes.put("VISA ")
        supported_schemes.put( "MASTERCARD ")
        supported_schemes.put("AMEX")
        supported_schemes.put("BENEFIT_CARD" )
        val supported_fund_source = JSONArray()
        supported_fund_source.put("CARD ")
        supported_fund_source.put("DEBIT ")
        supported_fund_source.put("CREDIT ")
        val supported_payment_flows = JSONArray()
        supported_payment_flows.put("CARD ")
        supported_payment_flows.put(    "POPUP ")
        supported_payment_flows.put(   "PAGE ")
        val supported_payment_authentications = JSONArray()
        supported_payment_authentications.put("3DS ")
        supported_payment_authentications.put(    "EMV ")
        supported_payment_authentications.put(   "PASSKEY ")
        acceptance.put("supported_regions",supported_regions)
        acceptance.put("supported_countries",supported_countries)
        acceptance.put("supported_payment_types",supported_payment_types)
        acceptance.put("supported_payment_methods",supported_payment_methods)
        acceptance.put("supported_schemes",supported_schemes)
        acceptance.put("supported_fund_source",supported_fund_source)
        acceptance.put("supported_payment_authentications",supported_payment_authentications)
        acceptance.put("supported_payment_flows",supported_payment_flows)

        val interfacee = JSONObject()
        interfacee.put("locale",intent.getStringExtra("selectedlangKey") ?: "en")
        interfacee.put("theme",intent.getStringExtra("selectedthemeKey") ?: "light")
        interfacee.put("edges",intent.getStringExtra("selectedcardedgeKey") ?: "curved")
        interfacee.put("colorStyle",intent.getStringExtra("selectedcolorstyleKey") ?:"colored")
        interfacee.put("user_experience","POPUP")
        interfacee.put("direction","DYNAMIC")
        interfacee.put("loader ", true)
        interfacee.put("powered ", true)



        configOb.put("features",features)
        configOb.put("acceptance",acceptance)
        configOb.put("field_visibility","")
        configOb.put("interface",interfacee)


        jsonObject.put("config", configOb)
        jsonObject.put("domain","")
        jsonObject.put("redirect","")
        jsonObject.put("post","")
        jsonObject.put("checkout","")

        val body = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url("https://api.tap.company/v2/intent")
            .post(body)
            .addHeader("Authorization", "Bearer sk_test_NSln5js3fIeq0QU1MuKRXAkD")
            .addHeader("Content-Type", "application/json")
            .build()
       okHttpClient.newCall(request).enqueue(object : Callback{
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    var responseBody: JSONObject? =
                        response.body?.string()?.let { JSONObject(it) } // toString() is not the response body, it is a debug representation of the response body

                    if(!responseBody.toString().contains("errors")){
                        println("responseBody>>"+responseBody)
                    }else{


                    }

                } catch (ex: JSONException) {
                    throw RuntimeException(ex)
                }
            }

        })
    }

}