package company.tap.tapWebForm.open.web_wrapper.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TapButtonSDKConfigUrlResponse(    val baseURL: String,
                                             val testEncKey: String,
                                             val prodEncKey: String,
                                             val payButtonUrlFormat: String,
                                             val redirectionKeyWord: String,
                                             val iOSFirebaseURL: String,
                                             val iOSFireBaseJS: String,
):Parcelable
