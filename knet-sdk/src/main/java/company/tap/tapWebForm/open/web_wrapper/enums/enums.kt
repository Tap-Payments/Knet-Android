package company.tap.tapWebForm.open.web_wrapper.enums


const val rawFolderRefrence = "raw"
const val webPrefix = "tap{buttonType}websdk://"
const val keyValueName = "data"
const val urlWebStarter = "https://button.dev.tap.company/wrapper/{url}?configurations="
const val publicKeyToGet = "publicKey"
const val HeadersApplication = "application"
const val HeadersMdn = "mdn"
const val operatorKey = "operator"
const val headersKey = "headers"

const val redirectKey = "redirect"
const val urlKey = "url"

enum class KnetStatusDelegate {
    onReady, onClick, onOrderCreated, onChargeCreated, onError, onSuccess, cancel, onClosePopup
}

enum class SCHEMES(var value: Pair<String, String>) {
    KNET(Pair(urlWebStarter.replace("{url}", "knet"), webPrefix.replace("{buttonType}", "knet"))),
    BENEFIT(
        Pair(
            urlWebStarter.replace("{url}", "benefit"),
            webPrefix.replace("{buttonType}", "benefit")
        )
    ),
    FAWRY(
        Pair(
            urlWebStarter.replace("{url}", "fawry"),
            webPrefix.replace("{buttonType}", "fawry")
        )
    ),
    PAYPAL(
        Pair(
            urlWebStarter.replace("{url}", "paypal"),
            webPrefix.replace("{buttonType}", "paypal")
        )
    ),
    TABBY(
        Pair(
            urlWebStarter.replace("{url}", "tabby"),
            webPrefix.replace("{buttonType}", "tabby")
        )
    ),
    GOOGLE(
        Pair(
            urlWebStarter.replace("{url}", "googlepay"),
            webPrefix.replace("{buttonType}", "googlepay")
        )
    )

}

enum class ThreeDsPayButtonType {
    KNET, BENEFIT, BENEFITPAY, FAWRY, PAYPAL, TABBY, GOOGLEPAY
}