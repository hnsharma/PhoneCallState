package com.hn.phonestatedemo.receiver

import android.telephony.PhoneNumberUtils
import com.hn.phonestatedemo.receiver.CallLogInfo
import java.util.*

data class CallLogInfo(var name: String?,
                       var number: String?,
                       var callType: String?,
                       var date: Long?,
                       var duration: Long?,) {

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as CallLogInfo
        return date == that.date && duration == that.duration && name == that.name &&PhoneNumberUtils.compare(number,that.number)  && callType == that.callType
    }

    override fun hashCode(): Int {
        return Objects.hash(name, number, callType, date, duration)
    }
}