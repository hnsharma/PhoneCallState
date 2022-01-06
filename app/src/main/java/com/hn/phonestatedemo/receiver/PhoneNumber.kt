package com.hn.phonestatedemo.receiver

import android.telephony.PhoneNumberUtils
import java.util.*

class PhoneNumber(var number: String?,) {


    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as PhoneNumber
        return PhoneNumberUtils.compare(number,that.number)
    }

    override fun hashCode(): Int {
        return Objects.hash( number)
    }
}