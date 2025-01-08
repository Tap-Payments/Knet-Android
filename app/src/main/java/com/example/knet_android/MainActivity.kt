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
import company.tap.tapWebForm.open.RedirectPayStatusDelegate
import company.tap.tapWebForm.open.web_wrapper.TapRedirectConfiguration
import company.tap.tapWebForm.open.web_wrapper.TapRedirectPay
import company.tap.tapWebForm.open.web_wrapper.enums.ThreeDsPayButtonType
import company.tap.tapcardformkit.open.TapCardStatusDelegate
import company.tap.tapcardformkit.open.web_wrapper.TapCardConfiguration
import company.tap.tapcardformkit.open.web_wrapper.TapCardKit

class MainActivity : AppCompatActivity() ,RedirectPayStatusDelegate{
    lateinit var tapRedirectPay: TapRedirectPay
     var authenticatedToken:String?=""
     var sourceId:String?=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
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

}