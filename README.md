[//]: # (# BenefitPay-Android)

[//]: # (Integrating Android BenefitPay SDK in your application)

[//]: # ()
[//]: # (# Introduction)

[//]: # (Before diving into the development process, it's essential to establish the prerequisites and criteria necessary for a successful build. In this step, we'll outline the specific Android requirements, including the minimum SDK version and other important details you need to consider. Let's ensure your project is set up for success from the very beginning.)

[//]: # ()
[//]: # ([![Platform]&#40;https://img.shields.io/badge/platform-Android-inactive.svg?style=flat&#41;]&#40;https://tap-payments.github.io/goSellSDK-Android/&#41;)

[//]: # ([![Documentation]&#40;https://img.shields.io/badge/documentation-100%25-bright%20green.svg&#41;]&#40;https://tap-payments.github.io/goSellSDK-Android/&#41;)

[//]: # ([![SDK Version]&#40;https://img.shields.io/badge/minSdkVersion-24-blue.svg&#41;]&#40;https://stuff.mit.edu/afs/sipb/project/android/docs/reference/packages.html&#41;)

[//]: # ([![SDK Version]&#40;https://img.shields.io/badge/targetSdkVersion-33-informational.svg&#41;]&#40;https://stuff.mit.edu/afs/sipb/project/android/docs/reference/packages.html&#41;)

[//]: # ([![SDK Version]&#40;https://img.shields.io/badge/latestVersion-0.0.3-informational.svg&#41;]&#40;https://stuff.mit.edu/afs/sipb/project/android/docs/reference/packages.html&#41;)

[//]: # ()
[//]: # ()
[//]: # (# Sample Demo)

[//]: # (![Imgur]&#40;https://imgur.com/Rw2vb6J.gif&#41;)

[//]: # ()
[//]: # (# Step 1 :Requirements)

[//]: # ()
[//]: # ( 1. We support from Android minSdk 24)

[//]: # ( 2. Kotlin support version 1.8.0+)

[//]: # ()
[//]: # (# Step 2 :Get Your Public Keys)

[//]: # ()
[//]: # ( While you can certainly use the sandbox keys available within our sample app which you can get by following)

[//]: # ( [installation page]&#40;https://developers.tap.company/docs/&#41;,)

[//]: # ( however, we highly recommend visiting our [onboarding page]&#40;https://register.tap.company/sell&#41;, there you'll have the opportunity to register your package name and acquire your essential Tap Key for activating BenefitPay-android integration.)

[//]: # ()
[//]: # (# Step 3 :Installation)

[//]: # ()
[//]: # (## Gradle)

[//]: # ()
[//]: # (in project module gradle )

[//]: # ()
[//]: # (```kotlin)

[//]: # (allprojects {)

[//]: # (    repositories {)

[//]: # (        google&#40;&#41;)

[//]: # (        jcenter&#40;&#41;)

[//]: # (        maven { url 'https://jitpack.io' })

[//]: # (    })

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (Then get latest dependency  in your app module gradle)

[//]: # (```kotlin)

[//]: # (dependencies {)

[//]: # (  implementation : 'com.github.Tap-Payments:BenefitPay-Android:{latest-tag-release}')

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (# Step 3 :Integrating BenefitPay-Android)

[//]: # (This integration offers two distinct options: a [simple integration]&#40;https://register.tap.company/sell&#41; designed for rapid development and streamlined merchant requirements, and an [advanced integration]&#40;https://register.tap.company/sell&#41; that adds extra features for a more dynamic payment integration experience.)

[//]: # ()
[//]: # (# Integration Flow)

[//]: # (Noting that in Android, you have the ability to create the UI part of the BenefitPay-Android by creating it as normal view in your XML then implement the functionality through code or fully create it by code. Below we will describe both flows:)

[//]: # ()
[//]: # (You will have to create a variable of type BenefitPayButton, which can be done in one of two ways:)

[//]: # ( - Created in the XML and then linked to a variable in code.)

[//]: # ( - Created totally within the code.)

[//]: # (Once you create the variable in any way, you will have to follow these steps:)

[//]: # ( - Create the parameters.)

[//]: # ( - Pass the parameters to the variable.)

[//]: # ( - Implement TapBenefitPayStatusDelegate interface, which allows you to get notified by different events fired from within the BenefitPay-Android SDK, also called callback functions.)

[//]: # ()
[//]: # (# Initialising the UI)

[//]: # (## Using xml)

[//]: # (  ### 1- create view in xml)

[//]: # ()
[//]: # (```kotlin)

[//]: # (<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android")

[//]: # (    xmlns:app="http://schemas.android.com/apk/res-auto")

[//]: # (    xmlns:tools="http://schemas.android.com/tools")

[//]: # (    android:layout_width="match_parent")

[//]: # (    android:orientation="vertical")

[//]: # (    android:layout_height="match_parent")

[//]: # (    tools:context=".main_activity.MainActivity">)

[//]: # ()
[//]: # ( <company.tap.tapcardformkit.open.web_wrapper.TapBenefitPay)

[//]: # (        android:id="@+id/tapBenefitPay")

[//]: # (        app:layout_constraintStart_toStartOf="parent")

[//]: # (        app:layout_constraintTop_toTopOf="parent")

[//]: # (        app:layout_constraintEnd_toEndOf="parent")

[//]: # (        android:layout_width="match_parent")

[//]: # (        android:layout_height="wrap_content")

[//]: # (        />)

[//]: # ()
[//]: # (</LinearLayout>)

[//]: # ( )
[//]: # (```)

[//]: # (### 2- Accessing the BenefitPayButton created in XML in your code )

[//]: # (### 3. Create an TapBenefitPay instance from the created view above to your Activity :)

[//]: # (```kotlin)

[//]: # (    lateinit var tapBenefitPay: TapBenefitPay)

[//]: # (    override fun onCreate&#40;savedInstanceState: Bundle?&#41; {)

[//]: # (        super.onCreate&#40;savedInstanceState&#41;)

[//]: # (        setContentView&#40;R.layout.activity_main&#41;)

[//]: # (taBenefitPay = findViewById<TapBenefitPay>&#40;R.id.tapBenefitPay&#41;)

[//]: # (    })

[//]: # ()
[//]: # (```)

[//]: # ()
[//]: # (## Using Code to create the BenefitPayButton)

[//]: # ()
[//]: # (```kotlin)

[//]: # (     lateinit var tapBenefitPay: TapBenefitPay)

[//]: # (        override fun onCreate&#40;savedInstanceState: Bundle?&#41; {)

[//]: # (        super.onCreate&#40;savedInstanceState&#41;)

[//]: # (        setContentView&#40;R.layout.activity_main&#41;)

[//]: # ()
[//]: # (         val linearLayoutParams = LinearLayout.LayoutParams&#40;)

[//]: # (            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)

[//]: # (        &#41;)

[//]: # (        /** create dynamic view of TapBenefitPay view **/ )

[//]: # (        tapBenefitPay  = TapBenefitPay&#40;this&#41;)

[//]: # (        tapBenefitPay.layoutParams = linearLayoutParams)

[//]: # (        /** refrence to parent layout view **/  )

[//]: # (        this.findViewById<LinearLayout>&#40;R.id.linear_layout&#41;.addView&#40;tapBenefitPay&#41;)

[//]: # (})

[//]: # (```)

[//]: # (# Simple Integration)

[//]: # (Here, you'll discover a comprehensive table featuring the parameters applicable to the simple integration. Additionally, you'll explore the various methods for integrating the SDK, either using xml to create the layout and then implementing the interface  functionalities by code, or directly using code. Furthermore, you'll gain insights into how to receive the callback notifications.)

[//]: # (# Parameters)

[//]: # (Each parameter is linked to the reference section, which provides a more in depth explanation of it.)

[//]: # ()
[//]: # (|Parameters|Description | Required | Type| Sample)

[//]: # (|--|--|--| --|--|)

[//]: # (| operator| This is the `Key` that you will get after registering you package name. | True  | String| `var operator=HashMap<String,Any>&#40;&#41;,operator.put&#40;"publicKey","pk_test_YhUjg9PNT8oDlKJ1aE2fMRz7"&#41;,operator.put&#40;"hashString",""&#41;` |)

[//]: # (| order| This is the `order id` that you created before or `amount` , `currency` , `transaction` to generate a new order .   It will be linked this token. | True  | `Dictionary`| ` var order = HashMap<String, Any>&#40;&#41;, order.put&#40;"id",""&#41; order.put&#40;"amount",1&#41;,order.put&#40;"currency","BHD"&#41;,order.put&#40;"description",""&#41;, order.put&#40;"reference":"A reference to this order in your system"&#41;&#41;` |)

[//]: # (| customer| The customer details you want to attach to this tokenization process. | True  | `Dictionary`| ` var customer =  HashMap<String,Any> ,customer.put&#40;"id,""&#41;, customer.put&#40;"nameOnCard","Tap Payments"&#41;,customer.put&#40;"editable",true&#41;,&#41; var name :HashMap<String,Any> = [["lang":"en","first":"TAP","middle":"","last":"PAYMENTS"]] "contact":["email":"tap@tap.company", "phone":["countryCode":"+965","number":"88888888"]]] customer.put&#40;"name",name&#41; , customer.put&#40;"contact",contact&#41;` |)

[//]: # ()
[//]: # (# Configuring the BenefitPay-Android SDK)

[//]: # (After creating the UI using any of the previously mentioned ways, it is time to pass the parameters needed for the SDK to work as expected and serve your need correctly.)

[//]: # (### 1- Creating the parameters)

[//]: # (To allow flexibility and to ease the integration, your application will only has to pass the parameters as a HashMap<String,Any> .)

[//]: # (First, let us create the required parameters:)

[//]: # ()
[//]: # (```kotlin)

[//]: # (     /**)

[//]: # (       * operator)

[//]: # (       */)

[//]: # (      val operator = HashMap<String,Any>&#40;&#41;)

[//]: # (        operator.put&#40;"publicKey","pk_test_YhUjg9PNT8oDlKJ1aE2fMRz7"&#41;)

[//]: # (        operator.put&#40;"hashString",""&#41;)

[//]: # ()
[//]: # (        /**)

[//]: # (         * phone)

[//]: # (         */)

[//]: # (        val phone = HashMap<String,Any>&#40;&#41;)

[//]: # (        phone.put&#40;"countryCode","+20"&#41;)

[//]: # (        phone.put&#40;"number","011"&#41;)

[//]: # ()
[//]: # (        /**)

[//]: # (         * contact)

[//]: # (         */)

[//]: # (        val contact = HashMap<String,Any>&#40;&#41;)

[//]: # (        contact.put&#40;"email","test@gmail.com"&#41;)

[//]: # (        contact.put&#40;"phone",phone&#41;)

[//]: # (        /**)

[//]: # (         * name)

[//]: # (         */)

[//]: # (        val name = HashMap<String,Any>&#40;&#41;)

[//]: # (        name.put&#40;"lang","en"&#41;)

[//]: # (        name.put&#40;"first","Tap"&#41;)

[//]: # (        name.put&#40;"middle",""&#41;)

[//]: # (        name.put&#40;"last","Payment"&#41;)

[//]: # ()
[//]: # (        /**)

[//]: # (         * customer)

[//]: # (         */)

[//]: # (        val customer = HashMap<String,Any>&#40;&#41;)

[//]: # (        customer.put&#40;"nameOnCard",""&#41;)

[//]: # (        customer.put&#40;"editable",true&#41;)

[//]: # (        customer.put&#40;"contact",contact&#41;)

[//]: # (        customer.put&#40;"name", listOf&#40;name&#41;&#41;)

[//]: # ()
[//]: # (        /**)

[//]: # (         * order)

[//]: # (         */)

[//]: # (        val order = HashMap<String,Any>&#40;&#41;)

[//]: # (        order.put&#40;"id","order_id"&#41;)

[//]: # (        order.put&#40;"amount","1"&#41;)

[//]: # (        order.put&#40;"currency","BHD"&#41;)

[//]: # (        order.put&#40;"description","description"&#41;)

[//]: # (        order.put&#40;"reference","refrence_id"&#41;)

[//]: # ()
[//]: # (        /**)

[//]: # (         * configuration request)

[//]: # (         */)

[//]: # ()
[//]: # (        val configuration = LinkedHashMap<String,Any>&#40;&#41;)

[//]: # (        configuration.put&#40;"operator", operator&#41;)

[//]: # (        configuration.put&#40;"order",order&#41;)

[//]: # (        configuration.put&#40;"customer",customer&#41;)

[//]: # ()
[//]: # (```)

[//]: # (### 2 - Pass these parameters to the created Button variable before as follows)

[//]: # ()
[//]: # (```kotlin)

[//]: # (     BeneiftPayConfiguration.configureWithTapBenfitPayDictionaryConfiguration&#40;)

[//]: # (            this, )

[//]: # (            findViewById&#40;R.id.benfit_pay&#41;,)

[//]: # (            configuration,)

[//]: # (            TapCardStatusDelegate&#41;)

[//]: # (```)

[//]: # ()
[//]: # (### Full code snippet for creating the parameters + passing it BenefitPayButton variable)

[//]: # (```kotlin)

[//]: # ()
[//]: # (override fun onCreate&#40;savedInstanceState: Bundle?&#41; {)

[//]: # (        super.onCreate&#40;savedInstanceState&#41;)

[//]: # (        setContentView&#40;R.layout.activity_main&#41;)

[//]: # (      /**)

[//]: # (       * operator)

[//]: # (       */)

[//]: # (      val operator = HashMap<String,Any>&#40;&#41;)

[//]: # (        operator.put&#40;"publicKey","pk_test_YhUjg9PNT8oDlKJ1aE2fMRz7"&#41;)

[//]: # (        operator.put&#40;"hashString",""&#41;)

[//]: # ()
[//]: # (        /**)

[//]: # (         * phone)

[//]: # (         */)

[//]: # (        val phone = HashMap<String,Any>&#40;&#41;)

[//]: # (        phone.put&#40;"countryCode","+20"&#41;)

[//]: # (        phone.put&#40;"number","011"&#41;)

[//]: # ()
[//]: # (        /**)

[//]: # (         * contact)

[//]: # (         */)

[//]: # (        val contact = HashMap<String,Any>&#40;&#41;)

[//]: # (        contact.put&#40;"email","test@gmail.com"&#41;)

[//]: # (        contact.put&#40;"phone",phone&#41;)

[//]: # (        /**)

[//]: # (         * name)

[//]: # (         */)

[//]: # (        val name = HashMap<String,Any>&#40;&#41;)

[//]: # (        name.put&#40;"lang","en"&#41;)

[//]: # (        name.put&#40;"first","Tap"&#41;)

[//]: # (        name.put&#40;"middle",""&#41;)

[//]: # (        name.put&#40;"last","Payment"&#41;)

[//]: # ()
[//]: # (        /**)

[//]: # (         * customer)

[//]: # (         */)

[//]: # (        val customer = HashMap<String,Any>&#40;&#41;)

[//]: # (        customer.put&#40;"nameOnCard",""&#41;)

[//]: # (        customer.put&#40;"editable",true&#41;)

[//]: # (        customer.put&#40;"contact",contact&#41;)

[//]: # (        customer.put&#40;"name", listOf&#40;name&#41;&#41;)

[//]: # ()
[//]: # (        /**)

[//]: # (         * order)

[//]: # (         */)

[//]: # (        val order = HashMap<String,Any>&#40;&#41;)

[//]: # (        order.put&#40;"id","order_id"&#41;)

[//]: # (        order.put&#40;"amount","1"&#41;)

[//]: # (        order.put&#40;"currency","BHD"&#41;)

[//]: # (        order.put&#40;"description","description"&#41;)

[//]: # (        order.put&#40;"reference","refrence_id"&#41;)

[//]: # ()
[//]: # (        /**)

[//]: # (         * configuration request)

[//]: # (         */)

[//]: # ()
[//]: # (        val configuration = LinkedHashMap<String,Any>&#40;&#41;)

[//]: # (        configuration.put&#40;"operator", operator&#41;)

[//]: # (        configuration.put&#40;"order",order&#41;)

[//]: # (        configuration.put&#40;"customer",customer&#41;)

[//]: # ()
[//]: # (     BeneiftPayConfiguration.configureWithTapBenfitPayDictionaryConfiguration&#40;)

[//]: # (            this, )

[//]: # (            findViewById&#40;R.id.benfit_pay&#41;,)

[//]: # (            configuration,)

[//]: # (            object : TapBenefitPayStatusDelegate {)

[//]: # (                override fun onSuccess&#40;data: String&#41; {)

[//]: # (                    Log.i&#40;"data",data.toString&#40;&#41;&#41;)

[//]: # (                })

[//]: # (                override fun onReady&#40;&#41; {)

[//]: # (                    Log.i&#40;"data","onReady"&#41;)

[//]: # ()
[//]: # (                })

[//]: # ()
[//]: # ()
[//]: # (                override fun onError&#40;error: String&#41; {)

[//]: # (                    Log.i&#40;"data","onError"&#41;)

[//]: # ()
[//]: # ()
[//]: # (                })

[//]: # (                )
[//]: # (            }&#41;)

[//]: # (})

[//]: # (```)

[//]: # (# Receiving Callback Notifications)

[//]: # (Now we have created the UI and the parameters required to to correctly display BenefitPayButton form. For the best experience, your class will have to implement TapBenefitPayStatusDelegate interface, which is a set of optional callbacks, that will be fired based on different events from within the benefit button. This will help you in deciding the logic you need to do upon receiving each event. Kindly follow the below steps in order to complete the mentioned flow:)

[//]: # (- Go back to Activity file you want to get the callbacks into.)

[//]: # (- Head to the class declaration line)

[//]: # (- Add TapBenefitPayStatusDelegate)

[//]: # (- Override the required callbacks as follows:)

[//]: # (```kotlin)

[//]: # ( object : TapBenefitPayStatusDelegate {)

[//]: # (                override fun onSuccess&#40;data: String&#41; {)

[//]: # (                    Log.i&#40;"data",data.toString&#40;&#41;&#41;)

[//]: # (                })

[//]: # (                override fun onReady&#40;&#41; {)

[//]: # (                    Log.i&#40;"data","onReady"&#41;)

[//]: # ()
[//]: # (                })

[//]: # ()
[//]: # ()
[//]: # (                override fun onError&#40;error: String&#41; {)

[//]: # (                    Log.i&#40;"data","onError"&#41;)

[//]: # ()
[//]: # ()
[//]: # (                })

[//]: # (                )
[//]: # (            })

[//]: # (```)

[//]: # (## Simple TapCardStatusDelegate)

[//]: # (A protocol that allows integrators to get notified from events fired from the `BenefitPay-Android`. )

[//]: # ()
[//]: # (```kotlin)

[//]: # (    interface TapBenefitPayStatusDelegate {)

[//]: # (    )
[//]: # (     ///   Will be fired whenever the card sdk finishes successfully the task assigned to it. Whether `TapToken` or `AuthenticatedToken`)

[//]: # (    override fun  onSuccess&#40;data: String&#41; {)

[//]: # (     })

[//]: # (    /// Will be fired whenever there is an error related to the card connectivity or apis)

[//]: # (    /// - Parameter data: includes a JSON format for the error description and error)

[//]: # (    override fun onError&#40;data: String&#41;{)

[//]: # (    })

[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # (# Advanced Integration)

[//]: # (The advanced configuration for the BenefitPay-Android integration not only has all the features available in the simple integration but also introduces new capabilities, providing merchants with maximum flexibility. You can find a code below, where you'll discover comprehensive guidance on implementing the advanced flow as well as a complete description of each parameter.)

[//]: # ()
[//]: # (# Parameters)

[//]: # (Each parameter is linked to the reference section, which provides a more in depth explanation of it.)

[//]: # ()
[//]: # (|Parameters |Description | Required | Type| Sample)

[//]: # (|--|--|--| --|--|)

[//]: # (| operator| This is the `Key` that you will get after registering you package name. | True  | String| `var operator=HashMap<String,Any>&#40;&#41;,operator.put&#40;"publicKey","pk_test_YhUjg9PNT8oDlKJ1aE2fMRz7"&#41;,operator.put&#40;"hashString",""&#41;` |)

[//]: # (| order| This is the `order id` that you created before or `amount` , `currency` , `transaction` to generate a new order .   It will be linked this token. | True  | `Dictionary`| ` var order = HashMap<String, Any>&#40;&#41;, order.put&#40;"id",""&#41; order.put&#40;"amount",1&#41;,order.put&#40;"currency","BHD"&#41;,order.put&#40;"description",""&#41;, order.put&#40;"reference":"A reference to this order in your system"&#41;&#41;` |)

[//]: # (| invoice| This is the `invoice id` that you want to link this token to if any. | False  | `Dictionary`| ` var invoice = HashMap<String,Any>.put&#40;"id",""&#41;` |)

[//]: # (| merchant| This is the `Merchant id` that you will get after registering you bundle id. | True  | `Dictionary`| ` var merchant = HashMap<String,Any>.put&#40;"id",""&#41;` |)

[//]: # (| customer| The customer details you want to attach to this tokenization process. | True  | `Dictionary`| ` var customer =  HashMap<String,Any> ,customer.put&#40;"id,""&#41;, customer.put&#40;"nameOnCard","Tap Payments"&#41;,customer.put&#40;"editable",true&#41;,&#41; var name :HashMap<String,Any> = [["lang":"en","first":"TAP","middle":"","last":"PAYMENTS"]] "contact":["email":"tap@tap.company", "phone":["countryCode":"+965","number":"88888888"]]] customer.put&#40;"name",name&#41; , customer.put&#40;"contact",contact&#41;` |)

[//]: # (| interface| Needed to defines look and feel related configurations. | False  | `Dictionary`| ` var interface = HashMap<String,Any> ,interface.put&#40;"locale","en"&#41;, interface.put&#40;"theme","light"&#41;, interface.put&#40;"edges","curved"&#41;,interface.put&#40;"colorStyle","colored"&#41;,interface.put&#40;"loader",true&#41; // Allowed values for theme : light/dark. locale: en/ar, edges: curved/flat, direction:ltr/dynaimc,colorStyle:colored/monochrome` |)

[//]: # (| post| This is the `webhook` for your server, if you want us to update you server to server. | False  | `Dictionary`| ` var post = HashMap<String,Any>.put&#40;"url",""&#41;` |)

[//]: # ()
[//]: # (# Initialisation of the input)

[//]: # (You can use a Hashmap to send data to our SDK. The benefit is that you can generate this data from one of your APIs. If we make updates to the configurations, you can update your API, avoiding the need to update your app on the Google play  Store.)

[//]: # ()
[//]: # ()
[//]: # (```kotlin)

[//]: # (     /**)

[//]: # (       * operator)

[//]: # (       */)

[//]: # (      val operator = HashMap<String,Any>&#40;&#41;)

[//]: # (        operator.put&#40;"publicKey","pk_test_YhUjg9PNT8oDlKJ1aE2fMRz7"&#41;)

[//]: # (        operator.put&#40;"hashString",""&#41;)

[//]: # ()
[//]: # (        /**)

[//]: # (         * phone)

[//]: # (         */)

[//]: # (        val phone = HashMap<String,Any>&#40;&#41;)

[//]: # (        phone.put&#40;"countryCode","+20"&#41;)

[//]: # (        phone.put&#40;"number","011"&#41;)

[//]: # ()
[//]: # (        /**)

[//]: # (         * contact)

[//]: # (         */)

[//]: # (        val contact = HashMap<String,Any>&#40;&#41;)

[//]: # (        contact.put&#40;"email","test@gmail.com"&#41;)

[//]: # (        contact.put&#40;"phone",phone&#41;)

[//]: # (        /**)

[//]: # (         * name)

[//]: # (         */)

[//]: # (        val name = HashMap<String,Any>&#40;&#41;)

[//]: # (        name.put&#40;"lang","en"&#41;)

[//]: # (        name.put&#40;"first","Tap"&#41;)

[//]: # (        name.put&#40;"middle",""&#41;)

[//]: # (        name.put&#40;"last","Payment"&#41;)

[//]: # ()
[//]: # (        /**)

[//]: # (         * customer)

[//]: # (         */)

[//]: # (        val customer = HashMap<String,Any>&#40;&#41;)

[//]: # (        customer.put&#40;"nameOnCard",""&#41;)

[//]: # (        customer.put&#40;"editable",true&#41;)

[//]: # (        customer.put&#40;"contact",contact&#41;)

[//]: # (        customer.put&#40;"name", listOf&#40;name&#41;&#41;)

[//]: # ()
[//]: # (        /**)

[//]: # (         * order)

[//]: # (         */)

[//]: # (        val order = HashMap<String,Any>&#40;&#41;)

[//]: # (        order.put&#40;"id","order_id"&#41;)

[//]: # (        order.put&#40;"amount","1"&#41;)

[//]: # (        order.put&#40;"currency","BHD"&#41;)

[//]: # (        order.put&#40;"description","description"&#41;)

[//]: # (        order.put&#40;"reference","refrence_id"&#41;)

[//]: # ()
[//]: # (        /**)

[//]: # (         * merchant)

[//]: # (         */)

[//]: # (        val merchant = HashMap<String,Any>&#40;&#41;)

[//]: # (        merchant.put&#40;"id", ""&#41;)

[//]: # ()
[//]: # (        /**)

[//]: # (         * invoice)

[//]: # (         */)

[//]: # (        val invoice = HashMap<String,Any>&#40;&#41;)

[//]: # (        invoice.put&#40;"id",""&#41;)

[//]: # ()
[//]: # (     /** interface **/)

[//]: # ()
[//]: # (      val interfacee = HashMap<String,Any>&#40;&#41;)

[//]: # (        interfacee.put&#40;"locale",selectedLanguage ?: "en"&#41;)

[//]: # (        interfacee.put&#40;"theme",selectedTheme ?: "light"&#41;)

[//]: # (        interfacee.put&#40;"edges",selectedCardEdge ?: "curved"&#41;)

[//]: # (        interfacee.put&#40;"colorStyle",selectedColorStylee ?:"colored"&#41;)

[//]: # (        interfacee.put&#40;"loader",loader&#41;)

[//]: # ()
[//]: # (     /** post  **/)

[//]: # ()
[//]: # (        val post = HashMap<String,Any>&#40;&#41;)

[//]: # (        post.put&#40;"url",""&#41;)

[//]: # (        /**)

[//]: # (         * configuration request)

[//]: # (         */)

[//]: # ()
[//]: # (       )
[//]: # ()
[//]: # (        val configuration = LinkedHashMap<String,Any>&#40;&#41;)

[//]: # (        configuration.put&#40;"operator", operator&#41;)

[//]: # (        configuration.put&#40;"order",order&#41;)

[//]: # (        configuration.put&#40;"customer",customer&#41;)

[//]: # (        configuration.put&#40;"merchant",merchant&#41;)

[//]: # (        configuration.put&#40;"invoice",invoice&#41;)

[//]: # (        configuration.put&#40;"interface",interfacee&#41;)

[//]: # (        configuration.put&#40;"post",post&#41;)

[//]: # ()
[//]: # (```)

[//]: # (# Receiving Callback Notifications &#40;Advanced Version&#41;)

[//]: # (The below will allow the integrators to get notified from events fired from the BenefitPayButton.)

[//]: # ()
[//]: # (```kotlin)

[//]: # (    override fun onReady&#40;&#41; {)

[//]: # (           print&#40;"\n\n========\n\nonReady"&#41;)

[//]: # (    })

[//]: # ()
[//]: # (    override fun onClick&#40;&#41; {)

[//]: # (         print&#40;"\n\n========\n\noClick"&#41;)

[//]: # (    })

[//]: # ()
[//]: # (    override fun onChargeCreated&#40;data: String&#41; {)

[//]: # (           print&#40;"\n\n========\n\nonChargeCreated"&#41;)

[//]: # (    })

[//]: # ()
[//]: # (    override fun onOrderCreated&#40;data: String&#41; {)

[//]: # (           print&#40;"\n\n========\n\nonOrderCreated"&#41;)

[//]: # ()
[//]: # (    })

[//]: # ()
[//]: # (    override fun onCancel&#40;&#41; {)

[//]: # (           print&#40;"\n\n========\n\nonCancel"&#41;)

[//]: # (    })

[//]: # ()
[//]: # (    override fun onError&#40;error: String&#41; {)

[//]: # (           print&#40;"\n\n========\n\nonError"&#41;)

[//]: # (    })

[//]: # (```)

[//]: # (# Full Code Sample)

[//]: # (Once all of the above steps are successfully completed, your Activity file should look like this:)

[//]: # (```kotlin)

[//]: # ()
[//]: # (class MainActivity : AppCompatActivity&#40;&#41; ,TapBenefitPayStatusDelegate{)

[//]: # (    lateinit var tapBenefitPay: TapBenefitPay)

[//]: # (    override fun onCreate&#40;savedInstanceState: Bundle?&#41; {)

[//]: # (        super.onCreate&#40;savedInstanceState&#41;)

[//]: # (        setContentView&#40;R.layout.activity_main&#41;)

[//]: # (        configureSdk&#40;&#41;)

[//]: # (    })

[//]: # ()
[//]: # (    fun configureSdk&#40;&#41;{)

[//]: # ()
[//]: # ()
[//]: # (        /**)

[//]: # (         * operator)

[//]: # (         */)

[//]: # (        val operator = HashMap<String,Any>&#40;&#41;)

[//]: # (        operator.put&#40;"publicKey",publicKey.toString&#40;&#41;&#41;)

[//]: # (        operator.put&#40;"hashString",hashStringKey.toString&#40;&#41;&#41;)

[//]: # ()
[//]: # (        /**)

[//]: # (         * order)

[//]: # (         */)

[//]: # (        val order = HashMap<String,Any>&#40;&#41;)

[//]: # (        order.put&#40;"id",ordrId ?: ""&#41;)

[//]: # (        order.put&#40;"amount",  if &#40;orderAmount?.isEmpty&#40;&#41; == true&#41;"1" else orderAmount.toString&#40;&#41; &#41;)

[//]: # (        order.put&#40;"currency",selectedCurrency&#41;)

[//]: # (        order.put&#40;"description",orderDescription ?: ""&#41;)

[//]: # (        order.put&#40;"reference",orderRefrence ?: ""&#41;)

[//]: # ()
[//]: # (        /**)

[//]: # (         * merchant)

[//]: # (         */)

[//]: # (        val merchant = HashMap<String,Any>&#40;&#41;)

[//]: # (        merchant.put&#40;"id", ""&#41;)

[//]: # ()
[//]: # (        /**)

[//]: # (         * invoice)

[//]: # (         */)

[//]: # (        val invoice = HashMap<String,Any>&#40;&#41;)

[//]: # (        invoice.put&#40;"id",""&#41;)

[//]: # ()
[//]: # ()
[//]: # (        /**)

[//]: # (         * phone)

[//]: # (         */)

[//]: # (        val phone = java.util.HashMap<String,Any>&#40;&#41;)

[//]: # (        phone.put&#40;"countryCode","+20"&#41;)

[//]: # (        phone.put&#40;"number","011"&#41;)

[//]: # ()
[//]: # ()
[//]: # (        /**)

[//]: # (         * contact)

[//]: # (         */)

[//]: # (        val contact = java.util.HashMap<String,Any>&#40;&#41;)

[//]: # (        contact.put&#40;"email","test@gmail.com"&#41;)

[//]: # (        contact.put&#40;"phone",phone&#41;)

[//]: # (        /**)

[//]: # (         * name)

[//]: # (         */)

[//]: # (        val name = java.util.HashMap<String,Any>&#40;&#41;)

[//]: # (        name.put&#40;"lang","en"&#41;)

[//]: # (        name.put&#40;"first", "first"&#41;)

[//]: # (        name.put&#40;"middle", "middle"&#41;)

[//]: # (        name.put&#40;"last","last"&#41;)

[//]: # ()
[//]: # (        /**)

[//]: # (         * customer)

[//]: # (         */)

[//]: # (        val customer = java.util.HashMap<String,Any>&#40;&#41;)

[//]: # (        customer.put&#40;"id", ""&#41;)

[//]: # (        customer.put&#40;"contact",contact&#41;)

[//]: # (        customer.put&#40;"name", listOf&#40;name&#41;&#41;)

[//]: # ()
[//]: # (        /**)

[//]: # (         * interface)

[//]: # (         */)

[//]: # (    )
[//]: # (        val interfacee = HashMap<String,Any>&#40;&#41;)

[//]: # (        interfacee.put&#40;"locale",selectedLanguage ?: "en"&#41;)

[//]: # (        interfacee.put&#40;"theme",selectedTheme ?: "light"&#41;)

[//]: # (        interfacee.put&#40;"edges",selectedCardEdge ?: "curved"&#41;)

[//]: # (        interfacee.put&#40;"colorStyle",selectedColorStylee ?:"colored"&#41;)

[//]: # (        interfacee.put&#40;"loader",loader&#41;)

[//]: # ()
[//]: # ()
[//]: # (        val post = HashMap<String,Any>&#40;&#41;)

[//]: # (        post.put&#40;"url",""&#41;)

[//]: # (        val configuration = LinkedHashMap<String,Any>&#40;&#41;)

[//]: # ()
[//]: # (        configuration.put&#40;"operator",operator&#41;)

[//]: # (        configuration.put&#40;"order",order&#41;)

[//]: # (        configuration.put&#40;"customer",customer&#41;)

[//]: # ()
[//]: # (        configuration.put&#40;"merchant",merchant&#41;)

[//]: # (        configuration.put&#40;"invoice",invoice&#41;)

[//]: # (        configuration.put&#40;"interface",interfacee&#41;)

[//]: # (        configuration.put&#40;"post",post&#41;)

[//]: # ()
[//]: # ()
[//]: # ()
[//]: # (        BeneiftPayConfiguration.configureWithTapBenfitPayDictionaryConfiguration&#40;)

[//]: # (            this,)

[//]: # (            findViewById&#40;R.id.benfit_pay&#41;,)

[//]: # (            configuration,)

[//]: # (           this&#41;)

[//]: # ()
[//]: # ()
[//]: # (    })

[//]: # ()
[//]: # (    )
[//]: # ()
[//]: # (    override fun onSuccess&#40;data: String&#41; {)

[//]: # (    })

[//]: # ()
[//]: # (    override fun onReady&#40;&#41; {)

[//]: # (        super.onReady&#40;&#41;)

[//]: # (    })

[//]: # ()
[//]: # (    override fun onClick&#40;&#41; {)

[//]: # (        super.onClick&#40;&#41;)

[//]: # (    })

[//]: # ()
[//]: # (    override fun onChargeCreated&#40;data: String&#41; {)

[//]: # (        super.onChargeCreated&#40;data&#41;)

[//]: # (    })

[//]: # ()
[//]: # (    override fun onOrderCreated&#40;data: String&#41; {)

[//]: # (        super.onOrderCreated&#40;data&#41;)

[//]: # (    })

[//]: # ()
[//]: # (    override fun onCancel&#40;&#41; {)

[//]: # (        super.onCancel&#40;&#41;)

[//]: # (    })

[//]: # ()
[//]: # (    override fun onError&#40;error: String&#41; {)

[//]: # (    })

[//]: # ()
[//]: # (})

[//]: # (```)

[//]: # ()
[//]: # ()
[//]: # (## Parameters Reference)

[//]: # (Below you will find more details about each parameter shared in the above tables that will help you easily integrate BenefitPay-Android SDK.)

[//]: # ()
[//]: # (### Documentation per variable)

[//]: # ()
[//]: # ( # operator:)

[//]: # (  - Definition: It links the payment gateway to your merchant account with Tap, in order to know your business name, logo, etc...)

[//]: # (  - Type: string &#40;required&#41;)

[//]: # (  - Fields:)

[//]: # (     - publicKey :)

[//]: # (        - Definition: This is a unique public key that you will receive after creating an account with Tap which is considered a reference to identify you as a merchant. You will receive 2 public keys, one for )

[//]: # (            sandbox/testing and another one for production.)

[//]: # (  - Example: )

[//]: # (      ```kotlin)

[//]: # (       val operator = HashMap<String,Any>&#40;&#41;)

[//]: # (        operator.put&#40;"publicKey",publicKey.toString&#40;&#41;&#41;)

[//]: # (        operator.put&#40;"hashString",hashStringKey.toString&#40;&#41;&#41;)

[//]: # (      ```)

[//]: # ()
[//]: # (## order:)

[//]: # (  - Definition:This defined the details of the order that you are trying to purchase, in which you need to specify some details like the id, amount, currency ...)

[//]: # (  - Type: Dictionary, &#40;required&#41;)

[//]: # (  - Fields:)

[//]: # (     - id :)

[//]: # (        - Definition: Pass the order ID created for the order you are trying to purchase, which will be available in your database.)

[//]: # (         Note: This field can be empty)

[//]: # (     - currency :)

[//]: # (        - Definition: The currency which is linked to the order being paid.)

[//]: # ()
[//]: # (     - amount :)

[//]: # (       -  Definition: The order amount to be paid by the customer.)

[//]: # (         Note: Minimum amount to be added is 0.1.   )

[//]: # (     - description :)

[//]: # (        - Definition: Order details, which defines what the customer is paying for or the description of the service you are providing.)

[//]: # (          )
[//]: # (     - reference :)

[//]: # (        - Definition: This will be the order reference present in your database in which the paying is being done for.)

[//]: # (     )
[//]: # (     - Example: )

[//]: # (      ```kotlin)

[//]: # (      val order = HashMap<String,Any>&#40;&#41;)

[//]: # (      order.put&#40;"id",ordrId ?: ""&#41;)

[//]: # (      order.put&#40;"amount",  if &#40;orderAmount?.isEmpty&#40;&#41; == true&#41;"1" else orderAmount.toString&#40;&#41; &#41;)

[//]: # (      order.put&#40;"currency",selectedCurrency&#41;)

[//]: # (      order.put&#40;"description",orderDescription ?: ""&#41;)

[//]: # (      order.put&#40;"reference",orderRefrence ?: ""&#41;)

[//]: # (      ```)

[//]: # ()
[//]: # ()
[//]: # (## merchant:)

[//]: # (  - Definition:It is the Merchant id that you get from our onboarding team. This will be used as reference for your account in Tap.)

[//]: # (  - Type: Dictionary, &#40;required&#41;)

[//]: # (  - Fields:)

[//]: # (     - id :)

[//]: # (        - Definition: Generated once your account with Tap is created, which is unique for every merchant. )

[//]: # (     - Example: )

[//]: # (      ```kotlin)

[//]: # (        val merchant = HashMap<String,Any>&#40;&#41;)

[//]: # (        merchant.put&#40;"id", ""&#41;)

[//]: # (      ```)

[//]: # ()
[//]: # ()
[//]: # (## invoice:)

[//]: # (  - Definition:After the token is generated, you can use it to pay for any invoice. Each invoice will have an invoice ID which you can add here using the SDK.)

[//]: # (Note: An invoice will first show you a receipt/summary of the order you are going to pay for as well as the amount, currency, and any related field before actually opening the payment form and completing the payment.)

[//]: # (  - Type: Dictionary, &#40;optional&#41;)

[//]: # (  - Fields:)

[//]: # (     - id :)

[//]: # (        - Definition: Unique Invoice ID which we are trying to pay.)

[//]: # (     )
[//]: # (     - Example: )

[//]: # (      ```kotlin)

[//]: # (        val invoice = HashMap<String,Any>&#40;&#41;)

[//]: # (        invoice.put&#40;"id",""&#41;)

[//]: # ()
[//]: # (      ```)

[//]: # ()
[//]: # (## post:)

[//]: # (  - Definition:Here you can pass the webhook URL you have, in order to receive notifications of the results of each Transaction happening on your application.)

[//]: # (  - Type: Dictionary, &#40;optional&#41;)

[//]: # (  - Fields:)

[//]: # (     - url :)

[//]: # (        - Definition:The webhook server's URL that you want to receive notifications on.)

[//]: # (     )
[//]: # (     - Example: )

[//]: # (      ```kotlin)

[//]: # (         val post = HashMap<String,Any>&#40;&#41;)

[//]: # (        post.put&#40;"url",""&#41;)

[//]: # ()
[//]: # (      ```)

[//]: # ()
[//]: # (## customer:)

[//]: # (  - Definition: Here, you will collect the information of the customer that is paying..)

[//]: # (  - Type: Dictionary, &#40;required&#41;)

[//]: # (  - Fields:)

[//]: # (     - id :)

[//]: # (        - Definition: This is an optional field that you do not have before the charge is processed. But, after the charge, then you will receive the customer ID in the response which can be handled in the onSuccess callback function.)

[//]: # (      )
[//]: # (    - name :)

[//]: # (        - Definition: Full Name of the customer paying.)

[//]: # (            - Fields:)

[//]: # (               - lang : Language chosen to write the customer name.)

[//]: # (               - first : Customer's first name.)

[//]: # (               - middle :Customer's middle name.)

[//]: # (               - last : Customer's last name.)

[//]: # (  )
[//]: # (    - contact :)

[//]: # (        - Definition: The customer's contact information like email address and phone number.)

[//]: # (Note: The contact information has to either have the email address or the phone details of the customers or both but it should not be empty.)

[//]: # ()
[//]: # (      - Fields:)

[//]: # (         - email :)

[//]: # (            - Customer's email address)

[//]: # (             Note: The email is of type string.)

[//]: # (         - phone :)

[//]: # (            -  Customer's Phone number details with country code and number )

[//]: # (           )
[//]: # ()
[//]: # (     - Example: )

[//]: # (      ```kotlin)

[//]: # (         /**)

[//]: # (         * phone)

[//]: # (         */)

[//]: # (        val phone = java.util.HashMap<String,Any>&#40;&#41;)

[//]: # (        phone.put&#40;"countryCode","+20"&#41;)

[//]: # (        phone.put&#40;"number","011"&#41;)

[//]: # ()
[//]: # ()
[//]: # (        /**)

[//]: # (         * contact)

[//]: # (         */)

[//]: # (        val contact = java.util.HashMap<String,Any>&#40;&#41;)

[//]: # (        contact.put&#40;"email","test@gmail.com"&#41;)

[//]: # (        contact.put&#40;"phone",phone&#41;)

[//]: # (        /**)

[//]: # (         * name)

[//]: # (         */)

[//]: # (        val name = java.util.HashMap<String,Any>&#40;&#41;)

[//]: # (        name.put&#40;"lang","en"&#41;)

[//]: # (        name.put&#40;"first", "first"&#41;)

[//]: # (        name.put&#40;"middle", "middle"&#41;)

[//]: # (        name.put&#40;"last","last"&#41;)

[//]: # ()
[//]: # (        /**)

[//]: # (         * customer)

[//]: # (         */)

[//]: # (        val customer = java.util.HashMap<String,Any>&#40;&#41;)

[//]: # (        customer.put&#40;"id", ""&#41;)

[//]: # (        customer.put&#40;"contact",contact&#41;)

[//]: # (        customer.put&#40;"name", listOf&#40;name&#41;&#41;)

[//]: # ()
[//]: # (      ```)

[//]: # ()
[//]: # ()
[//]: # (## interface:)

[//]: # (  - Definition:This will help you control the layout &#40;UI&#41; of the payment form, like changing the theme light to dark, the language used &#40;en or ar&#41;, ...)

[//]: # (  - Type: Dictionary, &#40;required&#41;)

[//]: # (  - Fields:)

[//]: # (     - loader :)

[//]: # (        - Definition: A boolean to indicate wether or not you want to show a loading view on top of the benefit button while it is performing api requests.)

[//]: # (     - locale :)

[//]: # (        - Definition: The language of the benefit button. Accepted values as of now are:)

[//]: # (Possible Values:)

[//]: # ()
[//]: # (     - theme :)

[//]: # (       -  Definition: The display styling of the benefit button. Accepted values as of now are: light /dark /dynamic)

[//]: # (         Note: Minimum amount to be added is 0.1.   )

[//]: # (     - edges :)

[//]: # (        - Definition: Control the edges of the payment form.  &#40;flat/curved&#41;)

[//]: # (          )
[//]: # (     - colorStyle :)

[//]: # (        - Definition: How do you want the icons rendered inside the benefit button Possible Values: colored / monochrome)

[//]: # (     )
[//]: # (     - Example: )

[//]: # (      ```kotlin)

[//]: # (       val interfacee = HashMap<String,Any>&#40;&#41;)

[//]: # (        interfacee.put&#40;"locale",selectedLanguage ?: "en"&#41;)

[//]: # (        interfacee.put&#40;"theme",selectedTheme ?: "light"&#41;)

[//]: # (        interfacee.put&#40;"edges",selectedCardEdge ?: "curved"&#41;)

[//]: # (        interfacee.put&#40;"colorStyle",selectedColorStylee ?:"colored"&#41;)

[//]: # (        interfacee.put&#40;"loader",loader&#41;)

[//]: # (      ```      )

[//]: # (# Expected Callbacks Responses)

[//]: # ()
[//]: # (## onError)

[//]: # (``` kotlin )

[//]: # ({)

[//]: # (    "error":"")

[//]: # (})

[//]: # (``` )

[//]: # (## onOrderCreated)

[//]: # (``` kotlin )

[//]: # ({)

[//]: # (    "ord_uAx145231127yHYS19Ou9B126")

[//]: # ()
[//]: # (})

[//]: # (``` )

[//]: # (## onChargeCreated)

[//]: # (``` kotlin )

[//]: # ({)

[//]: # (    {)

[//]: # (    "id": "chg_TS07A5220231433Ql241910314",)

[//]: # (    "object": "charge",)

[//]: # (    "live_mode": false,)

[//]: # (    "customer_initiated": true,)

[//]: # (    "api_version": "V2",)

[//]: # (    "method": "CREATE",)

[//]: # (    "status": "INITIATED",)

[//]: # (    "amount": 0.1,)

[//]: # (    "currency": "BHD",)

[//]: # (    "threeDSecure": true,)

[//]: # (    "card_threeDSecure": false,)

[//]: # (    "save_card": false,)

[//]: # (    "order_id": "ord_uAx145231127yHYS19Ou9B126",)

[//]: # (    "product": "GOSELL",)

[//]: # (    "order": {)

[//]: # (        "id": "ord_uAx145231127yHYS19Ou9B126")

[//]: # (    },)

[//]: # (    "transaction": {)

[//]: # (        "timezone": "UTC+03:00",)

[//]: # (        "created": "1697726033236",)

[//]: # (        "url": "",)

[//]: # (        "expiry": {)

[//]: # (            "period": 30,)

[//]: # (            "type": "MINUTE")

[//]: # (        },)

[//]: # (        "asynchronous": false,)

[//]: # (        "amount": 0.1,)

[//]: # (        "currency": "BHD")

[//]: # (    },)

[//]: # (    "response": {)

[//]: # (        "code": "100",)

[//]: # (        "message": "Initiated")

[//]: # (    },)

[//]: # (    "receipt": {)

[//]: # (        "email": true,)

[//]: # (        "sms": true)

[//]: # (    },)

[//]: # (    "customer": {)

[//]: # (        "first_name": "TAP",)

[//]: # (        "last_name": "PAYMENTS",)

[//]: # (        "email": "tap@tap.company",)

[//]: # (        "phone": {)

[//]: # (            "country_code": " 965",)

[//]: # (            "number": "88888888")

[//]: # (        })

[//]: # (    },)

[//]: # (    "merchant": {)

[//]: # (        "country": "KW",)

[//]: # (        "currency": "KWD",)

[//]: # (        "id": "599424")

[//]: # (    },)

[//]: # (    "source": {)

[//]: # (        "object": "source",)

[//]: # (        "id": "src_benefit_pay")

[//]: # (    },)

[//]: # (    "redirect": {)

[//]: # (        "status": "PENDING",)

[//]: # (        "url": "")

[//]: # (    },)

[//]: # (    "post": {)

[//]: # (        "status": "PENDING",)

[//]: # (        "url": "")

[//]: # (    },)

[//]: # (    "activities": [)

[//]: # (        {)

[//]: # (            "id": "activity_TS02A5420231433Mx4g1910470",)

[//]: # (            "object": "activity",)

[//]: # (            "created": 1697726033236,)

[//]: # (            "status": "INITIATED",)

[//]: # (            "currency": "BHD",)

[//]: # (            "amount": 0.1,)

[//]: # (            "remarks": "charge - created")

[//]: # (        })

[//]: # (    ],)

[//]: # (    "auto_reversed": false,)

[//]: # (    "gateway_response": {)

[//]: # (        "name": "BENEFITPAY",)

[//]: # (        "request": {)

[//]: # (            "amount": "0.100",)

[//]: # (            "currency": "BHD",)

[//]: # (            "hash": "gMjpC12Ffz+CMhyvm+/jdYJmqbPdgAhHJvvGBABYhCI=",)

[//]: # (            "reference": {)

[//]: # (                "transaction": "chg_TS07A5220231433Ql241910314")

[//]: # (            },)

[//]: # (            "merchant": {)

[//]: # (                "id": "00000101")

[//]: # (            },)

[//]: # (            "application": {)

[//]: # (                "id": "4530082749")

[//]: # (            },)

[//]: # (            "configuration": {)

[//]: # (                "show_result": "0",)

[//]: # (                "hide_mobile_qr": "0",)

[//]: # (                "frequency": {)

[//]: # (                    "start": 120,)

[//]: # (                    "interval": 60,)

[//]: # (                    "count": 10,)

[//]: # (                    "type": "SECOND")

[//]: # (                })

[//]: # (            })

[//]: # (        })

[//]: # (    })

[//]: # (})

[//]: # ()
[//]: # (})

[//]: # (``` )

[//]: # (## onSuccess)

[//]: # (``` kotlin )

[//]: # ({)

[//]: # (   {)

[//]: # (    "id": "chg_TS07A5220231433Ql241910314",)

[//]: # (    "object": "charge",)

[//]: # (    "live_mode": false,)

[//]: # (    "customer_initiated": true,)

[//]: # (    "api_version": "V2",)

[//]: # (    "method": "UPDATE",)

[//]: # (    "status": "INITIATED",)

[//]: # (    "amount": 0.1,)

[//]: # (    "currency": "BHD",)

[//]: # (    "threeDSecure": true,)

[//]: # (    "card_threeDSecure": false,)

[//]: # (    "save_card": false,)

[//]: # (    "order_id": "ord_uAx145231127yHYS19Ou9B126",)

[//]: # (    "product": "GOSELL",)

[//]: # (    "description": "",)

[//]: # (    "order": {)

[//]: # (        "id": "ord_uAx145231127yHYS19Ou9B126")

[//]: # (    },)

[//]: # (    "transaction": {)

[//]: # (        "timezone": "UTC+03:00",)

[//]: # (        "created": "1697726033236",)

[//]: # (        "url": "https://sandbox.payments.tap.company/test_gosell/v2/payment/tap_process.aspx?chg=8D9e9fdEo5N03hWrGnROvEEFw4DfqYVFv8R7R34GITc%3d",)

[//]: # (        "expiry": {)

[//]: # (            "period": 30,)

[//]: # (            "type": "MINUTE")

[//]: # (        },)

[//]: # (        "asynchronous": false,)

[//]: # (        "amount": 0.1,)

[//]: # (        "currency": "BHD")

[//]: # (    },)

[//]: # (    "response": {)

[//]: # (        "code": "100",)

[//]: # (        "message": "Initiated")

[//]: # (    },)

[//]: # (    "receipt": {)

[//]: # (        "email": true,)

[//]: # (        "sms": true)

[//]: # (    },)

[//]: # (    "customer": {)

[//]: # (        "first_name": "TAP",)

[//]: # (        "last_name": "PAYMENTS",)

[//]: # (        "email": "tap@tap.company",)

[//]: # (        "phone": {)

[//]: # (            "country_code": " 965",)

[//]: # (            "number": "88888888")

[//]: # (        })

[//]: # (    },)

[//]: # (    "merchant": {)

[//]: # (        "country": "KW",)

[//]: # (        "currency": "KWD",)

[//]: # (        "id": "599424")

[//]: # (    },)

[//]: # (    "source": {)

[//]: # (        "object": "source",)

[//]: # (        "id": "src_benefit_pay")

[//]: # (    },)

[//]: # (    "redirect": {)

[//]: # (        "status": "PENDING",)

[//]: # (        "url": "")

[//]: # (    },)

[//]: # (    "post": {)

[//]: # (        "status": "PENDING",)

[//]: # (        "url": "")

[//]: # (    },)

[//]: # (    "activities": [)

[//]: # (        {)

[//]: # (            "id": "activity_TS02A5420231433Mx4g1910470",)

[//]: # (            "object": "activity",)

[//]: # (            "created": 1697726033236,)

[//]: # (            "status": "INITIATED",)

[//]: # (            "currency": "BHD",)

[//]: # (            "amount": 0.1,)

[//]: # (            "remarks": "charge - created")

[//]: # (        })

[//]: # (    ],)

[//]: # (    "auto_reversed": false,)

[//]: # (    "gateway_response": {)

[//]: # (        "name": "BENEFITPAY",)

[//]: # (        "request": {)

[//]: # (            "amount": "0.100",)

[//]: # (            "currency": "BHD",)

[//]: # (            "hash": "gMjpC12Ffz+CMhyvm+/jdYJmqbPdgAhHJvvGBABYhCI=",)

[//]: # (            "reference": {)

[//]: # (                "transaction": "chg_TS07A5220231433Ql241910314")

[//]: # (            },)

[//]: # (            "merchant": {)

[//]: # (                "id": "00000101")

[//]: # (            },)

[//]: # (            "application": {)

[//]: # (                "id": "4530082749")

[//]: # (            },)

[//]: # (            "configuration": {)

[//]: # (                "show_result": "0",)

[//]: # (                "hide_mobile_qr": "0",)

[//]: # (                "frequency": {)

[//]: # (                    "start": 120,)

[//]: # (                    "interval": 60,)

[//]: # (                    "count": 10,)

[//]: # (                    "type": "SECOND")

[//]: # (                })

[//]: # (            })

[//]: # (        })

[//]: # (    })

[//]: # (})

[//]: # ()
[//]: # (})

[//]: # (``` )
