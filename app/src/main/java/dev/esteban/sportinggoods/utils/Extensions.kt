package dev.esteban.sportinggoods.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

fun String.getUrlImage(): String {
    return "https://openweathermap.org/img/wn/$this@2x.png"
}

fun Double.getFormattedSpeed(): String {
    return "$this mph"
}

fun Double.toDegree(): String {
    return "$this°"
}

fun Double.toFahrenheitDegree(): String {
    return "$this°F"
}

fun Calendar.getStartOfCurrentDay(): Long {
    this.apply {
        timeInMillis = System.currentTimeMillis()
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        return timeInMillis
    }
}

fun Calendar.getEndOfCurrentDay(): Long {
    this.apply {
        timeInMillis = System.currentTimeMillis()
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        return timeInMillis
    }
}

fun Int.getWindDirection(): String {
    val directions = arrayOf("N", "NE", "E", "SE", "S", "SW", "W", "NW", "N")
    val index = (this.toDouble() % 360 / 45).toInt()
    return directions[index]
}

fun Long.getHourFormattedDate(context: Context): String {
    return try {
        val sdf = SimpleDateFormat("hh:mm a", context.resources.configuration.locales.get(0))
        sdf.timeZone = TimeZone.getDefault()
        val date = Date(this * 1000)
        sdf.format(date)
    } catch (e: Exception) {
        e.toString()
    }
}

fun Long.getFormattedDate(context: Context): String {
    return try {
        val sdf = SimpleDateFormat("EE ,MMMM dd", context.resources.configuration.locales.get(0))
        sdf.timeZone = TimeZone.getDefault()
        val date = Date(this * 1000)
        sdf.format(date)
    } catch (e: Exception) {
        e.toString()
    }
}

fun Context.findActivity(): Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    throw IllegalStateException("Permissions should be called in the context of an Activity")
}
