package com.iesvirgendelcarmen.periodicowordpress.model.wordpress

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class User(
    val id: Int,
    val name: String,
    val url: String,
    val description: String,
    val slug: String,
    @SerializedName("avatar_urls")
    val avatarUrls: AvatarUrls
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readParcelable(AvatarUrls::class.java.classLoader)!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(url)
        parcel.writeString(description)
        parcel.writeString(slug)
        parcel.writeParcelable(avatarUrls, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}


data class AvatarUrls(
    @SerializedName("24")
    val size24: String,
    @SerializedName("48")
    val size48: String,
    @SerializedName("96")
    val size96: String
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(size24)
        parcel.writeString(size48)
        parcel.writeString(size96)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AvatarUrls> {
        override fun createFromParcel(parcel: Parcel): AvatarUrls {
            return AvatarUrls(parcel)
        }

        override fun newArray(size: Int): Array<AvatarUrls?> {
            return arrayOfNulls(size)
        }
    }
}