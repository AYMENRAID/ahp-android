package com.emirhanaydin.analytichierarchyprocess

import android.os.Parcel
import android.os.Parcelable

data class Alternatives(
    val parent: String,
    val children: MutableList<Alternative>
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readParcelableArray(Alternative::class.java.classLoader)!!
            .map { parcelable -> parcelable as Alternative }
            .toMutableList()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(parent)
        parcel.writeParcelableArray(children.toTypedArray(), flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Alternatives> {
        override fun createFromParcel(parcel: Parcel): Alternatives {
            return Alternatives(parcel)
        }

        override fun newArray(size: Int): Array<Alternatives?> {
            return arrayOfNulls(size)
        }
    }
}