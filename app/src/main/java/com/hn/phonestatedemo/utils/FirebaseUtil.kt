/**
 * Copyright 2021 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hn.phonestatedemo.utils

import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnCanceledListener
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.firestore.BuildConfig
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.hn.phonestatedemo.receiver.CallLogInfo


/**
 * Utility class for initializing Firebase services and connecting them to the Firebase Emulator
 * Suite if necessary.
 */
object FirebaseUtil {
    /** Use emulators only in debug builds  */
    private val sUseEmulators: Boolean = BuildConfig.DEBUG
    private var FIRESTORE: FirebaseFirestore? = null

    private const val collectionPath: String = "userLog"

    // Connect to the Cloud Firestore emulator when appropriate. The host '10.0.2.2' is a
    // special IP address to let the Android emulator connect to 'localhost'.
    private val firestore: FirebaseFirestore?
        private get() {
            if (FIRESTORE == null) {

                FIRESTORE = FirebaseFirestore.getInstance()


                // Connect to the Cloud Firestore emulator when appropriate. The host '10.0.2.2' is a
                // special IP address to let the Android emulator connect to 'localhost'.
                if (sUseEmulators) {
                    FIRESTORE!!.useEmulator("10.0.2.2", 8080)
                }
                // Connect to the Cloud Firestore emulator when appropriate. The host '10.0.2.2' is a
                // special IP address to let the Android emulator connect to 'localhost'.

            }
            return FIRESTORE
        }



    fun addUserInfoLog(userLogInfo: CallLogInfo, userCallLog: MutableLiveData<List<CallLogInfo>>) {
        var callLog = mapOf("name" to userLogInfo.name,"number" to userLogInfo.number,"callType" to userLogInfo.callType,"duration" to userLogInfo.duration,"date" to userLogInfo.date);

        firestore!!.collection(collectionPath).orderBy("date",Query.Direction.DESCENDING).get().addOnCompleteListener(OnCompleteListener {
            var found = true
            if (it.isSuccessful()) {

                var count = 0
                for (document in it.getResult()!!) {

                    val  userLogInfoSave = document.toObject(CallLogInfo::class.java)
                    count++;
                    if(userLogInfo.equals(userLogInfoSave))
                    {
                        found = false
                    }
                    if(count >=4)
                    break
                }


            }

            if(found)
            {
                firestore!!.collection(collectionPath).add(callLog).addOnCompleteListener(
                    OnCompleteListener {
                        getCallLog(userCallLog)
                        println("added")
                    }).addOnFailureListener(OnFailureListener {
                    it.printStackTrace()
                }).addOnCanceledListener(OnCanceledListener {
                    println("cancel")
                })
            }

        })




    }

    fun addUserInfoLogTest() {
            var callLog = mapOf("name" to "Hari","number" to "8290420991");
        firestore!!.collection(collectionPath).add(callLog).addOnCompleteListener(
            OnCompleteListener {
                println("added")
            }).addOnFailureListener(OnFailureListener {
            it.printStackTrace()
        }).addOnCanceledListener(OnCanceledListener {
            println("cancel")
        })
    }

    fun getCallLog(userCallLog: MutableLiveData<List<CallLogInfo>>) {
        val callLIst  = ArrayList<CallLogInfo>()
         firestore!!.collection(collectionPath).orderBy("date",Query.Direction.DESCENDING).get().addOnCompleteListener(
            OnCompleteListener {
                if(it.isSuccessful) {
                    for (document in it.getResult()!!) {
                        val userLogInfoSave = document.toObject(CallLogInfo::class.java)
                        callLIst.add(userLogInfoSave)
                    }
                    println("log size "+callLIst.size)
                    userCallLog.postValue(callLIst)
                }
            });



    }
}