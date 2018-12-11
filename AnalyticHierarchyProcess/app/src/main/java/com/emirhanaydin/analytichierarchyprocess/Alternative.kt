package com.emirhanaydin.analytichierarchyprocess

import android.os.Parcel
import android.os.Parcelable

class Alternative(name: String, rating: Int = 1, isReciprocal: Boolean = false) : AhpItem(
    name,
    rating,
    isReciprocal
), Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readInt() != 0
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeInt(rating)
        parcel.writeInt(if (isReciprocal) 1 else 0)
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