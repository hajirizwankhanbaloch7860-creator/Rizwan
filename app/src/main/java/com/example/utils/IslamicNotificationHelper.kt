package com.example.utils

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import java.util.Calendar
import kotlin.random.Random

object IslamicNotificationHelper {
    const val CHANNEL_ID = "islamic_daily_reminder"
    const val CHANNEL_NAME = "Daily Quran & Hadith Reminders"
    const val NOTIFICATION_ID = 7860

    // List of inspiring Quran Verses and Hadiths with Urdu translation for Daily Reminders
    val reminders = listOf(
        ReminderItem(
            title = "قرآنی آیت (Quranic Verse)",
            arabic = "إِنَّ مَعَ الْعُسْرِ يُسْرًا",
            translation = "بے شک ہر مشکل کے ساتھ آسانی ہے۔",
            reference = "سورہ الشرح - 94:6"
        ),
        ReminderItem(
            title = "قرآنی آیت (Quranic Verse)",
            arabic = "ادْعُونِي أَسْتَجِبْ لَكُمْ",
            translation = "مجھ سے دعا کرو، میں قبول کروں گا۔",
            reference = "سورہ غافر - 40:60"
        ),
        ReminderItem(
            title = "قرآنی آیت (Quranic Verse)",
            arabic = "أَلَا بِذِكْرِ اللَّهِ تَطْمَئِنُّ الْقُلُوبُ",
            translation = "سنو! اللہ کے ذکر ہی سے دلوں کو اطمینان نصیب ہوتا ہے۔",
            reference = "سورہ الرعد - 13:28"
        ),
        ReminderItem(
            title = "قرآنی آیت (Quranic Verse)",
            arabic = "وَاصْبِرْ لِحُكْمِ رَبِّكَ فَإِنَّكَ بِأَعْيُنِنَا",
            translation = "اور اپنے رب کے حکم کے لیے صبر کرو، کیونکہ تم یقیناً ہماری آنکھوں کے سامنے ہو۔",
            reference = "سورہ الطور - 52:48"
        ),
        ReminderItem(
            title = "قرآنی آیت (Quranic Verse)",
            arabic = "وَمَن يَتَوَكَّلْ عَلَى اللَّهِ فَهُوَ حَسْبُهُ",
            translation = "اور جو اللہ پر بھروسہ کرے تو وہ اس کے لیے کافی ہے۔",
            reference = "سورہ الطلاق - 65:3"
        ),
        ReminderItem(
            title = "حدیثِ مبارکہ (Noble Hadith)",
            arabic = "الدِّينُ النَّصِيحَةُ",
            translation = "دین سراسر خیر خواہی کا نام ہے۔",
            reference = "صحیح بخاری - کتاب الایمان"
        ),
        ReminderItem(
            title = "حدیثِ مبارکہ (Noble Hadith)",
            arabic = "إِنَّمَا الأَعْمَالُ بِالنِّيَّاتِ",
            translation = "اعمال کا دارومدار صرف نیتوں پر ہے۔",
            reference = "صحیح بخاری - کتاب بدء الوحی"
        ),
        ReminderItem(
            title = "حدیثِ مبارکہ (Noble Hadith)",
            arabic = "الطَّهُورُ شَطْرُ الإِيمَانِ",
            translation = "پاکیزگی اور صفائی آدھا ایمان ہے۔",
            reference = "صحیح مسلم - کتاب الطهارة"
        ),
        ReminderItem(
            title = "حدیثِ مبارکہ (Noble Hadith)",
            arabic = "المُسْلِمُ مَنْ سَلِمَ المُسْلِمُونَ مِنْ لِسَانِهِ وَيَدِهِ",
            translation = "سچا مسلمان وہ ہے جس کی زبان اور ہاتھ سے دوسرے مسلمان محفوظ رہیں۔",
            reference = "صحیح بخاری"
        ),
        ReminderItem(
            title = "حدیثِ مبارکہ (Noble Hadith)",
            arabic = "مَنْ سَلَكَ طَرِيقًا يَلْتَمِسُ فِيهِ عِلْمًا سَهَّلَ اللَّهُ لَهُ طَرِيقًا إِلَى الْجَنَّةِ",
            translation = "جو شخص علم کی تلاش میں کسی راستے پر چلے گا، اللہ اس کے لیے جنت کا راستہ آسان فرما دے گا۔",
            reference = "صحیح مسلم"
        ),
        ReminderItem(
            title = "قرآنی آیت (Quranic Verse)",
            arabic = "إِنَّ اللَّهَ مَعَ الصَّابِرِينَ",
            translation = "بے شک اللہ صبر کرنے والوں کے ساتھ ہے۔",
            reference = "سورہ البقرہ - 1:153"
        ),
        ReminderItem(
            title = "حدیثِ مبارکہ (Noble Hadith)",
            arabic = "خَيْرُكُمْ مَنْ تَعَلَّمَ الْقُرْآنَ وَعَلَّمَهُ",
            translation = "تم میں سے بہترین شخص وہ ہے جو قرآن سیکھے اور دوسروں کو سکھائے۔",
            reference = "صحیح بخاری"
        )
    )

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = "Daily inspirational verses from Quran and authentic Hadiths with Urdu translation."
                enableLights(true)
                enableVibration(true)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showNotification(context: Context, item: ReminderItem) {
        createNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, pendingIntentFlags)

        // Render beautiful expanded BigTextStyle notification
        val bigText = "${item.arabic}\n\n${item.translation}\n(${item.reference})"

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_today) // Native elegant icon
            .setContentTitle(item.title)
            .setContentText("${item.arabic} - ${item.translation}")
            .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    fun scheduleDailyReminder(context: Context, hour: Int, minute: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, DailyReminderReceiver::class.java)
        
        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        
        val pendingIntent = PendingIntent.getBroadcast(context, 100, intent, pendingIntentFlags)

        // Set time for Alarm
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            
            // If the time has already passed today, schedule for tomorrow
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            }
            
            // Save settings to SharedPreferences
            val prefs = context.getSharedPreferences("islamic_notification_settings", Context.MODE_PRIVATE)
            prefs.edit().apply {
                putBoolean("enabled", true)
                putInt("hour", hour)
                putInt("minute", minute)
                apply()
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    fun cancelDailyReminder(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, DailyReminderReceiver::class.java)
        
        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        
        val pendingIntent = PendingIntent.getBroadcast(context, 100, intent, pendingIntentFlags)
        alarmManager.cancel(pendingIntent)

        val prefs = context.getSharedPreferences("islamic_notification_settings", Context.MODE_PRIVATE)
        prefs.edit().apply {
            putBoolean("enabled", false)
            apply()
        }
    }
}

data class ReminderItem(
    val title: String,
    val arabic: String,
    val translation: String,
    val reference: String
)

class DailyReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null) return

        // Pick a random reminder
        val randomIndex = Random.nextInt(IslamicNotificationHelper.reminders.size)
        val reminder = IslamicNotificationHelper.reminders[randomIndex]

        // Show the notification
        IslamicNotificationHelper.showNotification(context, reminder)

        // Reschedule alarm for the next day
        val prefs = context.getSharedPreferences("islamic_notification_settings", Context.MODE_PRIVATE)
        val enabled = prefs.getBoolean("enabled", true)
        if (enabled) {
            val hour = prefs.getInt("hour", 8)
            val minute = prefs.getInt("minute", 0)
            IslamicNotificationHelper.scheduleDailyReminder(context, hour, minute)
        }
    }
}
