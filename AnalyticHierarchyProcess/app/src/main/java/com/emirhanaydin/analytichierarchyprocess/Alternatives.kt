package com.emirhanaydin.analytichierarchyprocess

import android.os.Parcel
import android.os.Parcelable

@Suppress("UNCHECKED_CAST")
class Alternatives(parentName: String, children: MutableList<Alternative>) : AhpGroup(
    parentName,
    children as MutableList<AhpItem>
), Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readParcelableArray(Alternative::class.java.classLoader)!!
            .filterIsInstance<Alternative>().toMutableList()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(parentName)
        parcel.writeParcelableArray(children.filterIsInstance<Alternative>().toTypedArray(), flags)
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