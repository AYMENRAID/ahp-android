package com.emirhanaydin.analytichierarchyprocess

import android.os.Parcel
import android.os.Parcelable

class Alternative(name: String, rating: Int = 1) : AhpItem(
    name,
    rating
), Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeInt(rating)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Alternative> {
        override fun createFromParcel(parcel: Parcel): Alternative {
            return Alternative(parcel)
        }

        override fun newArray(size: Int): Array<Alternative?> {
            return arrayOfNulls(size)
        }
    }
}