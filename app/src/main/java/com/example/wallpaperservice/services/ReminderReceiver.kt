package com.example.wallpaperservice.services

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import android.widget.Toast


class ReminderReceiver : BroadcastReceiver() {
    companion object {
        private const val REQUEST_TIMER1 = 1

        fun getIntent(context: Context, requestCode: Int): PendingIntent? {
            val intent = Intent(context, ReminderReceiver::class.java)
            // https://developer.android.com/reference/android/app/PendingIntent.html#getBroadcast(android.content.Context,%20int,%20android.content.Intent,%20int)
            return PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        }

        fun startAlarm(context: Context) {
            val pendingIntent = getIntent(context, REQUEST_TIMER1)
            val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager


//            // trigger at 8:30am
//            val alarmTime = LocalTime.of(11, 46)
//            var now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)
//            if (now.toLocalTime().isAfter(alarmTime)) {
//                now = now.plusDays(1)
//            }
//            now = now.withHour(alarmTime.hour).withMinute(alarmTime.minute) // .withSecond(alarmTime.second).withNano(alarmTime.nano)
//            val utc= now.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime()
//
//            val triggerAtMillis = utc.atZone(ZoneOffset.UTC)!!.toInstant()!!.toEpochMilli()
//            // first trigger at next 8:30am, then repeat each day
//            alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, triggerAtMillis, AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent)

            // this alarm might execute between now to next day, and repeat daily
            alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 10000, pendingIntent)
        }

        fun cancelAlarm(context: Context) {
            val pendingIntent = getIntent(context, REQUEST_TIMER1)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            Toast.makeText(context, "Canceled!", Toast.LENGTH_LONG).show()

            alarmManager.cancel(pendingIntent)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        //Toast.makeText(context, "Alarm!", Toast.LENGTH_LONG).show()

        val serviceIntent = Intent(context,BackgroundService::class.java)
        context.startService(serviceIntent)
    }
}