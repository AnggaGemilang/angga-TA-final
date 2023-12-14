package com.agrapana.fertigation.helper

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.agrapana.fertigation.R
import com.agrapana.fertigation.model.Controlling
import com.agrapana.fertigation.model.IntervalPreset
import com.agrapana.fertigation.model.MonitoringPrimaryDevice
import com.agrapana.fertigation.ui.activity.MainActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


class InternetDisruption: Service() {

    private lateinit var monitoringReference: DatabaseReference
    private lateinit var controllingReference: DatabaseReference
    private lateinit var fieldReference: DatabaseReference
    private lateinit var prefs: SharedPreferences
    private lateinit var ownerId: String
    private lateinit var scheduler: ScheduledExecutorService
    private var NOTIFICATION_CHANNEL_ID = "kota203.fertigation.notification"
    private val NOTIFICATION_ID = 100
    private val interval = 1800

    private fun showNotification(
        context: Context,
        title: String?,
        message: String?
    ) {
        val ii = Intent(context, MainActivity::class.java)
        ii.data = Uri.parse("custom://" + System.currentTimeMillis())
        ii.action = "actionstring" + System.currentTimeMillis()
        ii.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        val pi = PendingIntent.getActivity(context, 0, ii, PendingIntent.FLAG_MUTABLE)
        val notification: Notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setOngoing(true)
                .setSmallIcon(getNotificationIcon())
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pi)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setWhen(System.currentTimeMillis())
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentTitle(title).build()
            val notificationManager = context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                title,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(notificationChannel)
            notificationManager.notify(NOTIFICATION_ID, notification)
        } else {
            notification = NotificationCompat.Builder(context)
                .setSmallIcon(getNotificationIcon())
                .setAutoCancel(true)
                .setContentText(message)
                .setContentIntent(pi)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentTitle(title).build()
            val notificationManager = context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager
            notificationManager.notify(NOTIFICATION_ID, notification)
        }
    }

    private fun getNotificationIcon(): Int {
        val useWhiteIcon =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
        return if (useWhiteIcon) R.drawable.logo4 else R.drawable.logo4
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        scheduler = Executors.newScheduledThreadPool(1)
        val runnableTask = Runnable {
            fetchDataFromFirebase()
        }
        scheduler.scheduleAtFixedRate(runnableTask, 0, interval.toLong(), TimeUnit.SECONDS)
        return START_NOT_STICKY
    }

    private fun fetchDataFromFirebase() {
        prefs = this.getSharedPreferences("prefs",
            AppCompatActivity.MODE_PRIVATE
        )!!
        ownerId = prefs.getString("client_id", "")!!
        fieldReference = FirebaseDatabase.getInstance().getReference("fields")
        fieldReference.child(ownerId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (fieldSnapshot in dataSnapshot.children) {
                    if(fieldSnapshot.exists()){
                        controllingReference = FirebaseDatabase.getInstance().getReference("controlling")
                        controllingReference.child(ownerId).child("interval").addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                val userRequest = dataSnapshot.getValue(IntervalPreset::class.java)!!.userRequest
                                monitoringReference = FirebaseDatabase.getInstance().getReference("monitoring")
                                monitoringReference.child(ownerId).child(fieldSnapshot.key.toString()).child("primaryDevice").addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                        for (primaryDeviceSnapshot in dataSnapshot.children) {
                                            val takeAt = primaryDeviceSnapshot.getValue(MonitoringPrimaryDevice::class.java)!!.takenAt
                                            val localDateTime = LocalDateTime.parse(takeAt, DateTimeFormatter.ofPattern("dd:MM:yyyy HH:mm"))
                                            val localTime = localDateTime.toLocalTime()
                                            val localTimeAfter = localTime.plus(userRequest.toLong()/60, ChronoUnit.MINUTES)
                                            val localTimeNow = LocalDateTime.now(ZoneId.of("Asia/Jakarta")).toLocalTime()
                                            Log.d("sabihis", localTimeNow.toString())
                                            Log.d("sabihis", localTimeAfter.toString())
                                            if((localTimeNow.hour < 12 && localTimeAfter.hour >= 12) || (localTimeAfter.hour < 12 && localTimeNow.hour >= 12)
                                                || localTimeAfter < localTimeNow){
                                                showNotification(applicationContext, "Kemungkinkan terdapat gangguan internet pada alat", "Masih belum ada pembaharuan baru")
                                            }
                                        }
                                    }
                                    override fun onCancelled(databaseError: DatabaseError) {}
                                })
                            }
                            override fun onCancelled(databaseError: DatabaseError) {}
                        })
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

}