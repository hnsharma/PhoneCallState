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

import com.google.android.gms.tasks.OnCanceledListener
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.BuildConfig
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
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



    fun addUserInfoLog(userLogInfo : CallLogInfo) {
        val mSnapshots : Task<QuerySnapshot> = firestore!!.collection(collectionPath).get();
        if (mSnapshots.isSuccessful()) {
            var found = true
            var callLog = mapOf("name" to userLogInfo.name,"number" to userLogInfo.number,"callType" to userLogInfo.callType,"duration" to userLogInfo.duration,"date" to userLogInfo.date);
            for (document in mSnapshots.getResult()!!) {
                found = false
               val  userLogInfoSave = document.toObject(CallLogInfo::class.java)


                if(!userLogInfo.equals(userLogInfoSave))
                {
                    firestore!!.collection(collectionPath).add(callLog).addOnCompleteListener(
                        OnCompleteListener {
                            println("added")
                        }).addOnFailureListener(OnFailureListener {
                        it.printStackTrace()
                    }).addOnCanceledListener(OnCanceledListener {
                        println("cancel")
                    })
                }
                break
            }
            if(found)
            {
                firestore!!.collection(collectionPath).add(callLog).addOnCompleteListener(
                    OnCompleteListener {
                        println("added")
                    }).addOnFailureListener(OnFailureListener {
                        it.printStackTrace()
                }).addOnCanceledListener(OnCanceledListener {
                    println("cancel")
                })
            }

        }


    }

    fun getCallLog(): ArrayList<CallLogInfo>? {
        val callLIst  = ArrayList<CallLogInfo>()
        val mSnapshots : Task<QuerySnapshot> = firestore!!.collection(collectionPath).get();
        if (mSnapshots.isSuccessful()) {
            for (document in mSnapshots.getResult()!!) {
                val  userLogInfoSave = document.toObject(CallLogInfo::class.java)
                callLIst.add(userLogInfoSave)
            }
        }
        println("log size "+callLIst.size)

        return callLIst;
    }
}