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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.agrapana.fertigation.R
import com.agrapana.fertigation.ui.activity.MainActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class InternetDisruption: Service() {

    private lateinit var primaryDeviceReference: DatabaseReference
    private lateinit var intervalReference: DatabaseReference
    private lateinit var prefs: SharedPreferences
    private lateinit var ownerId: String
    private lateinit var fieldId: String
    private var NOTIFICATION_CHANNEL_ID = "kota203.fertigation.notification"
    private val NOTIFICATION_ID = 100
    private val interval = 300000
    private val handler = Handler()

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

    override fun onCreate() {
        super.onCreate()
        prefs = this.getSharedPreferences("prefs",
            AppCompatActivity.MODE_PRIVATE
        )!!
        ownerId = prefs.getString("client_id", "")!!
        ownerId = prefs.getString("client_id", "")!!
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        fetchDataFromFirebase()
        return START_NOT_STICKY
    }

    private fun fetchDataFromFirebase() {
        primaryDeviceReference = FirebaseDatabase.getInstance().getReference("path/to/data")
        primaryDeviceReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (childSnapshot in dataSnapshot.children) {
                    val data = childSnapshot.getValue(String::class.java)
                    // Lakukan sesuatu dengan data yang diterima
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
        handler.postDelayed({
            fetchDataFromFirebase()
        }, interval.toLong())
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

}