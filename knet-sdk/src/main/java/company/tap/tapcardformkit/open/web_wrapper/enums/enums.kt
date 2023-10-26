package company.tap.tapcardformkit.open.web_wrapper.enums


const val rawFolderRefrence = "raw"
const val knetWebPrefix = "tapknetwebsdk://"
const val benefitWebPrefix = "tapbenefitwebsdk://"

const val keyValueName = "data"
const val urlWebStarter = "https://button.dev.tap.company/wrapper/{url}?configurations="
const val publicKeyToGet ="publicKey"
const val HeadersApplication ="application"
const val HeadersMdn ="mdn"
const val operatorKey ="operator"
const val headersKey ="headers"
const val redirectKey ="redirect"
const val urlKey ="url"

enum class KnetStatusDelegate {
    onReady, onClick, onOrderCreated,onChargeCreated, onError, onSuccess,cancel
}
enum class SCHEMES(var value:Pair<String,String>){
    KNET(Pair(urlWebStarter.replace("{url}","knet"), knetWebPrefix)),
    BENEFIT(Pair(urlWebStarter.replace("{url}","benefit"), benefitWebPrefix))
}

enum class PayButtonTypes {
    KNET,BENEFIT,BENEFITPAY
}