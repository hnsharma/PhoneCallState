package com.hn.phonestatedemo.utils

object Utils {
    fun formatSeconds(timeInSeconds: Long): String {
        val hours = (timeInSeconds / 3600).toInt()
        val secondsLeft = (timeInSeconds - hours * 3600).toInt()
        val minutes = secondsLeft / 60
        val seconds = secondsLeft - minutes * 60
        var formattedTime = ""
        /*if (hours < 10)
                formattedTime += "0";
            formattedTime += hours + ":";*/if (hours > 0) formattedTime += hours.toString() + "h "
        formattedTime += minutes.toString() + "m "
        formattedTime += seconds.toString() + "s"

        /*if (minutes < 10)
                formattedTime += "0";
            formattedTime += minutes + ":";

            if (seconds < 10)
                formattedTime += "0";
            formattedTime += seconds ;*/return formattedTime
    }
}