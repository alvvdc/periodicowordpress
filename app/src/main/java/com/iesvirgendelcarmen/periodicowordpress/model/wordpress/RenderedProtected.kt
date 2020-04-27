package com.iesvirgendelcarmen.periodicowordpress.model.wordpress

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RenderedProtected(val rendered: String, val protected: String): Parcelable