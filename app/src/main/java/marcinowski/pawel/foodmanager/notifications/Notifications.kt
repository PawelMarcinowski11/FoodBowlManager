package marcinowski.pawel.foodmanager.notifications

import android.Manifest
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.datastore.preferences.core.booleanPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import marcinowski.pawel.foodmanager.MainActivity
import marcinowski.pawel.foodmanager.R
import marcinowski.pawel.foodmanager.dataStore
import marcinowski.pawel.foodmanager.storage.Products
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

/**
 * Food notifications class, handling scheduling and sending Android notifications
 *
 */
class FoodNotifications : BroadcastReceiver() {

    private val REQUEST_LOCK_SCREEN_PERMISSION = 300

    override fun onReceive(context: Context, intent: Intent) {
        CoroutineScope(Dispatchers.IO).launch {
            sendNotification(context)
        }
    }

    /**
     * Set notifications scheduled at the provided time of day
     *
     */
    fun setNotifications(context: Context, hourTime: Int) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_NOTIFICATION_POLICY
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.ACCESS_NOTIFICATION_POLICY),
                REQUEST_LOCK_SCREEN_PERMISSION
            )
        }
        var date = LocalDateTime.now()
        if (date.hour >= hourTime)
            date = date.plusDays(1)
        date = date.withHour(hourTime)
            .withMinute(5)
        val notifyIntent = Intent(
            context.applicationContext,
            FoodNotifications::class.java
        )
        notifyIntent.flags = Intent.FLAG_INCLUDE_STOPPED_PACKAGES
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            notifyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager?
        alarmManager!!.setRepeating(
            AlarmManager.RTC_WAKEUP,
            date.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    private suspend fun sendNotification(context: Context) {
        val currentDate = LocalDate.now()
        val daysLeft = 3
        val notifyOnShortDate = context.dataStore.data
            .map { settings ->
                settings[booleanPreferencesKey("notifyOnShortDate")] ?: false
            }.first()
        val notifyDaily = context.dataStore.data
            .map { settings ->
                settings[booleanPreferencesKey("notifyDaily")] ?: false
            }.first()
        val productList =
            if (notifyOnShortDate)
                Products(context).getProducts().first().filter { product -> ChronoUnit.DAYS.between(currentDate,product.expiryDate) < daysLeft }
            else
                Products(context).getProducts().first()
        val titleText = context.getString(R.string.notification_main_title)
        val subtitleText: String
        var contentText = ""
        if (notifyDaily) {
            subtitleText = context.getString(R.string.notification_title_daily_reminder)
            contentText += subtitleText
            if (productList.isNullOrEmpty()) {
                contentText += "\n" + context.getString(R.string.notification_content_daily_no_products)
            }
            else {
                contentText += "\n" + context.getString(R.string.notification_content_daily_reminder)
                productList.subList(0,3).forEach { contentText += "\n" + "- " + it.name}
            }
        }
        else if (notifyOnShortDate && !productList.isNullOrEmpty()) {
            subtitleText = context.getString(R.string.notification_title_short_date)
            contentText += subtitleText
            contentText += "\n" + context.getString(R.string.notification_content_short_expiry_date)
            productList.subList(0,3).forEach { contentText += "\n" + "- " + it.name}
            if (productList.size > 3)
                contentText += "\n" + context.getString(R.string.notification_content_short_date_more, productList.size - 3)
        }
        else
            return
        val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent =
            PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val NOTIFICATION_CHANNEL_ID = "101"
        val notificationChannel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "Notification",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        notificationManager.createNotificationChannel(notificationChannel)
        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setContentTitle(titleText)
                .setContentText(subtitleText)
                .setSmallIcon(R.drawable.ic_baseline_fastfood_24)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentIntent(pendingIntent)
                .setWhen(System.currentTimeMillis())
                .setStyle(NotificationCompat.BigTextStyle()
                    .bigText(contentText))
        notificationManager.notify(2, notificationBuilder.build())
    }

    /**
     * Cancel scheduled notifications
     *
     */
    fun cancelNotifications(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }
}