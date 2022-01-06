package com.hn.phonestatedemo.receiver

import com.hn.phonestatedemo.receiver.CallLogInfo
import android.annotation.SuppressLint
import android.provider.CallLog
import android.content.ContentResolver
import android.content.Context
import android.util.Log
import com.hn.phonestatedemo.receiver.CallLogUtils
import java.util.ArrayList

class CallLogUtils private constructor(private val context: Context) {

    private var mainList: ArrayList<CallLogInfo>? = null
    private var phoneList: ArrayList<PhoneNumber>? = null
    init {
        phoneList  = ArrayList()
        phoneList!!.add(PhoneNumber("8290420991")) // Add mobile number
    }
    @SuppressLint("Range")
     fun loadData() {

        val projection = arrayOf(
            "_id",
            CallLog.Calls.NUMBER,
            CallLog.Calls.DATE,
            CallLog.Calls.DURATION,
            CallLog.Calls.TYPE,
            CallLog.Calls.CACHED_NAME
        )
        val contentResolver = context.applicationContext.contentResolver
        val cursor = contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            projection,
            null,
            null,
            CallLog.Calls.DEFAULT_SORT_ORDER
        )
        if (cursor == null) {
            Log.d("CALLLOG", "cursor is null")
            return
        }
        if (cursor.count == 0) {
            Log.d("CALLLOG", "cursor size is 0")
            return
        }
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            val callLogInfo = CallLogInfo(cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME)),
            cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER)),
            cursor.getString(cursor.getColumnIndex(CallLog.Calls.TYPE)),
            cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DATE)),
           cursor.getLong(cursor.getColumnIndex(CallLog.Calls.DURATION)))
            if(phoneList!!.contains(PhoneNumber(callLogInfo.number))) {
                if (mainList == null) mainList = ArrayList()
                if (!mainList!!.contains(callLogInfo)) {
                    println("Add contect")
                    mainList!!.add(callLogInfo)
                } else {
                    println("already Added")
                }
            }

            break
            //cursor.moveToNext()
        }
        cursor.close()
    }

    fun readCallLogs(): ArrayList<CallLogInfo>? {
        if (mainList == null)
            mainList  = ArrayList()
       var tempList: ArrayList<CallLogInfo> = ArrayList()
            for(call: CallLogInfo in mainList!!)
            {
                tempList.add(0,call)
            }
        return tempList
    }


    fun getAllCallState(number: String): LongArray {
        val output = LongArray(2)
        for (callLogInfo in mainList!!) {
            if (callLogInfo.number == number) {
                output[0]++
                if (callLogInfo.callType!!.toInt() != CallLog.Calls.MISSED_TYPE) output[1] += callLogInfo.duration!!
            }
        }
        return output
    }


    companion object {
        lateinit var context: Context;
        private var instance: CallLogUtils? = null
        fun getInstance(): CallLogUtils? {
             if(instance == null)
            instance = CallLogUtils(context)
            return instance
        }
    }
}