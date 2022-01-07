package com.hn.phonestatedemo

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.provider.CallLog
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.FirebaseApp
import com.hn.phonestatedemo.application.AppApplication
import com.hn.phonestatedemo.receiver.CallLogInfo
import com.hn.phonestatedemo.receiver.CallLogUtils
import com.hn.phonestatedemo.service.ForegroundService
import com.hn.phonestatedemo.ui.theme.PhoneStateDemoTheme
import com.hn.phonestatedemo.utils.FirebaseUtil
import com.hn.phonestatedemo.utils.Utils
import com.hn.phonestatedemo.viewmodel.ScreenViewModel
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : ComponentActivity() {
    lateinit var screenViewModel : ScreenViewModel
    // Variable for storing instance of our service class
    var mService: ForegroundService? = null
    var mIsBound: Boolean? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        CallLogUtils.context  = this;
        setContent {
            PhoneStateDemoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    screenViewModel= viewModel()
                    (application as AppApplication).screenViewModel = screenViewModel;
                    callList(screenViewModel,screenViewModel.refresh(screenViewModel.list))
                }
            }
        }
       // FirebaseUtil.addUserInfoLogTest()
    }
    override fun onStart() {
        super.onStart()
        bindService()
        val phoneReadStatePermission = applicationContext.checkSelfPermission("READ_PHONE_STATE")
        val readCallLogPermission = applicationContext.checkSelfPermission("READ_CALL_LOG")
        val hasPhoneReadStatePermission =
            phoneReadStatePermission == PackageManager.PERMISSION_GRANTED
        val hasReadCallLogPermission = readCallLogPermission == PackageManager.PERMISSION_GRANTED
        if (!hasPhoneReadStatePermission || !hasReadCallLogPermission) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.READ_CALL_LOG
                ),
                1
            )
        }
    }

    /**
     * Interface for getting the instance of binder from our service class
     * So client can get instance of our service class and can directly communicate with it.
     */
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, iBinder: IBinder) {
            Log.d("Tag", "ServiceConnection: connected to service.")
            // We've bound to MyService, cast the IBinder and get MyBinder instance
            val binder = iBinder as ForegroundService.MyBinder
            mService = binder.service
            mIsBound = true
            changeCallLog() // return a random number from the service
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            Log.d("Tag", "ServiceConnection: disconnected from service.")
            mIsBound = false
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService()
    }

    private fun changeCallLog() {
        mService?.userCallLog?.observe(this, androidx.lifecycle.Observer {
            screenViewModel.refresh(it)
        })
    }


    /**
     * Used to bind to our service class
     */
    private fun bindService() {
        Intent(this, ForegroundService::class.java).also { intent ->
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    /**
     * Used to unbind and stop our service class
     */
    private fun unbindService() {
        Intent(this, ForegroundService::class.java).also { intent ->
            unbindService(serviceConnection)
        }
    }

}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    PhoneStateDemoTheme {
        Greeting("Android")
    }
}

@Composable
fun callList(screenViewModel: ScreenViewModel, refresh: Unit) {
    LazyColumn(contentPadding = PaddingValues(bottom = 16.dp)) {
        println("state  start")
        items(screenViewModel!!.list) { item ->
            CallDetail(item = item)
        }
    }
}



@Composable
fun CallDetail(
    item: CallLogInfo?
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp)
    ) {
        Row(Modifier) {
            Image(
                modifier = Modifier.size(50.dp,50.dp).padding(10.dp),
                painter = painterResource(id = R.drawable.profile_picture),
                contentDescription = null ,// decorative element
            )


        Column(modifier = Modifier) {
            var name  = item?.name ?: ""
                if (name.trim()?.isNotEmpty()) {
                    Text(
                        text = item?.name ?: "",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.subtitle1,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            if (item?.number?.trim()?.isNotEmpty() == true)
                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Text(
                        text = item!!.number!!.trim(),
                        textAlign = TextAlign.Start,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.caption,
                    )
                }
            var idIcon = R.drawable.ic_outgoing
            var idColor =  R.color.green
            when (item!!.callType!!.toInt()) {
                CallLog.Calls.OUTGOING_TYPE -> {
                    idIcon = R.drawable.ic_outgoing
                     idColor =  R.color.green
                }
                CallLog.Calls.INCOMING_TYPE -> {
                    idIcon = R.drawable.ic_missed
                     idColor =  R.color.blue
                }
                CallLog.Calls.MISSED_TYPE -> {
                     idColor =  R.color.red
                    idIcon = R.drawable.ic_missed
                }
            }
            Image(
                modifier = Modifier.size(15.dp,15.dp),
                painter = painterResource(id = idIcon),
                contentDescription = null ,// decorative element
                colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(colorResource(id = idColor))
            )

            val dateObj: Date = Date(item!!.date!!)
            val formatter = SimpleDateFormat("dd-MMM-yyyy   hh:mm a")
            Text(
                text = formatter.format(dateObj),
                textAlign = TextAlign.Start,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.caption,
            )
            Text(
                text = Utils.formatSeconds(item!!.duration!!),
                textAlign = TextAlign.Start,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.caption,
            )
        }
        }
    }
}


