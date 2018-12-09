package com.emirhanaydin.analytichierarchyprocess

import android.os.Parcel
import android.os.Parcelable

class Criterion(
    val name: String,
    var rating: Int = 1
) : Parcelable {
    val alternativeRatings: MutableList<Int> = mutableListOf()

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

    companion object CREATOR : Parcelable.Creator<Criterion> {
        override fun createFromParcel(parcel: Parcel): Criterion {
            return Criterion(parcel)
        }

        override fun newArray(size: Int): Array<Criterion?> {
            return arrayOfNulls(size)
        }
    }
}