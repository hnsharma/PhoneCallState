package com.hn.phonestatedemo.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.widget.Toast
import com.hn.phonestatedemo.application.AppApplication
import java.util.*


class PhoneStateReceiver : BroadcastReceiver() {


    private var lastState = TelephonyManager.CALL_STATE_IDLE
    private var callStartTime: Date? = null
    private var isIncoming = false
    private var savedNumber //because the passed incoming is only valid in ringing
            : String? = null

    override fun onReceive(context: Context?, intent: Intent?) {

        CallLogUtils.getInstance()!!.loadData()
        (context!!.applicationContext as AppApplication).screenViewModel!!.refresh()

    }



}