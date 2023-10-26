package company.tap.tapWebForm.open.web_wrapper.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ThreeDsResponse(var id: String, var url: String,var powered:Boolean) :
    Parcelable