package com.hn.phonestatedemo.receiver

import android.app.PendingIntent
import android.app.PendingIntent.CanceledException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.TelephonyManager
import com.google.firebase.FirebaseApp
import com.hn.phonestatedemo.service.ForegroundService
import java.util.*


class PhoneStateReceiver : BroadcastReceiver() {


    private var lastState = TelephonyManager.CALL_STATE_IDLE
    private var callStartTime: Date? = null
    private var isIncoming = false
    private var savedNumber //because the passed incoming is only valid in ringing
            : String? = null

    override fun onReceive(context: Context?, intent: Intent?) {

        FirebaseApp.initializeApp(context!!)
        CallLogUtils.context  = context!!

        if(!ForegroundService.serviceStared) {
            Intent(context, ForegroundService::class.java).also {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(it)
                    return
                }
                context.startService(it)
            }
        }
        val userLogIntent = Intent(context, ForegroundService::class.java)
        val userPendingLogIntent = PendingIntent.getService(context,
            0,
            userLogIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        try {
            userPendingLogIntent.send()
        } catch (e: CanceledException) {
            e.printStackTrace()
        }

    }



}