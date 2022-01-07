package com.hn.phonestatedemo.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hn.phonestatedemo.receiver.CallLogInfo
import com.hn.phonestatedemo.receiver.CallLogUtils
import kotlinx.coroutines.launch

class ScreenViewModel:ViewModel() {
    var list by mutableStateOf(emptyList<CallLogInfo>())
    var mutableList = mutableStateListOf<CallLogInfo>()
    var isRefreshing by mutableStateOf(false)

    init {
        //refresh()
    }

    /*fun refresh() {
        isRefreshing = true
        viewModelScope.launch {
            list = getCallData()
            mutableList.addAll(0, list)
            isRefreshing = false
        }
    }*/

    fun refresh(userListData: List<CallLogInfo> ) {
        isRefreshing = true
        viewModelScope.launch {
            list =userListData
            mutableList.addAll(0, list)
            isRefreshing = false
        }
    }

    suspend fun getCallData(): List<CallLogInfo> {
        return CallLogUtils.getInstance()!!.readCallLogs()!!
        // emulate network call
    }
}