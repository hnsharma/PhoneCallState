package com.hn.phonestatedemo.application

import android.app.Application
import com.hn.phonestatedemo.viewmodel.ScreenViewModel

open class AppApplication : Application(){
    var screenViewModel:ScreenViewModel? = null;
}