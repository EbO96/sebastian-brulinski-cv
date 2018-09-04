package cv.brulinski.sebastian.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Simple object to hold email and password and valid this values
 */
data class Auth(val email: String, val password: String) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(email)
        parcel.writeString(password)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Auth> {
        override fun createFromParcel(parcel: Parcel): Auth {
            return Auth(parcel)
        }

        override fun newArray(size: Int): Array<Auth?> {
            return arrayOfNulls(size)
        }
    }

    /*
    Public methods
     */
    fun valid() = email.isNotBlank() && password.isNotBlank()

}